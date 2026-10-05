package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.entity.Especialidad;
import com.clinica.clinica_backend.repository.EspecialidadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    public List<Especialidad> listarTodas() {
        return especialidadRepository.findAll();
    }

    public Optional<Especialidad> buscarPorId(int id) {
        return especialidadRepository.findById(id);
    }

    public List<Especialidad> listarActivas() {
        return especialidadRepository.findByEstadoTrue();
    }

    public Especialidad guardar(Especialidad especialidad) {
        return especialidadRepository.save(especialidad);
    }

    public Optional<Especialidad> actualizar(int id, Especialidad especialidad) {
    	
        Optional<Especialidad> existente = especialidadRepository.findById(id);

        if (existente.isPresent()) {
            Especialidad actual = existente.get();

            actual.setNombre(especialidad.getNombre());
            actual.setDescripcion(especialidad.getDescripcion());
            actual.setEstado(especialidad.isEstado());

            return Optional.of(especialidadRepository.save(actual));
        }

        return Optional.empty();
    }

    public Optional<Especialidad> cambiarEstado(int id) {
    	
        Optional<Especialidad> existente = especialidadRepository.findById(id);

        if (existente.isPresent()) {
            Especialidad especialidad = existente.get();

            especialidad.setEstado(!especialidad.isEstado());

            return Optional.of(especialidadRepository.save(especialidad));
        }

        return Optional.empty();
    }
}