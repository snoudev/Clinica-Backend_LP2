package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.dto.LoginRequest;
import com.clinica.clinica_backend.dto.LoginResponse;
import com.clinica.clinica_backend.entity.Usuario;
import com.clinica.clinica_backend.repository.UsuarioRepository;
import com.clinica.clinica_backend.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    
    public UsuarioService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
    	
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo()).orElseThrow();

        String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol().getNombreRol());

        return new LoginResponse(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getRol().getNombreRol(),
                usuario.getEmpleado().getNombre(),
                usuario.getEmpleado().getApellido(),
                token
        );
    }
}