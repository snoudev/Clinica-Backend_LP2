package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.dto.EmpleadoRequest;
import com.clinica.clinica_backend.dto.EmpleadoResponse;
import com.clinica.clinica_backend.entity.Cargo;
import com.clinica.clinica_backend.entity.Empleado;
import com.clinica.clinica_backend.entity.Especialidad;
import com.clinica.clinica_backend.exception.ConflictoException;
import com.clinica.clinica_backend.exception.RecursoNoEncontradoException;
import com.clinica.clinica_backend.exception.SolicitudInvalidaException;
import com.clinica.clinica_backend.repository.CargoRepository;
import com.clinica.clinica_backend.repository.EmpleadoRepository;
import com.clinica.clinica_backend.repository.EspecialidadRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

	private static final String CARGO_MEDICO = "MEDICO";
	private static final String CARGO_RECEPCIONISTA = "RECEPCIONISTA";

	private static final String TURNO_DIURNO = "DIURNO";
	private static final String TURNO_NOCTURNO = "NOCTURNO";

	private final EmpleadoRepository empleadoRepository;
	private final CargoRepository cargoRepository;
	private final EspecialidadRepository especialidadRepository;

	public EmpleadoService(EmpleadoRepository empleadoRepository, CargoRepository cargoRepository,
			EspecialidadRepository especialidadRepository) {

		this.empleadoRepository = empleadoRepository;
		this.cargoRepository = cargoRepository;
		this.especialidadRepository = especialidadRepository;
	}

	public EmpleadoResponse registrar(EmpleadoRequest request) {

		String dni = request.dni().trim();

		if (empleadoRepository.existsByDni(dni)) {
		    throw new ConflictoException(
		            "Ya existe un empleado con el DNI " + dni);
		}

		Empleado empleado = new Empleado();
		empleado.setEstado(true);

		aplicarDatos(empleado, request);

		return EmpleadoResponse.desde(empleadoRepository.save(empleado));
	}

	public List<EmpleadoResponse> listar() {

		return empleadoRepository.findAll().stream().map(EmpleadoResponse::desde).toList();
	}

	public EmpleadoResponse obtenerPorId(int id) {

		return EmpleadoResponse.desde(buscarEmpleado(id));
	}

	public EmpleadoResponse actualizar(int id, EmpleadoRequest request) {

		Empleado empleado = buscarEmpleado(id);

		String dni = request.dni().trim();

		if (empleadoRepository.existsByDniAndIdEmpleadoNot(dni, id)) {
		    throw new ConflictoException(
		            "Ya existe otro empleado con el DNI " + dni);
		}

		aplicarDatos(empleado, request);

		return EmpleadoResponse.desde(empleadoRepository.save(empleado));
	}

	public EmpleadoResponse cambiarEstado(int id, boolean estado) {

		Empleado empleado = buscarEmpleado(id);

		empleado.setEstado(estado);

		return EmpleadoResponse.desde(empleadoRepository.save(empleado));
	}

	public List<EmpleadoResponse> listarMedicos(Integer idEspecialidad) {

		List<Empleado> medicos;

		if (idEspecialidad == null) {

			medicos = empleadoRepository.findByCargoNombreCargoAndEstadoTrue(CARGO_MEDICO);

		} else {

			medicos = empleadoRepository.findByCargoNombreCargoAndEstadoTrueAndEspecialidadIdEspecialidad(CARGO_MEDICO,
					idEspecialidad);
		}

		return medicos.stream().map(EmpleadoResponse::desde).toList();
	}

	private Empleado buscarEmpleado(int id) {

		return empleadoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe un empleado con id " + id));
	}

	private void aplicarDatos(Empleado empleado, EmpleadoRequest request) {

		Cargo cargo = cargoRepository.findById(request.idCargo())
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe un cargo con id " + request.idCargo()));

		Especialidad especialidad = null;

		boolean esMedico = CARGO_MEDICO.equalsIgnoreCase(cargo.getNombreCargo());

		boolean esRecepcionista = CARGO_RECEPCIONISTA.equalsIgnoreCase(cargo.getNombreCargo());

		if (esMedico) {

			if (request.idEspecialidad() == null) {
				throw new SolicitudInvalidaException("Un médico debe tener una especialidad");
			}

			especialidad = especialidadRepository.findById(request.idEspecialidad())
					.orElseThrow(() -> new RecursoNoEncontradoException(
							"No existe una especialidad con id " + request.idEspecialidad()));

			if (!especialidad.isEstado()) {
				throw new SolicitudInvalidaException("La especialidad seleccionada está inactiva");
			}

		} else if (request.idEspecialidad() != null) {

			throw new SolicitudInvalidaException("Solo los médicos pueden tener una especialidad");
		}

		if (esMedico || esRecepcionista) {

			if (request.turno() == null || request.turno().isBlank()) {

				throw new SolicitudInvalidaException("El turno es obligatorio para médicos y recepcionistas");
			}

			String turno = request.turno().trim().toUpperCase();

			if (!TURNO_DIURNO.equals(turno) && !TURNO_NOCTURNO.equals(turno)) {

				throw new SolicitudInvalidaException("El turno debe ser DIURNO o NOCTURNO");
			}

			empleado.setTurno(turno);

		} else {

			if (request.turno() != null && !request.turno().isBlank()) {

				throw new SolicitudInvalidaException("Los empleados administrativos no tienen turno");
			}

			empleado.setTurno(null);
		}

		empleado.setCargo(cargo);
		empleado.setEspecialidad(especialidad);
		empleado.setNombre(request.nombre().trim());
		empleado.setApellido(request.apellido().trim());
		empleado.setDni(request.dni().trim());
		empleado.setTelefono(request.telefono());
	}
}