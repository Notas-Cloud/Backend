package com.upao.notas.services;

import com.upao.notas.domain.entity.Rol;
import com.upao.notas.domain.entity.Usuario;
import com.upao.notas.infra.repository.UsuarioRepository;
import com.upao.notas.infra.security.JwtService;
import com.upao.notas.infra.security.LoginRequest;
import com.upao.notas.infra.security.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Usuario user = usuarioRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );
        String token = jwtService.generateToken(new HashMap<>(), user.getUsername());
        return new TokenResponse(token); // Reemplaza el uso de 'builder' por un constructor o método alternativo
    }


    public TokenResponse addUsuario(Usuario usuario) {
        Usuario user = Usuario.builder()
                .username(usuario.getUsername())
                .password(passwordEncoder.encode( usuario.getPassword()))
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .telefono(usuario.getTelefono())
                .correo(usuario.getCorreo())
                .dni(usuario.getDni())
                .rol(Rol.USER)
                .build();

        usuarioRepository.save(user);

        String token = jwtService.getToken(user, user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public void cambiarRolUsuario(Long usuarioId, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void updateUsuario(Usuario usuario, Long id) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );

        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setApellido(usuario.getApellido());
        usuarioExistente.setCorreo(usuario.getCorreo());
        usuarioExistente.setTelefono(usuario.getTelefono());
        usuarioExistente.setDni(usuario.getDni());
        usuarioExistente.setRol(usuario.getRol());

        usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
