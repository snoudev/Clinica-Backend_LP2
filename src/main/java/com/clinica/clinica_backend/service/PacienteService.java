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

        if (pacienteRepository.existsByDni(request.dni())) {
            throw new ConflictoException("Ya existe un paciente con ese DNI.");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));

        Paciente paciente = new Paciente();
        paciente.setNombre(request.nombre());
        paciente.setApellido(request.apellido());
        paciente.setDni(request.dni());
        paciente.setTelefono(request.telefono());
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
        Paciente paciente = pacienteRepository.findByDni(dni)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un paciente con ese DNI."));

        return aRespuesta(paciente);
    }

    public PacienteResponse actualizar(int id, PacienteRequest request) {

        Paciente paciente = buscarEntidad(id);

        if (pacienteRepository.existsByDniAndIdPacienteNot(request.dni(), id)) {
            throw new ConflictoException("Ya existe un paciente con ese DNI.");
        }

        paciente.setNombre(request.nombre());
        paciente.setApellido(request.apellido());
        paciente.setDni(request.dni());
        paciente.setTelefono(request.telefono());

        return aRespuesta(pacienteRepository.save(paciente));
    }

    private Paciente buscarEntidad(int id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un paciente con id " + id + "."));
    }

    private PacienteResponse aRespuesta(Paciente paciente) {

        Integer idUsuario = null;

        if (paciente.getRegistradoPorUsuario() != null) {
            idUsuario = paciente.getRegistradoPorUsuario().getIdUsuario();
        }

        return new PacienteResponse(
                paciente.getIdPaciente(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getTelefono(),
                idUsuario
        );
    }
}