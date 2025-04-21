package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.MenuItemPromo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuItemPromoRepository extends JpaRepository<MenuItemPromo, Long> {

    @Query("SELECT p FROM MenuItemPromo p WHERE p.menuItem.id = :menuItemId AND CURRENT_DATE BETWEEN p.startDate AND p.endDate AND p.active = true")
    Optional<MenuItemPromo> findActivePromo(Long menuItemId);
}
