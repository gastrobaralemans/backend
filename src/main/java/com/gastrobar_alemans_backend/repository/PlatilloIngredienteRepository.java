package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.MenuItemMODEL;
import com.gastrobar_alemans_backend.model.PlatilloIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatilloIngredienteRepository extends JpaRepository<PlatilloIngrediente, Long> {
    List<PlatilloIngrediente> findByPlatillo(MenuItemMODEL platillo);

    List<PlatilloIngrediente> findByPlatilloId(Long platilloId);

    List<PlatilloIngrediente> findByIngredienteId(Long id);
}
