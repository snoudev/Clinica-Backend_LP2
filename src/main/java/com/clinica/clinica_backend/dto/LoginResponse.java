package com.clinica.clinica_backend.dto;

public class LoginResponse {

	private int idUsuario;
	private String correo;
	private String rol;
	private String nombre;
	private String apellido;
	private String token;

	public LoginResponse() {
	}

	public LoginResponse(int idUsuario, String correo, String rol, String nombre, String apellido, String token) {
		this.idUsuario = idUsuario;
		this.correo = correo;
		this.rol = rol;
		this.nombre = nombre;
		this.apellido = apellido;
		this.token = token;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}