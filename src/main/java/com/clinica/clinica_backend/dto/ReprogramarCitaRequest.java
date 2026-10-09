package com.clinica.clinica_backend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReprogramarCitaRequest(
        @NotNull(message = "El médico es obligatorio")
        Integer idMedico,

        @NotNull(message = "La nueva fecha y hora son obligatorias")
        LocalDateTime fechaHoraCita
) {}