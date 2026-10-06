package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.entity.Especialidad;
import com.clinica.clinica_backend.exception.ConflictoException;
import com.clinica.clinica_backend.exception.RecursoNoEncontradoException;
import com.clinica.clinica_backend.repository.EspecialidadRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    public List<Especialidad> listarTodas() {
        return especialidadRepository.findAll();
    }

    public Especialidad buscarPorId(int id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una especialidad con id " + id));
    }

    public List<Especialidad> listarActivas() {
        return especialidadRepository.findByEstadoTrue();
    }

    public Especialidad guardar(Especialidad especialidad) {

        if (especialidadRepository.existsByNombre(especialidad.getNombre())) {
            throw new ConflictoException(
                    "Ya existe una especialidad con ese nombre");
        }

        especialidad.setNombre(especialidad.getNombre().trim());

        return especialidadRepository.save(especialidad);
    }

    public Especialidad actualizar(int id, Especialidad especialidad) {

        Especialidad actual = buscarPorId(id);

        if (!actual.getNombre().equalsIgnoreCase(especialidad.getNombre())
                && especialidadRepository.existsByNombre(especialidad.getNombre())) {

            throw new ConflictoException(
                    "Ya existe una especialidad con ese nombre");
        }

        actual.setNombre(especialidad.getNombre().trim());
        actual.setDescripcion(especialidad.getDescripcion());
        actual.setEstado(especialidad.isEstado());

        return especialidadRepository.save(actual);
    }

    public Especialidad cambiarEstado(int id) {

        Especialidad especialidad = buscarPorId(id);

        especialidad.setEstado(!especialidad.isEstado());

        return especialidadRepository.save(especialidad);
    }
}