package com.clinica.clinica_backend.repository;

import com.clinica.clinica_backend.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    Optional<Paciente> findByDni(String dni);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdPacienteNot(String dni, int idPaciente);
}