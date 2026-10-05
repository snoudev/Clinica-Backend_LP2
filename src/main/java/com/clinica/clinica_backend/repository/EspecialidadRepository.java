package com.clinica.clinica_backend.repository;

import com.clinica.clinica_backend.entity.Especialidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {
	
	List<Especialidad> findByEstadoTrue();
	
}