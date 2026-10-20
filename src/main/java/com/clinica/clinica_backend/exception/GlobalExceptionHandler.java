package com.clinica.clinica_backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Map<String, String>> credencialesInvalidas(AuthenticationException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("mensaje", "Correo o contraseña incorrectos"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> datosInvalidos(MethodArgumentNotValidException e) {

		String mensaje = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", mensaje));
	}

	@ExceptionHandler(ConflictoException.class)
	public ResponseEntity<Map<String, String>> conflicto(ConflictoException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", e.getMessage()));
	}

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<Map<String, String>> recursoNoEncontrado(RecursoNoEncontradoException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
	}

	@ExceptionHandler(SolicitudInvalidaException.class)
	public ResponseEntity<Map<String, String>> solicitudInvalida(SolicitudInvalidaException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> cuerpoIlegible(HttpMessageNotReadableException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("mensaje", "El cuerpo de la petición no tiene un formato válido"));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> integridadDatos(DataIntegrityViolationException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(Map.of("mensaje", "La operación no es válida porque entra en conflicto con datos existentes"));
	}
}