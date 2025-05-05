package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.MenuItemMODEL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemMODEL, Long> {
}
