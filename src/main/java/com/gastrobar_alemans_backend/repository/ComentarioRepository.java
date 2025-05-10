package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.Comentario;
import com.gastrobar_alemans_backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByPostOrderByFechaDesc(Post post);
}
