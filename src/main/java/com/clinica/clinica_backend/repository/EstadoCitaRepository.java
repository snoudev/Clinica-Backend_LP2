package com.clinica.clinica_backend.repository;

import com.clinica.clinica_backend.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoCitaRepository extends JpaRepository<EstadoCita, Integer> {

    Optional<EstadoCita> findByNombreEstado(String nombreEstado);
}
