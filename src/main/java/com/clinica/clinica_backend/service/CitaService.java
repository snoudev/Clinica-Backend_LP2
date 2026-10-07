package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.dto.DisponibilidadResponse;
import com.clinica.clinica_backend.dto.MedicoDisponibleResponse;
import com.clinica.clinica_backend.entity.Cita;
import com.clinica.clinica_backend.entity.Empleado;
import com.clinica.clinica_backend.repository.CitaRepository;
import com.clinica.clinica_backend.repository.EmpleadoRepository;
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

	public CitaService(CitaRepository citaRepository, EmpleadoRepository empleadoRepository) {

		this.citaRepository = citaRepository;
		this.empleadoRepository = empleadoRepository;
	}

	public List<DisponibilidadResponse> consultarDisponibilidad(int idEspecialidad, LocalDate fecha) {

		LocalDateTime inicio = fecha.atStartOfDay();
		LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

		List<Empleado> medicos = empleadoRepository
				.findByCargoNombreCargoAndEstadoTrueAndEspecialidadIdEspecialidad("MEDICO", idEspecialidad);

		List<Cita> citas = citaRepository.findCitasDelDiaPorEspecialidad(idEspecialidad, inicio, fin);

		List<DisponibilidadResponse> disponibilidad = new ArrayList<>();

		List<LocalDateTime> horarios = generarHorarios(fecha);

		for (LocalDateTime fechaHora : horarios) {

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
}