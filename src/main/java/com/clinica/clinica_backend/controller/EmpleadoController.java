package com.clinica.clinica_backend.controller;

import com.clinica.clinica_backend.dto.EmpleadoRequest;
import com.clinica.clinica_backend.dto.EmpleadoResponse;
import com.clinica.clinica_backend.dto.EstadoRequest;
import com.clinica.clinica_backend.service.EmpleadoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponse> registrar(@Valid @RequestBody EmpleadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.registrar(request));
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoResponse>> listar() {
        return ResponseEntity.ok(empleadoService.listar());
    }

    @GetMapping("/medicos")
    public ResponseEntity<List<EmpleadoResponse>> listarMedicos(
            @RequestParam(required = false) Integer idEspecialidad) {
        return ResponseEntity.ok(empleadoService.listarMedicos(idEspecialidad));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(empleadoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> actualizar(
            @PathVariable int id,
            @Valid @RequestBody EmpleadoRequest request) {
        return ResponseEntity.ok(empleadoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EmpleadoResponse> cambiarEstado(
            @PathVariable int id,
            @Valid @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(empleadoService.cambiarEstado(id, request.estado()));
    }
}
