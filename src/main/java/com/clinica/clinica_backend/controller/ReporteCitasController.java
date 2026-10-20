package com.clinica.clinica_backend.controller;

import com.clinica.clinica_backend.dto.ReporteCitasDetalleResponse;
import com.clinica.clinica_backend.dto.ReporteCitasResumenResponse;
import com.clinica.clinica_backend.service.ReporteCitasService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes/citas")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ReporteCitasController {

	private final ReporteCitasService reporteCitasService;

	public ReporteCitasController(ReporteCitasService reporteCitasService) {
		this.reporteCitasService = reporteCitasService;
	}

	@GetMapping("/resumen")
	public ReporteCitasResumenResponse obtenerResumen(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,

			@RequestParam(required = false) Integer idEspecialidad,

			@RequestParam(required = false) Integer idMedico) {

		return reporteCitasService.obtenerResumen(desde, hasta, idEspecialidad, idMedico);
	}

	@GetMapping("/detalle")
	public List<ReporteCitasDetalleResponse> obtenerDetalle(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,

			@RequestParam(required = false) Integer idEspecialidad,

			@RequestParam(required = false) Integer idMedico,

			@RequestParam(required = false) String estado) {

		return reporteCitasService.obtenerDetalle(desde, hasta, idEspecialidad, idMedico, estado);
	}
}