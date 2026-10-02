package com.clinica.clinica_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_estado_cita")
public class EstadoCita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_cita")
    private int idEstadoCita;

    @Column(name = "nombre_estado")
    private String nombreEstado;

    @Column(name = "descripcion")
    private String descripcion;

    public EstadoCita() {
    }

    public EstadoCita(int idEstadoCita, String nombreEstado, String descripcion) {
        this.idEstadoCita = idEstadoCita;
        this.nombreEstado = nombreEstado;
        this.descripcion = descripcion;
    }

    public int getIdEstadoCita() {
        return idEstadoCita;
    }

    public void setIdEstadoCita(int idEstadoCita) {
        this.idEstadoCita = idEstadoCita;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}