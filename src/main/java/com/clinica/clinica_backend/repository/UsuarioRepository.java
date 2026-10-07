package com.clinica.clinica_backend.repository;

import com.clinica.clinica_backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByEmpleadoIdEmpleado(int idEmpleado);

    boolean existsByCorreoAndIdUsuarioNot(String correo, int idUsuario);
}