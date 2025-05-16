package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
}
