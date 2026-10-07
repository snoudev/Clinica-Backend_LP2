package com.clinica.clinica_backend.dto;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoCitaRequest(

        @NotNull(message = "El estado es obligatorio")
        Integer idEstadoCita
) {
}