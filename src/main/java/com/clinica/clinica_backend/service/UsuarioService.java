package com.clinica.clinica_backend.service;

import com.clinica.clinica_backend.dto.LoginRequest;
import com.clinica.clinica_backend.dto.LoginResponse;
import com.clinica.clinica_backend.dto.PasswordRequest;
import com.clinica.clinica_backend.dto.UsuarioActualizarRequest;
import com.clinica.clinica_backend.dto.UsuarioRequest;
import com.clinica.clinica_backend.dto.UsuarioResponse;
import com.clinica.clinica_backend.entity.Empleado;
import com.clinica.clinica_backend.entity.Rol;
import com.clinica.clinica_backend.entity.Usuario;
import com.clinica.clinica_backend.exception.ConflictoException;
import com.clinica.clinica_backend.exception.RecursoNoEncontradoException;
import com.clinica.clinica_backend.exception.SolicitudInvalidaException;
import com.clinica.clinica_backend.repository.EmpleadoRepository;
import com.clinica.clinica_backend.repository.RolRepository;
import com.clinica.clinica_backend.repository.UsuarioRepository;
import com.clinica.clinica_backend.security.JwtService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

	private static final String CARGO_RECEPCIONISTA = "RECEPCIONISTA";
	private static final String CARGO_ADMINISTRATIVO = "ADMINISTRATIVO";
	
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            EmpleadoRepository empleadoRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        String correo = request.getCorreo().trim();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        correo,
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByCorreo(correo).orElseThrow();

        String token = jwtService.generarToken(
                usuario.getCorreo(),
                usuario.getRol().getNombreRol()
        );

        return new LoginResponse(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getRol().getNombreRol(),
                usuario.getEmpleado().getNombre(),
                usuario.getEmpleado().getApellido(),
                token
        );
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::aRespuesta)
                .toList();
    }

    public UsuarioResponse buscarPorId(int id) {
        return aRespuesta(buscarEntidad(id));
    }

    public UsuarioResponse crear(UsuarioRequest request) {

        Empleado empleado = empleadoRepository.findById(request.idEmpleado())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un empleado con ese id."));

        if (!empleado.isEstado()) {
            throw new SolicitudInvalidaException(
                    "No se puede crear un usuario para un empleado inactivo."
            );
        }

        String nombreCargo = empleado.getCargo().getNombreCargo();

        if (!CARGO_RECEPCIONISTA.equalsIgnoreCase(nombreCargo)
                && !CARGO_ADMINISTRATIVO.equalsIgnoreCase(nombreCargo)) {

            throw new SolicitudInvalidaException(
                    "Solo los empleados de recepción o administración pueden tener un usuario."
            );
        }
        
        Rol rol = rolRepository.findById(request.idRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un rol con ese id."));

        String correo = request.correo().trim();

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictoException("Ya existe un usuario con ese correo.");
        }

        if (usuarioRepository.existsByEmpleadoIdEmpleado(request.idEmpleado())) {
            throw new ConflictoException("Ese empleado ya tiene un usuario asignado.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmpleado(empleado);
        usuario.setRol(rol);
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(passwordEncoder.encode(request.password()));
        usuario.setEstado(true);
        usuario.setCreadoEn(LocalDateTime.now());

        return aRespuesta(usuarioRepository.save(usuario));
    }

    public UsuarioResponse actualizar(int id, UsuarioActualizarRequest request) {

        Usuario usuario = buscarEntidad(id);

        Rol rol = rolRepository.findById(request.idRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un rol con ese id."));

        String correo = request.correo().trim();

        if (usuarioRepository.existsByCorreoAndIdUsuarioNot(correo, id)) {
            throw new ConflictoException("Ya existe un usuario con ese correo.");
        }

        usuario.setRol(rol);
        usuario.setCorreo(correo);

        return aRespuesta(usuarioRepository.save(usuario));
    }

    public UsuarioResponse cambiarEstado(int id, boolean estado) {

        Usuario usuario = buscarEntidad(id);
        usuario.setEstado(estado);

        return aRespuesta(usuarioRepository.save(usuario));
    }

    public void cambiarPassword(int id, PasswordRequest request) {

        Usuario usuario = buscarEntidad(id);
        usuario.setContrasenaHash(passwordEncoder.encode(request.password()));

        usuarioRepository.save(usuario);
    }

    private Usuario buscarEntidad(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con id " + id + "."));
    }

    private UsuarioResponse aRespuesta(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getEmpleado().getIdEmpleado(),
                usuario.getEmpleado().getNombre() + " " + usuario.getEmpleado().getApellido(),
                usuario.getCorreo(),
                usuario.getRol().getNombreRol(),
                usuario.isEstado(),
                usuario.getCreadoEn()
        );
    }
}