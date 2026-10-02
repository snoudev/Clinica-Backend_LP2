package com.clinica.clinica_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private int idPaciente;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "dni")
    private String dni;

    @Column(name = "telefono")
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "registrado_por_usuario")
    private Usuario registradoPorUsuario;

    public Paciente() {
    }

    public Paciente(int idPaciente, String nombre, String apellido,
                    String dni, String telefono, Usuario registradoPorUsuario) {
        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.registradoPorUsuario = registradoPorUsuario;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Usuario getRegistradoPorUsuario() {
        return registradoPorUsuario;
    }

    public void setRegistradoPorUsuario(Usuario registradoPorUsuario) {
        this.registradoPorUsuario = registradoPorUsuario;
    }
}