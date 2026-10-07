package com.clinica.clinica_backend.controller;

import com.clinica.clinica_backend.dto.EstadoRequest;
import com.clinica.clinica_backend.dto.LoginRequest;
import com.clinica.clinica_backend.dto.LoginResponse;
import com.clinica.clinica_backend.dto.PasswordRequest;
import com.clinica.clinica_backend.dto.UsuarioActualizarRequest;
import com.clinica.clinica_backend.dto.UsuarioRequest;
import com.clinica.clinica_backend.dto.UsuarioResponse;
import java.util.List;
import com.clinica.clinica_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginResponse response = usuarioService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {

        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable int id) {

        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {

        UsuarioResponse response = usuarioService.crear(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable int id,
            @Valid @RequestBody UsuarioActualizarRequest request) {

        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable int id,
            @Valid @RequestBody EstadoRequest request) {

        return ResponseEntity.ok(usuarioService.cambiarEstado(id, request.estado()));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable int id,
            @Valid @RequestBody PasswordRequest request) {

        usuarioService.cambiarPassword(id, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> perfil(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "correo", authentication.getName(),
                "roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList()
        ));
    }
}