package com.clinica.clinica_backend.controller;

import com.clinica.clinica_backend.entity.Especialidad;
import com.clinica.clinica_backend.service.EspecialidadService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<Especialidad>> listarTodas() {
        return ResponseEntity.ok(especialidadService.listarTodas());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Especialidad> buscarPorId(@PathVariable int id) {
        return especialidadService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'AGENTE_VENTANILLA')")
    @GetMapping("/activas")
    public ResponseEntity<List<Especialidad>> listarActivas() {
        return ResponseEntity.ok(especialidadService.listarActivas());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<Especialidad> registrar(@RequestBody Especialidad especialidad) {
        return ResponseEntity.ok(especialidadService.guardar(especialidad));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Especialidad> actualizar(
            @PathVariable int id,
            @RequestBody Especialidad especialidad) {

        return especialidadService.actualizar(id, especialidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Especialidad> cambiarEstado(@PathVariable int id) {
        return especialidadService.cambiarEstado(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}