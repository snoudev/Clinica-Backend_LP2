package com.clinica.clinica_backend.repository;

import java.time.LocalDate;

public class ReporteCitasAgrupacionProjection {

    public interface PorEspecialidad {
        Integer getIdEspecialidad();
        String getEspecialidad();
        Long getTotal();
        Long getPendientes();
        Long getAtendidas();
        Long getCanceladas();
        Long getNoAsistidas();
    }

    public interface PorMedico {
        Integer getIdMedico();
        String getMedico();
        String getEspecialidad();
        String getTurno();
        Long getTotal();
        Long getPendientes();
        Long getAtendidas();
        Long getCanceladas();
        Long getNoAsistidas();
    }

    public interface PorDia {
        LocalDate getFecha();
        Long getTotal();
        Long getPendientes();
        Long getAtendidas();
        Long getCanceladas();
        Long getNoAsistidas();
    }

    public interface PorUsuario {
        Integer getIdUsuario();
        String getNombre();
        Long getTotal();
    }
}
