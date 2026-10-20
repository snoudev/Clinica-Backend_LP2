package com.clinica.clinica_backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String claveSecreta;

    public String generarToken(String correo, String rol) {

        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(claveSecreta.getBytes()))
                .compact();
    }

    public String extraerCorreo(String token) {

        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(claveSecreta.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validarToken(String token, String correo) {

        try {
            String correoDelToken = extraerCorreo(token);

            return correoDelToken.equals(correo)
                    && !estaExpirado(token);

        } catch (Exception e) {
            return false;
        }
    }

    private boolean estaExpirado(String token) {

        Date fechaExpiracion = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(claveSecreta.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return fechaExpiracion.before(new Date());
    }
} 
