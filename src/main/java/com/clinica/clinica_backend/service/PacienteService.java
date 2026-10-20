package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.dto.PacienteRequest;
import com.clinica.clinica_backend.dto.PacienteResponse;
import com.clinica.clinica_backend.entity.Paciente;
import com.clinica.clinica_backend.entity.Usuario;
import com.clinica.clinica_backend.exception.ConflictoException;
import com.clinica.clinica_backend.exception.RecursoNoEncontradoException;
import com.clinica.clinica_backend.repository.PacienteRepository;
import com.clinica.clinica_backend.repository.UsuarioRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public PacienteService(
            PacienteRepository pacienteRepository,
            UsuarioRepository usuarioRepository) {

        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PacienteResponse registrar(PacienteRequest request, String correoUsuario) {

        String dni = request.dni().trim();

        if (pacienteRepository.existsByDni(dni)) {
            throw new ConflictoException("Ya existe un paciente con ese DNI.");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario autenticado."
                ));

        Paciente paciente = new Paciente();
        paciente.setNombre(request.nombre().trim());
        paciente.setApellido(request.apellido().trim());
        paciente.setDni(dni);
        paciente.setTelefono(
                request.telefono() != null ? request.telefono().trim() : null
        );
        paciente.setRegistradoPorUsuario(usuario);

        return aRespuesta(pacienteRepository.save(paciente));
    }

    public List<PacienteResponse> listar() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::aRespuesta)
                .toList();
    }

    public PacienteResponse obtenerPorId(int id) {
        return aRespuesta(buscarEntidad(id));
    }

    public PacienteResponse buscarPorDni(String dni) {

        Paciente paciente = pacienteRepository.findByDni(dni.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un paciente con ese DNI."
                ));

        return aRespuesta(paciente);
    }

    public PacienteResponse actualizar(int id, PacienteRequest request) {

        Paciente paciente = buscarEntidad(id);

        String dni = request.dni().trim();

        if (pacienteRepository.existsByDniAndIdPacienteNot(dni, id)) {
            throw new ConflictoException("Ya existe un paciente con ese DNI.");
        }

        paciente.setNombre(request.nombre().trim());
        paciente.setApellido(request.apellido().trim());
        paciente.setDni(dni);
        paciente.setTelefono(
                request.telefono() != null ? request.telefono().trim() : null
        );

        return aRespuesta(pacienteRepository.save(paciente));
    }

    private Paciente buscarEntidad(int id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un paciente con id " + id + "."));
    }

    private PacienteResponse aRespuesta(Paciente paciente) {

        String nombreUsuarioRegistro = null;
        String apellidoUsuarioRegistro = null;
        String correoUsuarioRegistro = null;

        if (paciente.getRegistradoPorUsuario() != null) {
            nombreUsuarioRegistro = paciente.getRegistradoPorUsuario().getEmpleado().getNombre();
            apellidoUsuarioRegistro = paciente.getRegistradoPorUsuario().getEmpleado().getApellido();
            correoUsuarioRegistro = paciente.getRegistradoPorUsuario().getCorreo();
        }

        return new PacienteResponse(
                paciente.getIdPaciente(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getTelefono(),
                nombreUsuarioRegistro,
                apellidoUsuarioRegistro,
                correoUsuarioRegistro
        );
    }
}