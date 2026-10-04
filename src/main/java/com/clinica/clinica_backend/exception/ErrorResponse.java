package com.clinica.clinica_backend.exception;

import java.util.List;

public record ErrorResponse(
        int estado,
        String mensaje,
        List<String> detalles
) {
}
