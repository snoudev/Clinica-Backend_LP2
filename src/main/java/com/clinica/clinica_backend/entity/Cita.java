package com.clinica.clinica_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private int idCita;

    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_medico")
    private Empleado medico;

    @ManyToOne
    @JoinColumn(name = "id_usuario_registro")
    private Usuario usuarioRegistro;

    @ManyToOne
    @JoinColumn(name = "id_estado_cita")
    private EstadoCita estadoCita;

    @Column(name = "fecha_hora_cita")
    private LocalDateTime fechaHoraCita;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public Cita() {
    }

    public Cita(int idCita, Paciente paciente, Empleado medico,
                Usuario usuarioRegistro, EstadoCita estadoCita,
                LocalDateTime fechaHoraCita, LocalDateTime fechaRegistro) {
        this.idCita = idCita;
        this.paciente = paciente;
        this.medico = medico;
        this.usuarioRegistro = usuarioRegistro;
        this.estadoCita = estadoCita;
        this.fechaHoraCita = fechaHoraCita;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Empleado getMedico() {
        return medico;
    }

    public void setMedico(Empleado medico) {
        this.medico = medico;
    }

    public Usuario getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuario usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public EstadoCita getEstadoCita() {
        return estadoCita;
    }

    public void setEstadoCita(EstadoCita estadoCita) {
        this.estadoCita = estadoCita;
    }

    public LocalDateTime getFechaHoraCita() {
        return fechaHoraCita;
    }

    public void setFechaHoraCita(LocalDateTime fechaHoraCita) {
        this.fechaHoraCita = fechaHoraCita;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}