package com.clinica.clinica_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UsuarioActualizarRequest(
        @Positive(message = "El id del rol debe ser un número positivo")
        int idRol,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String correo
) {
}