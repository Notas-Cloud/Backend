package com.upao.notas.web.controller;

import com.upao.notas.domain.entity.Nota;
import com.upao.notas.domain.entity.Usuario;
import com.upao.notas.services.NotaService;
import com.upao.notas.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nota")
@CrossOrigin("*")
@RequiredArgsConstructor
public class NotaController {
    private final NotaService notaService; 
    private final UsuarioService usuarioService;
    

    @GetMapping("/vernotas")
    public ResponseEntity<List<Nota>> getNotasDelUsuarioAutenticado() {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var usuario = usuarioService.findByUsername(username).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );

        // Obtener todas las notas y filtrar solo las del usuario autenticado
        List<Nota> notas = notaService.getAllNotas().stream()
                .filter(nota -> nota.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(notas);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Nota> getNotaById(@PathVariable Long id) {
        Nota nota = notaService.getNotaById(id).orElse(null);
        if (nota == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nota);
    }

    @PostMapping("/registrar")
    public ResponseEntity<Nota> addNota(@RequestBody Nota nota) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioService.findByUsername(username).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );

        Nota nuevaNota = notaService.addNota(
                nota.getTitulo(),
                nota.getDescripcion(),
                usuario,
                nota.getImagenBase64()  // Aquí se pasa el argumento de la imagen, que puede ser nulo
        );

        return ResponseEntity.ok(nuevaNota);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Nota> updateNota(@PathVariable Long id, @RequestBody Nota notaActualizada) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioService.findByUsername(username).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );

        Nota notaExistente = notaService.getNotaById(id).orElseThrow(
                () -> new RuntimeException("Nota no encontrada")
        );

        if (!notaExistente.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).body(null); // Retorna un 403 si el usuario no tiene permiso para actualizar
        }

        // Llamar al método `updateNota` con cuatro argumentos
        Nota notaActualizadaResponse = notaService.updateNota(
                id,
                notaActualizada.getTitulo(),
                notaActualizada.getDescripcion(),
                notaActualizada.getImagenBase64()  // Puede ser nulo
        );

        return ResponseEntity.ok(notaActualizadaResponse);
    }


    private String convertirImagenABase64(String imagen) {
        // Lógica para convertir la imagen a Base64, si es necesario
        return imagen; // Aquí puedes implementar la conversión si se recibe la imagen en un formato diferente
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> deleteNotaById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioService.findByUsername(username).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );

        notaService.deleteNotaById(id, usuario.getId()); // Pasa ambos argumentos
        return ResponseEntity.ok().build();
    }

}
