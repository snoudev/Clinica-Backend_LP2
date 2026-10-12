package com.clinica.clinica_backend.repository;

public interface ReporteCitasTotalesProjection {

	Long getTotal();
	Long getPendientes();
	Long getAtendidas();
	Long getCanceladas();
	Long getNoAsistidas();
	Long getPacientesDistintos();
	Long getMedicosConCitas();
}
