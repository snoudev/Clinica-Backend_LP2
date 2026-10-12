package com.clinica.clinica_backend.dto;

import java.time.LocalDateTime;

public record ReporteCitasDetalleResponse(
        int idCita,
        LocalDateTime fechaHoraCita,
        String paciente,
        String dniPaciente,
        String medico,
        String especialidad,
        String estado,
        String registradoPor,
        LocalDateTime fechaRegistro
) {
}