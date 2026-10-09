
package com.clinica.clinica_backend.controller;

import com.clinica.clinica_backend.dto.CitaRequest;
import com.clinica.clinica_backend.dto.CitaResponse;
import com.clinica.clinica_backend.dto.DisponibilidadResponse;
import com.clinica.clinica_backend.dto.ReprogramarCitaRequest;
import com.clinica.clinica_backend.service.CitaService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/citas")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'AGENTE_VENTANILLA')")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping("/disponibilidad")
    public List<DisponibilidadResponse> consultarDisponibilidad(
            @RequestParam int idEspecialidad,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return citaService.consultarDisponibilidad(idEspecialidad, fecha);
    }

    @PostMapping
    public ResponseEntity<CitaResponse> registrar(
            @Valid @RequestBody CitaRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(citaService.registrarCita(request));
    }

    @GetMapping
    public List<CitaResponse> listar() {
        return citaService.listarCitas();
    }

    @GetMapping("/{idCita}")
    public CitaResponse buscarPorId(@PathVariable int idCita) {
        return citaService.buscarCitaPorId(idCita);
    }

    @PatchMapping("/{idCita}/estado")
    public CitaResponse cambiarEstado(
            @PathVariable int idCita,
            @RequestParam String nombreEstado) {

        return citaService.cambiarEstadoCita(idCita, nombreEstado);
    }

    @PutMapping("/{idCita}/reprogramar")
    public CitaResponse reprogramar(
            @PathVariable int idCita,
            @Valid @RequestBody ReprogramarCitaRequest request) {

        return citaService.reprogramarCita(idCita, request);
    }
}
