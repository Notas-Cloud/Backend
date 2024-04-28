package com.upao.notas.services;

import com.upao.notas.domain.entity.Nota;
import com.upao.notas.domain.entity.Usuario;
import com.upao.notas.infra.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotaService {
    private final NotaRepository notaRepository;

    public List<Nota> getAllNotas() {
        return notaRepository.findAll();
    }

    public Optional<Nota> getNotaById(Long id) {
        return notaRepository.findById(id);
    }
    public List<Nota> getNotasPorUsuario(Long usuarioId) {
        return notaRepository.findAllByUsuarioId(usuarioId);
    }

    public Nota addNota(String titulo, String descripcion, Usuario usuario) {
        Nota nuevaNota = new Nota();
        nuevaNota.setTitulo(titulo);
        nuevaNota.setDescripcion(descripcion);
        nuevaNota.setUsuario(usuario);
        nuevaNota.setFechaCreacion(LocalDateTime.now());
        nuevaNota.setFechaActualizacion(LocalDateTime.now());

        return notaRepository.save(nuevaNota);
    }

    public Nota addNota(String titulo, String descripcion, Usuario usuario, String imagenBase64) {
        Nota nuevaNota = new Nota();
        nuevaNota.setTitulo(titulo);
        nuevaNota.setDescripcion(descripcion);
        nuevaNota.setUsuario(usuario);
        nuevaNota.setFechaCreacion(LocalDateTime.now());
        nuevaNota.setFechaActualizacion(LocalDateTime.now());

        if (imagenBase64 != null) {
            nuevaNota.setImagenBase64(imagenBase64);
        }

        return notaRepository.save(nuevaNota);
    }



    @Transactional
    public Nota updateNota(Long id, String titulo, String descripcion, String imagenBase64) {
        Nota notaExistente = notaRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Nota no encontrada")
        );

        notaExistente.setTitulo(titulo);
        notaExistente.setDescripcion(descripcion);

        if (imagenBase64 != null) {
            notaExistente.setImagenBase64(imagenBase64); // Actualizar la imagen si se proporciona
        }

        notaExistente.setFechaActualizacion(LocalDateTime.now()); // Actualizar la fecha de modificación

        return notaRepository.save(notaExistente); // Guardar los cambios
    }
    @Transactional
    public void deleteNotaById(Long notaId, Long usuarioId) {
        Nota notaExistente = notaRepository.findById(notaId).orElseThrow(
                () -> new RuntimeException("Nota no encontrada")
        );

        if (!notaExistente.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta nota.");
        }

        notaRepository.deleteById(notaId);
    }


}
