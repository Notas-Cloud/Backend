package com.upao.notas.web.controller;

import com.upao.notas.domain.entity.Nota;
import com.upao.notas.domain.entity.Usuario;
import com.upao.notas.infra.repository.UsuarioRepository;
import com.upao.notas.infra.security.LoginRequest;
import com.upao.notas.infra.security.TokenResponse;
import com.upao.notas.services.NotaService;
import com.upao.notas.services.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/usuario")
@CrossOrigin("*") // Permite solicitudes de cualquier origen
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final NotaService notaService;


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = usuarioService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/registrar")
    @Transactional
    public ResponseEntity<TokenResponse> addUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.addUsuario(usuario));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.getUsuarioById(id).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
