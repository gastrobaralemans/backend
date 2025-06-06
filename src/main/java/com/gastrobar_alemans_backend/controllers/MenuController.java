package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.DTO.MenuCategoryDTO;
import com.gastrobar_alemans_backend.DTO.PromoDTO;
import com.gastrobar_alemans_backend.service.MenuService;
import com.gastrobar_alemans_backend.model.MenuItemMODEL;
import com.gastrobar_alemans_backend.model.MenuItemPromo;
import com.gastrobar_alemans_backend.repository.MenuItemRepository;
import com.gastrobar_alemans_backend.repository.MenuItemPromoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemPromoRepository promoRepository;

    @GetMapping
    public ResponseEntity<List<MenuCategoryDTO>> getMenu() {
        List<MenuCategoryDTO> menu = menuService.getFullMenu();
        return ResponseEntity.ok(menu);
    }
    @GetMapping("/platillos")
    public List<MenuItemMODEL> getSoloPlatillos() {
        return menuItemRepository.findAll().stream()
                .filter(MenuItemMODEL::isActive)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/promo")
    public ResponseEntity<?> updatePromo(@PathVariable Long id, @RequestBody PromoDTO promoDTO) {
        Optional<MenuItemMODEL> itemOpt = menuItemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Platillo no encontrado.");
        }
        MenuItemMODEL item = itemOpt.get();
        MenuItemPromo promo = promoRepository.findByMenuItemId(item.getId()).orElse(new MenuItemPromo());
        promo.setMenuItem(item);
        promo.setPromoPrice(promoDTO.getPromoPrice());
        promo.setPromoDescription(promoDTO.getPromoDescription());
        promo.setStartDate(promoDTO.getPromoStartDate());
        promo.setEndDate(promoDTO.getPromoEndDate());
        promo.setActive(true);

        promoRepository.save(promo);
        return ResponseEntity.ok("Promoción creada/actualizada.");
    }
    @DeleteMapping("/{id}/promo")
    public ResponseEntity<?> deletePromo(@PathVariable Long id) {
        promoRepository.deleteByMenuItemId(id);
        return ResponseEntity.ok("Haz eliminado la promoción");
    }

}
