package com.clinica.clinica_backend.controller;

import com.clinica.clinica_backend.dto.PacienteRequest;
import com.clinica.clinica_backend.dto.PacienteResponse;
import com.clinica.clinica_backend.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<PacienteResponse> registrar(
            @Valid @RequestBody PacienteRequest request,
            Authentication authentication) {

        PacienteResponse response = pacienteService.registrar(request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponse>> listar() {

        return ResponseEntity.ok(pacienteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> obtenerPorId(@PathVariable int id) {

        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<PacienteResponse> buscarPorDni(@PathVariable String dni) {

        return ResponseEntity.ok(pacienteService.buscarPorDni(dni));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponse> actualizar(
            @PathVariable int id,
            @Valid @RequestBody PacienteRequest request) {

        return ResponseEntity.ok(pacienteService.actualizar(id, request));
    }
}