package com.clinica.clinica_backend.controller;

import com.clinica.clinica_backend.dto.DisponibilidadResponse;
import com.clinica.clinica_backend.service.CitaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/citas")
public class CitaController {

	private final CitaService citaService;

	public CitaController(CitaService citaService) {
		this.citaService = citaService;
	}

	@GetMapping("/disponibilidad")
	public List<DisponibilidadResponse> consultarDisponibilidad(@RequestParam int idEspecialidad,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

		return citaService.consultarDisponibilidad(idEspecialidad, fecha);
	}
}