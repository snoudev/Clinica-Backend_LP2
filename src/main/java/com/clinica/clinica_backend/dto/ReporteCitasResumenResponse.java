package com.clinica.clinica_backend.dto;

import java.time.LocalDate;
import java.util.List;

public record ReporteCitasResumenResponse(
	LocalDate desde,
	LocalDate hasta,
	Totales totales,
	List<PorEspecialidad> porEspecialidad,
	List<PorMedico> porMedico,
	List<PorDia> porDia,
	List<PorUsuario> porUsuario
) {

public record Totales(
        long total,
        long pendientes,
        long atendidas,
        long canceladas,
        long noAsistidas,
        long pacientesDistintos,
        long medicosConCitas
) {}

public record PorEspecialidad(
        int idEspecialidad,
        String especialidad,
        long total,
        long pendientes,
        long atendidas,
        long canceladas,
        long noAsistidas
) {}

public record PorMedico(
        int idMedico,
        String medico,
        String especialidad,
        String turno,
        long total,
        long pendientes,
        long atendidas,
        long canceladas,
        long noAsistidas
) {}

public record PorDia(
        LocalDate fecha,
        long total,
        long pendientes,
        long atendidas,
        long canceladas,
        long noAsistidas
) {}

public record PorUsuario(
        int idUsuario,
        String nombre,
        long total
) {}

}
