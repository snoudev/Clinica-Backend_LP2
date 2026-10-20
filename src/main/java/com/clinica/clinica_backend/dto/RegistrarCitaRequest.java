package com.clinica.clinica_backend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RegistrarCitaRequest(

        @NotNull(message = "El paciente es obligatorio")
        Integer idPaciente,

        @NotNull(message = "El médico es obligatorio")
        Integer idMedico,

        @NotNull(message = "La fecha y hora de la cita son obligatorias")
        LocalDateTime fechaHoraCita
) {
}