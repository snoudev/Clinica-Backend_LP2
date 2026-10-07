package com.clinica.clinica_backend.dto;

public record PacienteResponse(
        int idPaciente,
        String nombre,
        String apellido,
        String dni,
        String telefono,
        Integer registradoPorUsuario
) {
}