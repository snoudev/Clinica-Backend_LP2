package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.dto.CitaRequest;
import com.clinica.clinica_backend.dto.CitaResponse;
import com.clinica.clinica_backend.dto.DisponibilidadResponse;
import com.clinica.clinica_backend.dto.MedicoDisponibleResponse;
import com.clinica.clinica_backend.entity.Cita;
import com.clinica.clinica_backend.entity.Empleado;
import com.clinica.clinica_backend.entity.EstadoCita;
import com.clinica.clinica_backend.entity.Paciente;
import com.clinica.clinica_backend.entity.Usuario;
import com.clinica.clinica_backend.exception.ConflictoException;
import com.clinica.clinica_backend.exception.RecursoNoEncontradoException;
import com.clinica.clinica_backend.exception.SolicitudInvalidaException;
import com.clinica.clinica_backend.repository.CitaRepository;
import com.clinica.clinica_backend.repository.EmpleadoRepository;
import com.clinica.clinica_backend.repository.EstadoCitaRepository;
import com.clinica.clinica_backend.repository.PacienteRepository;
import com.clinica.clinica_backend.repository.UsuarioRepository;
import com.clinica.clinica_backend.dto.ReprogramarCitaRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CitaService {

	private final CitaRepository citaRepository;
	private final EmpleadoRepository empleadoRepository;
	private final PacienteRepository pacienteRepository;
	private final UsuarioRepository usuarioRepository;
	private final EstadoCitaRepository estadoCitaRepository;
	
	public CitaService(
	        CitaRepository citaRepository,
	        EmpleadoRepository empleadoRepository,
	        PacienteRepository pacienteRepository,
	        UsuarioRepository usuarioRepository,
	        EstadoCitaRepository estadoCitaRepository) {

	    this.citaRepository = citaRepository;
	    this.empleadoRepository = empleadoRepository;
	    this.pacienteRepository = pacienteRepository;
	    this.usuarioRepository = usuarioRepository;
	    this.estadoCitaRepository = estadoCitaRepository;
	}

	@Transactional
	public CitaResponse registrarCita(CitaRequest request) {

	    LocalDateTime fechaHora = request.fechaHoraCita();

	    if (fechaHora.isBefore(LocalDateTime.now())) {
	        throw new SolicitudInvalidaException(
	                "No se puede registrar una cita en el pasado");
	    }

	    if (!generarHorarios(fechaHora.toLocalDate()).contains(fechaHora)) {
	        throw new SolicitudInvalidaException(
	                "La hora seleccionada no corresponde a un horario disponible");
	    }

	    Paciente paciente = pacienteRepository.findById(request.idPaciente())
	            .orElseThrow(() -> new RecursoNoEncontradoException(
	                    "El paciente no existe"));

	    Empleado medico = empleadoRepository.findById(request.idMedico())
	            .orElseThrow(() -> new RecursoNoEncontradoException(
	                    "El médico no existe"));

	    if (!medico.isEstado()) {
	        throw new SolicitudInvalidaException(
	                "El médico no está activo");
	    }

	    if (medico.getCargo() == null
	            || !"MEDICO".equalsIgnoreCase(medico.getCargo().getNombreCargo())) {
	        throw new SolicitudInvalidaException(
	                "El empleado seleccionado no tiene el cargo de médico");
	    }

	    if (medico.getEspecialidad() == null) {
	        throw new SolicitudInvalidaException(
	                "El médico no tiene una especialidad asignada");
	    }

	    if (!estaEnTurno(medico, fechaHora)) {
	        throw new SolicitudInvalidaException(
	                "El horario está fuera del turno del médico");
	    }

	    List<Cita> citasCercanas =
	            citaRepository.findCitasActivasDelMedicoEnRango(
	                    medico.getIdEmpleado(),
	                    fechaHora.minusMinutes(45),
	                    fechaHora.plusMinutes(45));

	    if (estaOcupado(medico, fechaHora, citasCercanas)) {
	        throw new ConflictoException(
	                "El médico ya tiene una cita en ese horario");
	    }

	    Authentication autenticacion =
	            SecurityContextHolder.getContext().getAuthentication();

	    if (autenticacion == null
	            || !autenticacion.isAuthenticated()
	            || "anonymousUser".equals(autenticacion.getName())) {
	        throw new SolicitudInvalidaException(
	                "No se pudo identificar al usuario que registra la cita");
	    }

	    Usuario usuario = usuarioRepository.findByCorreo(
	                    autenticacion.getName())
	            .orElseThrow(() -> new RecursoNoEncontradoException(
	                    "El usuario autenticado no existe"));

	    EstadoCita estadoPendiente = estadoCitaRepository
	            .findByNombreEstado("PENDIENTE")
	            .orElseThrow(() -> new RecursoNoEncontradoException(
	                    "No existe el estado PENDIENTE"));

	    Cita cita = new Cita();

	    cita.setPaciente(paciente);
	    cita.setMedico(medico);
	    cita.setUsuarioRegistro(usuario);
	    cita.setEstadoCita(estadoPendiente);
	    cita.setFechaHoraCita(fechaHora);

	    return aRespuesta(citaRepository.save(cita));
	}
	

	public List<CitaResponse> listarCitas() {
	
	    Authentication autenticacion =
	            SecurityContextHolder.getContext().getAuthentication();
	
	    if (autenticacion == null
	            || !autenticacion.isAuthenticated()
	            || "anonymousUser".equals(autenticacion.getName())) {
	        throw new SolicitudInvalidaException(
	                "No se pudo identificar al usuario autenticado");
	    }
	
	    boolean esAdministrador = autenticacion.getAuthorities().stream()
	            .anyMatch(autoridad ->
	                    autoridad.getAuthority().equals("ROLE_ADMINISTRADOR"));
	
	    List<Cita> citas;
	
	    if (esAdministrador) {
	        citas = citaRepository.findAll();
	    } else {
	        Usuario usuario = usuarioRepository.findByCorreo(
	                        autenticacion.getName())
	                .orElseThrow(() -> new RecursoNoEncontradoException(
	                        "El usuario autenticado no existe"));
	
	        citas = citaRepository.findByUsuarioRegistroIdUsuario(
	                usuario.getIdUsuario());
	    }
	
	    return citas.stream()
	            .map(this::aRespuesta)
	            .toList();
	}
	
	public CitaResponse buscarCitaPorId(int idCita) {
	
	    Cita cita = citaRepository.findById(idCita)
	            .orElseThrow(() -> new RecursoNoEncontradoException(
	                    "La cita no existe"));
	
	    Authentication autenticacion =
	            SecurityContextHolder.getContext().getAuthentication();
	
	    if (autenticacion == null
	            || !autenticacion.isAuthenticated()
	            || "anonymousUser".equals(autenticacion.getName())) {
	        throw new SolicitudInvalidaException(
	                "No se pudo identificar al usuario autenticado");
	    }
	
	    boolean esAdministrador = autenticacion.getAuthorities().stream()
	            .anyMatch(autoridad ->
	                    autoridad.getAuthority().equals("ROLE_ADMINISTRADOR"));
	
	    if (!esAdministrador) {
	
	        Usuario usuario = usuarioRepository.findByCorreo(
	                        autenticacion.getName())
	                .orElseThrow(() -> new RecursoNoEncontradoException(
	                        "El usuario autenticado no existe"));
	
	        if (cita.getUsuarioRegistro().getIdUsuario()
	                != usuario.getIdUsuario()) {
	            throw new org.springframework.security.access.AccessDeniedException(
	                    "No tienes permiso para consultar esta cita");
	        }
	    }
	
	    return aRespuesta(cita);
	}


	@Transactional
	public CitaResponse cambiarEstadoCita(int idCita, String nombreEstado) {
	
	    Cita cita = citaRepository.findById(idCita)
	            .orElseThrow(() -> new RecursoNoEncontradoException(
	                    "La cita no existe"));
	
	    validarPermisoCita(cita);
	    
	    if (!"PENDIENTE".equals(cita.getEstadoCita().getNombreEstado())) {
	        throw new SolicitudInvalidaException(
	                "Solo se puede cambiar el estado de una cita pendiente");
	    }
	
	    if (!List.of("ATENDIDA", "CANCELADA", "NO ASISTIÓ")
	            .contains(nombreEstado)) {
	        throw new SolicitudInvalidaException(
	                "El estado solicitado no es válido");
	    }
	
	    if (("ATENDIDA".equals(nombreEstado)
	            || "NO ASISTIÓ".equals(nombreEstado))
	            && cita.getFechaHoraCita().isAfter(LocalDateTime.now())) {
	        throw new SolicitudInvalidaException(
	                "No se puede marcar como atendida o inasistencia una cita futura");
	    }
	
	    EstadoCita estado = estadoCitaRepository
	            .findByNombreEstado(nombreEstado)
	            .orElseThrow(() -> new RecursoNoEncontradoException(
	                    "El estado solicitado no existe"));
	
	    cita.setEstadoCita(estado);
	
	    return aRespuesta(citaRepository.save(cita));
	}


	@Transactional
	public CitaResponse reprogramarCita(int idCita, ReprogramarCitaRequest request) {
	
	    Cita cita = citaRepository.findById(idCita).orElseThrow(() -> new RecursoNoEncontradoException("La cita no existe"));
	
	    validarPermisoCita(cita);
	    
	    if (!"PENDIENTE".equals(cita.getEstadoCita().getNombreEstado())) {
	        throw new SolicitudInvalidaException(
	                "Solo se pueden reprogramar citas pendientes");
	    }
	
	    LocalDateTime nuevaFechaHora = request.fechaHoraCita();
	
	    if (nuevaFechaHora.isBefore(LocalDateTime.now())) {
	        throw new SolicitudInvalidaException(
	                "No se puede reprogramar una cita en el pasado");
	    }
	
	    if (!generarHorarios(nuevaFechaHora.toLocalDate())
	            .contains(nuevaFechaHora)) {
	        throw new SolicitudInvalidaException(
	                "La hora seleccionada no corresponde a un horario válido");
	    }
	
	    Empleado medicoOriginal = cita.getMedico();
	
	    Empleado nuevoMedico = empleadoRepository
	            .findById(request.idMedico())
	            .orElseThrow(() -> new RecursoNoEncontradoException(
	                    "El médico seleccionado no existe"));
	
	    if (!nuevoMedico.isEstado()) {
	        throw new SolicitudInvalidaException(
	                "El médico seleccionado no está activo");
	    }
	
	    if (nuevoMedico.getCargo() == null
	            || !"MEDICO".equalsIgnoreCase(
	                    nuevoMedico.getCargo().getNombreCargo())) {
	        throw new SolicitudInvalidaException(
	                "El empleado seleccionado no tiene el cargo de médico");
	    }
	
	    if (nuevoMedico.getEspecialidad() == null) {
	        throw new SolicitudInvalidaException(
	                "El médico seleccionado no tiene especialidad asignada");
	    }
	
	    if (!nuevoMedico.getEspecialidad().getIdEspecialidad()
	            .equals(medicoOriginal.getEspecialidad().getIdEspecialidad())) {
	        throw new SolicitudInvalidaException(
	                "El nuevo médico debe tener la misma especialidad");
	    }
	
	    if (!estaEnTurno(nuevoMedico, nuevaFechaHora)) {
	        throw new SolicitudInvalidaException(
	                "El horario está fuera del turno del médico");
	    }
	
	    List<Cita> citasCercanas =
	            citaRepository.findCitasActivasDelMedicoEnRangoExcluyendoCita(
	                    nuevoMedico.getIdEmpleado(),
	                    cita.getIdCita(),
	                    nuevaFechaHora.minusMinutes(45),
	                    nuevaFechaHora.plusMinutes(45));
	
	    if (estaOcupado(nuevoMedico, nuevaFechaHora, citasCercanas)) {
	        throw new ConflictoException(
	                "El médico ya tiene una cita en ese horario");
	    }
	
	    cita.setMedico(nuevoMedico);
	    cita.setFechaHoraCita(nuevaFechaHora);
	
	    return aRespuesta(citaRepository.save(cita));
	}

	
	public List<DisponibilidadResponse> consultarDisponibilidad(int idEspecialidad, LocalDate fecha) {

		LocalDateTime inicio = fecha.atStartOfDay();
		LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

		if (fecha.isBefore(LocalDate.now())) {
		    return List.of();
		}
		
		List<Empleado> medicos = empleadoRepository
				.findByCargoNombreCargoAndEstadoTrueAndEspecialidadIdEspecialidad("MEDICO", idEspecialidad);

		List<Cita> citas = citaRepository.findCitasDelDiaPorEspecialidad(idEspecialidad, inicio, fin);

		List<DisponibilidadResponse> disponibilidad = new ArrayList<>();

		List<LocalDateTime> horarios = generarHorarios(fecha);

		LocalDateTime ahora = LocalDateTime.now();
		
		for (LocalDateTime fechaHora : horarios) {

			if (fecha.equals(ahora.toLocalDate())
		            && fechaHora.isBefore(ahora)) {
		        continue;
		    }
			
			List<MedicoDisponibleResponse> medicosDisponibles = new ArrayList<>();

			for (Empleado medico : medicos) {

				if (!estaEnTurno(medico, fechaHora)) {
					continue;
				}

				if (estaOcupado(medico, fechaHora, citas)) {
					continue;
				}

				medicosDisponibles.add(
						new MedicoDisponibleResponse(medico.getIdEmpleado(), medico.getNombre(), medico.getApellido()));
			}

			if (!medicosDisponibles.isEmpty()) {
				disponibilidad.add(new DisponibilidadResponse(fechaHora, medicosDisponibles));
			}
		}

		return disponibilidad;
	}

	private List<LocalDateTime> generarHorarios(LocalDate fecha) {

		List<LocalDateTime> horarios = new ArrayList<>();

		agregarHorarios(horarios, fecha.atTime(0, 0), 8);

		agregarHorarios(horarios, fecha.atTime(6, 0), 16);

		agregarHorarios(horarios, fecha.atTime(18, 0), 8);

		return horarios;
	}

	private void agregarHorarios(List<LocalDateTime> horarios, LocalDateTime inicio, int cantidad) {

		LocalDateTime horario = inicio;

		for (int i = 0; i < cantidad; i++) {
			horarios.add(horario);
			horario = horario.plusMinutes(45);
		}
	}

	private boolean estaEnTurno(Empleado medico, LocalDateTime fechaHora) {

		String turno = medico.getTurno();

		if ("DIURNO".equals(turno)) {
			return !fechaHora.toLocalTime().isBefore(LocalTime.of(6, 0))
					&& fechaHora.toLocalTime().isBefore(LocalTime.of(18, 0));
		}

		if ("NOCTURNO".equals(turno)) {
			return !fechaHora.toLocalTime().isBefore(LocalTime.of(18, 0))
					|| fechaHora.toLocalTime().isBefore(LocalTime.of(6, 0));
		}

		return false;
	}

	private boolean estaOcupado(Empleado medico, LocalDateTime horario, List<Cita> citas) {

		LocalDateTime finHorario = horario.plusMinutes(45);

		for (Cita cita : citas) {

			if (cita.getMedico().getIdEmpleado() != medico.getIdEmpleado()) {
				continue;
			}

			LocalDateTime inicioCita = cita.getFechaHoraCita();
			LocalDateTime finCita = inicioCita.plusMinutes(45);

			boolean hayCruce = inicioCita.isBefore(finHorario) && finCita.isAfter(horario);

			if (hayCruce) {
				return true;
			}
		}

		return false;
	}
	
	private CitaResponse aRespuesta(Cita cita) {

	    return new CitaResponse(
	            cita.getIdCita(),

	            cita.getPaciente().getIdPaciente(),
	            cita.getPaciente().getNombre(),
	            cita.getPaciente().getApellido(),
	            cita.getPaciente().getDni(),

	            cita.getMedico().getIdEmpleado(),
	            cita.getMedico().getNombre(),
	            cita.getMedico().getApellido(),

	            cita.getMedico().getEspecialidad().getNombre(),

	            cita.getFechaHoraCita(),
	            cita.getFechaRegistro(),

	            cita.getEstadoCita().getNombreEstado(),

	            cita.getUsuarioRegistro().getEmpleado().getNombre(),
	            cita.getUsuarioRegistro().getEmpleado().getApellido(),
	            cita.getUsuarioRegistro().getCorreo()
	    );
	}
	

	private void validarPermisoCita(Cita cita) {
	
	    Authentication autenticacion =
	            SecurityContextHolder.getContext().getAuthentication();
	
	    if (autenticacion == null
	            || !autenticacion.isAuthenticated()
	            || "anonymousUser".equals(autenticacion.getName())) {
	        throw new SolicitudInvalidaException(
	                "No se pudo identificar al usuario autenticado");
	    }
	
	    boolean esAdministrador = autenticacion.getAuthorities().stream()
	            .anyMatch(autoridad ->
	                    autoridad.getAuthority().equals("ROLE_ADMINISTRADOR"));
	
	    if (esAdministrador) {
	        return;
	    }
	
	    Usuario usuario = usuarioRepository.findByCorreo(
	                    autenticacion.getName())
	            .orElseThrow(() -> new RecursoNoEncontradoException(
	                    "El usuario autenticado no existe"));
	
	    if (cita.getUsuarioRegistro().getIdUsuario()
	            != usuario.getIdUsuario()) {
	        throw new org.springframework.security.access.AccessDeniedException(
	                "No tienes permiso para modificar esta cita");
	    }
	}

}