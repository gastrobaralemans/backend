package com.gastrobar_alemans_backend.service;

import com.gastrobar_alemans_backend.DTO.MenuCategoryDTO;
import com.gastrobar_alemans_backend.DTO.MenuItemDTO;
import com.gastrobar_alemans_backend.model.MenuCategory;
import com.gastrobar_alemans_backend.model.MenuItemMODEL;
import com.gastrobar_alemans_backend.model.MenuItemPromo;
import com.gastrobar_alemans_backend.repository.MenuCategoryRepository;
import com.gastrobar_alemans_backend.repository.MenuItemPromoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuCategoryRepository categoryRepository;
    private final MenuItemPromoRepository promoRepository;

    public List<MenuCategoryDTO> getFullMenu() {
        List<MenuCategory> categories = categoryRepository.findAllWithItems();

        return categories.stream().map(category -> {
            List<MenuItemDTO> items = category.getItems().stream()
                    .filter(MenuItemMODEL::isActive)
                    .map(item -> {
                        Optional<MenuItemPromo> promoOpt = promoRepository.findActivePromo(item.getId());

                        BigDecimal promoPrice = null;
                        String promoDescription = null;
                        LocalDateTime startDate = null;
                        LocalDateTime endDate = null;


                        if (promoOpt.isPresent()) {
                            MenuItemPromo promo = promoOpt.get();
                            LocalDateTime now = LocalDateTime.now();
                            if (!now.isBefore(promo.getStartDate()) && !now.isAfter(promo.getEndDate())) {
                                promoPrice = promo.getPromoPrice();
                                promoDescription = promo.getPromoDescription();
                                startDate = promo.getStartDate();
                                endDate = promo.getEndDate();
                            } else if (now.isAfter(promo.getEndDate())){
                                promo.setActive(false);
                                promoRepository.save(promo);
                            }
                        }

                        String imageUrl = "/menu/" + category.getName().toLowerCase() + "/" + item.getImageUrl();

                        return new MenuItemDTO(
                                item.getId(),
                                item.getName(),
                                item.getDescription(),
                                imageUrl,
                                item.getPrice(),
                                promoPrice,
                                promoDescription,
                                startDate,
                                endDate
                        );
                    }).toList();

            return new MenuCategoryDTO(category.getName(), items);
        }).toList();
    }
}
