package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    @Query("SELECT c FROM MenuCategory c JOIN FETCH c.items i WHERE i.active = true")
    List<MenuCategory> findAllWithItems();
}
