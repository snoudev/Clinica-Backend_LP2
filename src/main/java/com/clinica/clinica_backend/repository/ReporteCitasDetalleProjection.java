package com.clinica.clinica_backend.repository;

import java.time.LocalDateTime;

public interface ReporteCitasDetalleProjection {

    Integer getIdCita();
    LocalDateTime getFechaHoraCita();
    String getPaciente();
    String getDniPaciente();
    String getMedico();
    String getEspecialidad();
    String getEstado();
    String getRegistradoPor();
    LocalDateTime getFechaRegistro();
}