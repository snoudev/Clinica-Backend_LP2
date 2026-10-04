package com.clinica.clinica_backend.controller;

import com.clinica.clinica_backend.dto.LoginRequest;
import com.clinica.clinica_backend.dto.LoginResponse;
import com.clinica.clinica_backend.service.UsuarioService;
import jakarta.validation.Valid;
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

    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> perfil(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "correo", authentication.getName(),
                "roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList()
        ));
    }
}

