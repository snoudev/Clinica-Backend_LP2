package com.clinica.clinica_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DisponibilidadResponse(
        LocalDateTime fechaHora,
        List<MedicoDisponibleResponse> medicos
) {}