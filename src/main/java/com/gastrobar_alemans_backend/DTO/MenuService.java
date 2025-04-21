package com.gastrobar_alemans_backend.DTO;

import com.gastrobar_alemans_backend.model.MenuCategory;
import com.gastrobar_alemans_backend.model.MenuItem;
import com.gastrobar_alemans_backend.model.MenuItemPromo;
import com.gastrobar_alemans_backend.repository.MenuCategoryRepository;
import com.gastrobar_alemans_backend.repository.MenuItemPromoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                    .filter(MenuItem::isActive)
                    .map(item -> {
                        Optional<MenuItemPromo> promo = promoRepository.findActivePromo(item.getId());
                        BigDecimal promoPrice = promo.map(MenuItemPromo::getPromoPrice).orElse(null);

                        String imageUrl = "/menu/" + category.getName().toLowerCase() + "/" + item.getImageUrl();

                        return new MenuItemDTO(
                                item.getName(),
                                item.getDescription(),
                                imageUrl,
                                item.getPrice(),
                                promoPrice
                        );
                    }).toList();

            return new MenuCategoryDTO(category.getName(), items);
        }).toList();
    }
}
