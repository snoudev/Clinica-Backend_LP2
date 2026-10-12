
package com.clinica.clinica_backend.repository;

import com.clinica.clinica_backend.repository.ReporteCitasAgrupacionProjection.PorEspecialidad;
import com.clinica.clinica_backend.repository.ReporteCitasAgrupacionProjection.PorMedico;
import com.clinica.clinica_backend.repository.ReporteCitasAgrupacionProjection.PorDia;
import com.clinica.clinica_backend.repository.ReporteCitasAgrupacionProjection.PorUsuario;
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
          AND c.estadoCita.nombreEstado = 'PENDIENTE'
        """)
    List<Cita> findCitasDelDiaPorEspecialidad(
            @Param("idEspecialidad") int idEspecialidad,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    
    
    @Query("""
        SELECT c
        FROM Cita c
        WHERE c.medico.idEmpleado = :idMedico
          AND c.fechaHoraCita > :desde
          AND c.fechaHoraCita < :hasta
          AND c.estadoCita.nombreEstado = 'PENDIENTE'
        """)
    List<Cita> findCitasActivasDelMedicoEnRango(
            @Param("idMedico") int idMedico,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
    

    
    
	@Query("""
	    SELECT c
	    FROM Cita c
	    WHERE c.medico.idEmpleado = :idMedico
	      AND c.idCita <> :idCita
	      AND c.fechaHoraCita > :desde
	      AND c.fechaHoraCita < :hasta
	      AND c.estadoCita.nombreEstado = 'PENDIENTE'
	    """)
	List<Cita> findCitasActivasDelMedicoEnRangoExcluyendoCita(
	        @Param("idMedico") int idMedico,
	        @Param("idCita") int idCita,
	        @Param("desde") LocalDateTime desde,
	        @Param("hasta") LocalDateTime hasta);

	
	
	
	@Query("""
		    SELECT
		        COUNT(c) AS total,
		        COALESCE(SUM(CASE WHEN c.estadoCita.nombreEstado = 'PENDIENTE' THEN 1 ELSE 0 END), 0) AS pendientes,
		        COALESCE(SUM(CASE WHEN c.estadoCita.nombreEstado = 'ATENDIDA' THEN 1 ELSE 0 END), 0) AS atendidas,
		        COALESCE(SUM(CASE WHEN c.estadoCita.nombreEstado = 'CANCELADA' THEN 1 ELSE 0 END), 0) AS canceladas,
		        COALESCE(SUM(CASE WHEN c.estadoCita.nombreEstado = 'NO ASISTIÓ' THEN 1 ELSE 0 END), 0) AS noAsistidas,
		        COUNT(DISTINCT c.paciente.idPaciente) AS pacientesDistintos,
		        COUNT(DISTINCT c.medico.idEmpleado) AS medicosConCitas
		    FROM Cita c
		    WHERE c.fechaHoraCita >= :inicio
		      AND c.fechaHoraCita < :fin
		      AND (:idEspecialidad IS NULL
		           OR c.medico.especialidad.idEspecialidad = :idEspecialidad)
		      AND (:idMedico IS NULL
		           OR c.medico.idEmpleado = :idMedico)
		    """)
		ReporteCitasTotalesProjection obtenerTotalesReporte(
		        @Param("inicio") LocalDateTime inicio,
		        @Param("fin") LocalDateTime fin,
		        @Param("idEspecialidad") Integer idEspecialidad,
		        @Param("idMedico") Integer idMedico
		);
	
	
	
	
	@Query("""
	    SELECT
	        e.idEspecialidad AS idEspecialidad,
	        e.nombre AS especialidad,
	        COUNT(c) AS total,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'PENDIENTE' THEN 1 ELSE 0 END) AS pendientes,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'ATENDIDA' THEN 1 ELSE 0 END) AS atendidas,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'CANCELADA' THEN 1 ELSE 0 END) AS canceladas,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'NO ASISTIÓ' THEN 1 ELSE 0 END) AS noAsistidas
	    FROM Cita c
	    JOIN c.medico m
	    JOIN m.especialidad e
	    WHERE c.fechaHoraCita >= :inicio
	      AND c.fechaHoraCita < :fin
	      AND (:idEspecialidad IS NULL OR e.idEspecialidad = :idEspecialidad)
	      AND (:idMedico IS NULL OR m.idEmpleado = :idMedico)
	    GROUP BY e.idEspecialidad, e.nombre
		ORDER BY e.nombre
	    """)
	List<PorEspecialidad> obtenerCitasPorEspecialidadReporte(
	    @Param("inicio") LocalDateTime inicio,
	    @Param("fin") LocalDateTime fin,
	    @Param("idEspecialidad") Integer idEspecialidad,
	    @Param("idMedico") Integer idMedico
	);


	
	
	@Query("""
	    SELECT
	        m.idEmpleado AS idMedico,
	        CONCAT(m.nombre, ' ', m.apellido) AS medico,
	        e.nombre AS especialidad,
	        m.turno AS turno,
	        COUNT(c) AS total,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'PENDIENTE' THEN 1 ELSE 0 END) AS pendientes,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'ATENDIDA' THEN 1 ELSE 0 END) AS atendidas,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'CANCELADA' THEN 1 ELSE 0 END) AS canceladas,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'NO ASISTIÓ' THEN 1 ELSE 0 END) AS noAsistidas
	    FROM Cita c
	    JOIN c.medico m
	    JOIN m.especialidad e
	    WHERE c.fechaHoraCita >= :inicio
	      AND c.fechaHoraCita < :fin
	      AND (:idEspecialidad IS NULL OR e.idEspecialidad = :idEspecialidad)
	      AND (:idMedico IS NULL OR m.idEmpleado = :idMedico)
	    GROUP BY m.idEmpleado, m.nombre, m.apellido, e.nombre, m.turno
	    ORDER BY m.apellido, m.nombre
	    """)
	List<PorMedico> obtenerCitasPorMedicoReporte(
	    @Param("inicio") LocalDateTime inicio,
	    @Param("fin") LocalDateTime fin,
	    @Param("idEspecialidad") Integer idEspecialidad,
	    @Param("idMedico") Integer idMedico
	);


	
	
	@Query("""
	    SELECT
	        FUNCTION('DATE', c.fechaHoraCita) AS fecha,
	        COUNT(c) AS total,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'PENDIENTE' THEN 1 ELSE 0 END) AS pendientes,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'ATENDIDA' THEN 1 ELSE 0 END) AS atendidas,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'CANCELADA' THEN 1 ELSE 0 END) AS canceladas,
	        SUM(CASE WHEN c.estadoCita.nombreEstado = 'NO ASISTIÓ' THEN 1 ELSE 0 END) AS noAsistidas
	    FROM Cita c
	    WHERE c.fechaHoraCita >= :inicio
	      AND c.fechaHoraCita < :fin
	      AND (:idEspecialidad IS NULL OR c.medico.especialidad.idEspecialidad = :idEspecialidad)
	      AND (:idMedico IS NULL OR c.medico.idEmpleado = :idMedico)
	    GROUP BY FUNCTION('DATE', c.fechaHoraCita)
	    ORDER BY FUNCTION('DATE', c.fechaHoraCita)
	    """)
	List<PorDia> obtenerCitasPorDiaReporte(
	    @Param("inicio") LocalDateTime inicio,
	    @Param("fin") LocalDateTime fin,
	    @Param("idEspecialidad") Integer idEspecialidad,
	    @Param("idMedico") Integer idMedico
	);


	
	
	@Query("""
		    SELECT
		        u.idUsuario AS idUsuario,
		        CASE
		            WHEN u.empleado IS NULL THEN CONCAT('Usuario ', u.idUsuario)
		            ELSE CONCAT(u.empleado.nombre, ' ', u.empleado.apellido)
		        END AS nombre,
		        COUNT(c) AS total
		    FROM Cita c
		    JOIN c.usuarioRegistro u
		    LEFT JOIN u.empleado emp
		    WHERE c.fechaHoraCita >= :inicio
		      AND c.fechaHoraCita < :fin
		      AND (:idEspecialidad IS NULL
		           OR c.medico.especialidad.idEspecialidad = :idEspecialidad)
		      AND (:idMedico IS NULL
		           OR c.medico.idEmpleado = :idMedico)
		    GROUP BY u.idUsuario, u.empleado, emp.nombre, emp.apellido
		    ORDER BY COUNT(c) DESC
		    """)
		List<PorUsuario> obtenerCitasPorUsuarioReporte(
		    @Param("inicio") LocalDateTime inicio,
		    @Param("fin") LocalDateTime fin,
		    @Param("idEspecialidad") Integer idEspecialidad,
		    @Param("idMedico") Integer idMedico
		);
	
	
	
	@Query("""
		    SELECT
		        c.idCita AS idCita,
		        c.fechaHoraCita AS fechaHoraCita,
		        CONCAT(p.nombre, ' ', p.apellido) AS paciente,
		        p.dni AS dniPaciente,
		        CONCAT(m.nombre, ' ', m.apellido) AS medico,
		        e.nombre AS especialidad,
		        est.nombreEstado AS estado,
		        CASE
		            WHEN u.idUsuario IS NULL THEN 'Sin usuario'
		            WHEN emp.idEmpleado IS NULL THEN CONCAT('Usuario ', u.idUsuario)
		            ELSE CONCAT(emp.nombre, ' ', emp.apellido)
		        END AS registradoPor,
		        c.fechaRegistro AS fechaRegistro
		    FROM Cita c
		    JOIN c.paciente p
		    JOIN c.medico m
		    JOIN m.especialidad e
		    JOIN c.estadoCita est
		    LEFT JOIN c.usuarioRegistro u
		    LEFT JOIN u.empleado emp
		    WHERE c.fechaHoraCita >= :inicio
		      AND c.fechaHoraCita < :fin
		      AND (:idEspecialidad IS NULL OR e.idEspecialidad = :idEspecialidad)
		      AND (:idMedico IS NULL OR m.idEmpleado = :idMedico)
		      AND (:estado IS NULL OR est.nombreEstado = :estado)
		    ORDER BY c.fechaHoraCita DESC
		    """)
		List<ReporteCitasDetalleProjection> obtenerDetalleReporte(
		        @Param("inicio") LocalDateTime inicio,
		        @Param("fin") LocalDateTime fin,
		        @Param("idEspecialidad") Integer idEspecialidad,
		        @Param("idMedico") Integer idMedico,
		        @Param("estado") String estado
		);
}
