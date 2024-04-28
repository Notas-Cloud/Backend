package com.upao.notas.infra.repository;

import com.upao.notas.domain.entity.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findAllByUsuarioId(Long usuarioId);
}