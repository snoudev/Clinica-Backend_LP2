package com.clinica.clinica_backend.repository;

import com.clinica.clinica_backend.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

	List<Cita> findByUsuarioRegistroIdUsuario(int idUsuario);

	@Query("""
			    SELECT c
			    FROM Cita c
			    WHERE c.medico.especialidad.idEspecialidad = :idEspecialidad
			      AND c.fechaHoraCita >= :inicio
			      AND c.fechaHoraCita < :fin
			      AND c.estadoCita.nombreEstado IN ('PENDIENTE', 'CONFIRMADA')
			""")
	List<Cita> findCitasDelDiaPorEspecialidad(@Param("idEspecialidad") int idEspecialidad,
			@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}