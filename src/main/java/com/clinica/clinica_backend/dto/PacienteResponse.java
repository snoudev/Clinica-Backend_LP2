package com.clinica.clinica_backend.dto;

public record PacienteResponse(
        int idPaciente,
        String nombre,
        String apellido,
        String dni,
        String telefono,
        String nombreUsuarioRegistro,
        String apellidoUsuarioRegistro,
        String correoUsuarioRegistro
) {}