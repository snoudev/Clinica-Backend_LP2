package com.clinica.clinica_backend.dto;

import java.time.LocalDateTime;

public record CitaResponse(
        int idCita,

        int idPaciente,
        String nombrePaciente,
        String apellidoPaciente,
        String dniPaciente,

        int idMedico,
        String nombreMedico,
        String apellidoMedico,

        String nombreEspecialidad,

        LocalDateTime fechaHoraCita,
        LocalDateTime fechaRegistro,

        String estado,

        String nombreUsuarioRegistro,
        String apellidoUsuarioRegistro,
        String correoUsuarioRegistro
) {
}