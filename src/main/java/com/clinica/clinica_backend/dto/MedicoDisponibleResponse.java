package com.clinica.clinica_backend.dto;

public record MedicoDisponibleResponse(
        int idMedico,
        String nombre,
        String apellido
) {}