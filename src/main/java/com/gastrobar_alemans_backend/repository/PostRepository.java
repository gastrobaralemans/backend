package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
