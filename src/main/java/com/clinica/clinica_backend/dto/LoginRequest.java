package com.clinica.clinica_backend.dto;

import jakarta.validation.constraints.*;

public class LoginRequest {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingresa un correo válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}