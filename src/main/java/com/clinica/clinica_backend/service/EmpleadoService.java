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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpleadoService {

    private static final String CARGO_MEDICO = "MEDICO";

    private final EmpleadoRepository empleadoRepository;
    private final CargoRepository cargoRepository;
    private final EspecialidadRepository especialidadRepository;

    public EmpleadoService(
            EmpleadoRepository empleadoRepository,
            CargoRepository cargoRepository,
            EspecialidadRepository especialidadRepository) {

        this.empleadoRepository = empleadoRepository;
        this.cargoRepository = cargoRepository;
        this.especialidadRepository = especialidadRepository;
    }

    @Transactional
    public EmpleadoResponse registrar(EmpleadoRequest request) {

        if (empleadoRepository.existsByDni(request.dni())) {
            throw new ConflictoException("Ya existe un empleado con el DNI " + request.dni());
        }

        Empleado empleado = new Empleado();
        empleado.setEstado(true);
        aplicarDatos(empleado, request);

        return EmpleadoResponse.desde(empleadoRepository.save(empleado));
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listar() {
        return empleadoRepository.findAll()
                .stream()
                .map(EmpleadoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse obtenerPorId(int id) {
        return EmpleadoResponse.desde(buscarEmpleado(id));
    }

    @Transactional
    public EmpleadoResponse actualizar(int id, EmpleadoRequest request) {

        Empleado empleado = buscarEmpleado(id);

        if (empleadoRepository.existsByDniAndIdEmpleadoNot(request.dni(), id)) {
            throw new ConflictoException("Ya existe otro empleado con el DNI " + request.dni());
        }

        aplicarDatos(empleado, request);

        return EmpleadoResponse.desde(empleadoRepository.save(empleado));
    }

    @Transactional
    public EmpleadoResponse cambiarEstado(int id, boolean estado) {

        Empleado empleado = buscarEmpleado(id);
        empleado.setEstado(estado);

        return EmpleadoResponse.desde(empleadoRepository.save(empleado));
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listarMedicos(Integer idEspecialidad) {

        List<Empleado> medicos;

        if (idEspecialidad == null) {
            medicos = empleadoRepository.findByCargoNombreCargoAndEstadoTrue(CARGO_MEDICO);
        } else {
            medicos = empleadoRepository
                    .findByCargoNombreCargoAndEstadoTrueAndEspecialidadIdEspecialidad(
                            CARGO_MEDICO, idEspecialidad);
        }

        return medicos.stream()
                .map(EmpleadoResponse::desde)
                .toList();
    }

    private Empleado buscarEmpleado(int id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un empleado con id " + id));
    }

    private void aplicarDatos(Empleado empleado, EmpleadoRequest request) {

        Cargo cargo = cargoRepository.findById(request.idCargo())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un cargo con id " + request.idCargo()));

        Especialidad especialidad = null;
        boolean esMedico = CARGO_MEDICO.equalsIgnoreCase(cargo.getNombreCargo());

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

        empleado.setCargo(cargo);
        empleado.setEspecialidad(especialidad);
        empleado.setNombre(request.nombre().trim());
        empleado.setApellido(request.apellido().trim());
        empleado.setDni(request.dni());
        empleado.setTelefono(request.telefono());
    }
}
