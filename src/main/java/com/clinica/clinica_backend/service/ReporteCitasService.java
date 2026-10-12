package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.dto.ReporteCitasDetalleResponse;
import com.clinica.clinica_backend.dto.ReporteCitasResumenResponse;
import com.clinica.clinica_backend.repository.CitaRepository;
import com.clinica.clinica_backend.repository.ReporteCitasDetalleProjection;
import com.clinica.clinica_backend.repository.ReporteCitasTotalesProjection;
import org.springframework.stereotype.Service;
import com.clinica.clinica_backend.exception.SolicitudInvalidaException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteCitasService {

	private final CitaRepository citaRepository;

	public ReporteCitasService(CitaRepository citaRepository) {
		this.citaRepository = citaRepository;
	}

	public ReporteCitasResumenResponse obtenerResumen(LocalDate desde, LocalDate hasta, Integer idEspecialidad,
			Integer idMedico) {

		if (desde == null || hasta == null) {
		    throw new SolicitudInvalidaException(
		        "Las fechas desde y hasta son obligatorias."
		    );
		}

		if (desde.isAfter(hasta)) {
		    throw new SolicitudInvalidaException(
		        "La fecha desde no puede ser posterior a hasta."
		    );
		}

		LocalDateTime inicio = desde.atStartOfDay();
		LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

		ReporteCitasTotalesProjection resultado = citaRepository.obtenerTotalesReporte(inicio, fin, idEspecialidad,
				idMedico);

		ReporteCitasResumenResponse.Totales totales = new ReporteCitasResumenResponse.Totales(resultado.getTotal(),
				resultado.getPendientes(), resultado.getAtendidas(), resultado.getCanceladas(),
				resultado.getNoAsistidas(), resultado.getPacientesDistintos(), resultado.getMedicosConCitas());

		List<ReporteCitasResumenResponse.PorEspecialidad> porEspecialidad = new ArrayList<>();

		for (var r : citaRepository.obtenerCitasPorEspecialidadReporte(inicio, fin, idEspecialidad, idMedico)) {

			porEspecialidad
					.add(new ReporteCitasResumenResponse.PorEspecialidad(r.getIdEspecialidad(), r.getEspecialidad(),
							r.getTotal(), r.getPendientes(), r.getAtendidas(), r.getCanceladas(), r.getNoAsistidas()));
		}

		List<ReporteCitasResumenResponse.PorMedico> porMedico = new ArrayList<>();

		for (var r : citaRepository.obtenerCitasPorMedicoReporte(inicio, fin, idEspecialidad, idMedico)) {

			porMedico.add(new ReporteCitasResumenResponse.PorMedico(r.getIdMedico(), r.getMedico(), r.getEspecialidad(),
					r.getTurno(), r.getTotal(), r.getPendientes(), r.getAtendidas(), r.getCanceladas(),
					r.getNoAsistidas()));
		}

		List<ReporteCitasResumenResponse.PorDia> porDia = new ArrayList<>();

		for (var r : citaRepository.obtenerCitasPorDiaReporte(inicio, fin, idEspecialidad, idMedico)) {

			porDia.add(new ReporteCitasResumenResponse.PorDia(r.getFecha(), r.getTotal(), r.getPendientes(),
					r.getAtendidas(), r.getCanceladas(), r.getNoAsistidas()));
		}

		List<ReporteCitasResumenResponse.PorUsuario> porUsuario = new ArrayList<>();

		for (var r : citaRepository.obtenerCitasPorUsuarioReporte(inicio, fin, idEspecialidad, idMedico)) {

			porUsuario.add(new ReporteCitasResumenResponse.PorUsuario(r.getIdUsuario(), r.getNombre(), r.getTotal()));
		}

		return new ReporteCitasResumenResponse(desde, hasta, totales, porEspecialidad, porMedico, porDia, porUsuario);
	}

	public List<ReporteCitasDetalleResponse> obtenerDetalle(LocalDate desde, LocalDate hasta, Integer idEspecialidad,
			Integer idMedico, String estado) {

		if (desde == null || hasta == null) {
		    throw new SolicitudInvalidaException(
		        "Las fechas desde y hasta son obligatorias."
		    );
		}

		if (desde.isAfter(hasta)) {
		    throw new SolicitudInvalidaException(
		        "La fecha desde no puede ser posterior a hasta."
		    );
		}

		LocalDateTime inicio = desde.atStartOfDay();
		LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

		List<ReporteCitasDetalleResponse> detalles = new ArrayList<>();

		List<ReporteCitasDetalleProjection> resultados = citaRepository.obtenerDetalleReporte(inicio, fin,
				idEspecialidad, idMedico, estado);

		for (ReporteCitasDetalleProjection r : resultados) {
			detalles.add(new ReporteCitasDetalleResponse(r.getIdCita(), r.getFechaHoraCita(), r.getPaciente(),
					r.getDniPaciente(), r.getMedico(), r.getEspecialidad(), r.getEstado(), r.getRegistradoPor(),
					r.getFechaRegistro()));
		}

		return detalles;
	}
}