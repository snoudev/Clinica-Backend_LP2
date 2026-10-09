package com.clinica.clinica_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CambiarEstadoCitaRequest(
        @NotBlank(message = "El estado es obligatorio")
        String nombreEstado
) {}