package com.clinica.clinica_backend.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
        int idUsuario,
        int idEmpleado,
        String nombreEmpleado,
        String correo,
        String rol,
        boolean estado,
        LocalDateTime creadoEn
) {
}