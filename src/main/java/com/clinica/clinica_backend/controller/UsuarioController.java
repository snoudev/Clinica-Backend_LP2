package com.clinica.clinica_backend.controller;

import com.clinica.clinica_backend.dto.EstadoRequest;
import com.clinica.clinica_backend.dto.LoginRequest;
import com.clinica.clinica_backend.dto.LoginResponse;
import com.clinica.clinica_backend.dto.PasswordRequest;
import com.clinica.clinica_backend.dto.UsuarioActualizarRequest;
import com.clinica.clinica_backend.dto.UsuarioRequest;
import com.clinica.clinica_backend.dto.UsuarioResponse;
import com.clinica.clinica_backend.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@GetMapping
	public ResponseEntity<List<UsuarioResponse>> listar() {

		return ResponseEntity.ok(usuarioService.listar());
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable int id) {

		return ResponseEntity.ok(usuarioService.buscarPorId(id));
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@PostMapping
	public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {

		UsuarioResponse response = usuarioService.crear(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@PutMapping("/{id}")
	public ResponseEntity<UsuarioResponse> actualizar(@PathVariable int id,
			@Valid @RequestBody UsuarioActualizarRequest request) {

		return ResponseEntity.ok(usuarioService.actualizar(id, request));
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@PatchMapping("/{id}/estado")
	public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable int id,
			@Valid @RequestBody EstadoRequest request) {

		return ResponseEntity.ok(usuarioService.cambiarEstado(id, request.estado()));
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@PatchMapping("/{id}/password")
	public ResponseEntity<Void> cambiarPassword(@PathVariable int id, @Valid @RequestBody PasswordRequest request) {

		usuarioService.cambiarPassword(id, request);

		return ResponseEntity.noContent().build();
	}
}