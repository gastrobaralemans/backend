package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.DTO.MenuCategoryDTO;
import com.gastrobar_alemans_backend.DTO.MenuItemDTO;
import com.gastrobar_alemans_backend.DTO.PromoDTO;
import com.gastrobar_alemans_backend.service.MenuService;
import com.gastrobar_alemans_backend.model.MenuItemMODEL;
import com.gastrobar_alemans_backend.model.MenuItemPromo;
import com.gastrobar_alemans_backend.repository.MenuItemRepository;
import com.gastrobar_alemans_backend.repository.MenuItemPromoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuItemPromoRepository promoRepository;

    @InjectMocks
    private MenuController menuController;

    private MenuItemMODEL menuItem;
    private MenuItemMODEL menuItem2;
    private MenuCategoryDTO categoryDTO;
    private PromoDTO promoDTO;

    @BeforeEach
    void setUp() {
        menuItem = new MenuItemMODEL();
        menuItem.setId(1L);
        menuItem.setName("Hamburguesa");
        menuItem.setDescription("Deliciosa hamburguesa");
        menuItem.setPrice(BigDecimal.valueOf(12.99));
        menuItem.setImageUrl("hamburguesa.jpg");
        menuItem.setActive(true);

        menuItem2 = new MenuItemMODEL();
        menuItem2.setId(2L);
        menuItem2.setName("Pizza");
        menuItem2.setDescription("Pizza margarita");
        menuItem2.setPrice(BigDecimal.valueOf(15.99));
        menuItem2.setImageUrl("pizza.jpg");
        menuItem2.setActive(false);

        List<MenuItemDTO> menuItemDTOs = Arrays.asList(
                new MenuItemDTO(
                        menuItem.getId(),
                        menuItem.getName(),
                        menuItem.getDescription(),
                        menuItem.getImageUrl(),
                        menuItem.getPrice(),
                        null,
                        null,
                        null,
                        null
                ),
                new MenuItemDTO(
                        menuItem2.getId(),
                        menuItem2.getName(),
                        menuItem2.getDescription(),
                        menuItem2.getImageUrl(),
                        menuItem2.getPrice(),
                        null,
                        null,
                        null,
                        null
                )
        );

        categoryDTO = new MenuCategoryDTO("Platos Fuertes", menuItemDTOs);

        promoDTO = new PromoDTO();
        promoDTO.setPromoPrice(BigDecimal.valueOf(15.99));
        promoDTO.setPromoDescription("Promoción especial");
        promoDTO.setPromoStartDate(LocalDateTime.now());
        promoDTO.setPromoEndDate(LocalDateTime.now().plusDays(7));
    }

    @Test
    void testGetMenu_Success() {
        List<MenuCategoryDTO> menuCategories = Arrays.asList(categoryDTO);
        when(menuService.getFullMenu()).thenReturn(menuCategories);

        ResponseEntity<List<MenuCategoryDTO>> response = menuController.getMenu();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Platos Fuertes", response.getBody().get(0).getName());
        verify(menuService).getFullMenu();
    }

    @Test
    void testGetMenu_Empty() {
        when(menuService.getFullMenu()).thenReturn(Arrays.asList());

        ResponseEntity<List<MenuCategoryDTO>> response = menuController.getMenu();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(menuService).getFullMenu();
    }

    @Test
    void testGetSoloPlatillos_Success() {
        List<MenuItemMODEL> items = Arrays.asList(menuItem, menuItem2);
        when(menuItemRepository.findAll()).thenReturn(items);

        List<MenuItemMODEL> result = menuController.getSoloPlatillos();

        assertEquals(1, result.size());
        assertEquals("Hamburguesa", result.get(0).getName());
        verify(menuItemRepository).findAll();
    }

    @Test
    void testGetSoloPlatillos_Empty() {
        when(menuItemRepository.findAll()).thenReturn(Arrays.asList());

        List<MenuItemMODEL> result = menuController.getSoloPlatillos();

        assertTrue(result.isEmpty());
        verify(menuItemRepository).findAll();
    }

    @Test
    void testGetSoloPlatillos_OnlyActive() {
        MenuItemMODEL inactiveItem = new MenuItemMODEL();
        inactiveItem.setId(3L);
        inactiveItem.setName("Ensalada");
        inactiveItem.setActive(false);

        List<MenuItemMODEL> items = Arrays.asList(menuItem, inactiveItem);
        when(menuItemRepository.findAll()).thenReturn(items);

        List<MenuItemMODEL> result = menuController.getSoloPlatillos();

        assertEquals(1, result.size());
        assertEquals("Hamburguesa", result.get(0).getName());
        verify(menuItemRepository).findAll();
    }

    @Test
    void testUpdatePromo_Success_NewPromo() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(promoRepository.findByMenuItemId(1L)).thenReturn(Optional.empty());
        when(promoRepository.save(any(MenuItemPromo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = menuController.updatePromo(1L, promoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Promoción creada/actualizada.", response.getBody());
        verify(menuItemRepository).findById(1L);
        verify(promoRepository).findByMenuItemId(1L);
        verify(promoRepository).save(any(MenuItemPromo.class));
    }

    @Test
    void testUpdatePromo_Success_ExistingPromo() {
        MenuItemPromo existingPromo = new MenuItemPromo();
        existingPromo.setMenuItem(menuItem);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(promoRepository.findByMenuItemId(1L)).thenReturn(Optional.of(existingPromo));
        when(promoRepository.save(any(MenuItemPromo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = menuController.updatePromo(1L, promoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Promoción creada/actualizada.", response.getBody());
        verify(menuItemRepository).findById(1L);
        verify(promoRepository).findByMenuItemId(1L);
        verify(promoRepository).save(any(MenuItemPromo.class));
    }

    @Test
    void testUpdatePromo_ItemNotFound() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = menuController.updatePromo(99L, promoDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Platillo no encontrado.", response.getBody());
        verify(menuItemRepository).findById(99L);
        verify(promoRepository, never()).findByMenuItemId(anyLong());
        verify(promoRepository, never()).save(any(MenuItemPromo.class));
    }

    @Test
    void testUpdatePromo_PromoFieldsSetCorrectly() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(promoRepository.findByMenuItemId(1L)).thenReturn(Optional.empty());
        when(promoRepository.save(any(MenuItemPromo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = menuController.updatePromo(1L, promoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(promoRepository).save(argThat(promo -> {
            assertEquals(menuItem, promo.getMenuItem());
            assertEquals(promoDTO.getPromoPrice(), promo.getPromoPrice());
            assertEquals(promoDTO.getPromoDescription(), promo.getPromoDescription());
            assertEquals(promoDTO.getPromoStartDate(), promo.getStartDate());
            assertEquals(promoDTO.getPromoEndDate(), promo.getEndDate());
            assertTrue(promo.isActive());
            return true;
        }));
    }

    @Test
    void testDeletePromo_Success() {
        doNothing().when(promoRepository).deleteByMenuItemId(1L);

        ResponseEntity<?> response = menuController.deletePromo(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Haz eliminado la promoción", response.getBody());
        verify(promoRepository).deleteByMenuItemId(1L);
    }

    @Test
    void testDeletePromo_MultipleCalls() {
        doNothing().when(promoRepository).deleteByMenuItemId(anyLong());

        menuController.deletePromo(1L);
        menuController.deletePromo(2L);
        menuController.deletePromo(3L);

        verify(promoRepository, times(3)).deleteByMenuItemId(anyLong());
    }

    @Test
    void testUpdatePromo_NullValues() {
        PromoDTO nullPromoDTO = new PromoDTO();
        nullPromoDTO.setPromoPrice(null);
        nullPromoDTO.setPromoDescription(null);
        nullPromoDTO.setPromoStartDate(null);
        nullPromoDTO.setPromoEndDate(null);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(promoRepository.findByMenuItemId(1L)).thenReturn(Optional.empty());
        when(promoRepository.save(any(MenuItemPromo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = menuController.updatePromo(1L, nullPromoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(promoRepository).save(argThat(promo -> {
            assertEquals(menuItem, promo.getMenuItem());
            assertNull(promo.getPromoPrice());
            assertNull(promo.getPromoDescription());
            assertNull(promo.getStartDate());
            assertNull(promo.getEndDate());
            assertTrue(promo.isActive());
            return true;
        }));
    }

    @Test
    void testUpdatePromo_ZeroPrice() {
        PromoDTO zeroPricePromo = new PromoDTO();
        zeroPricePromo.setPromoPrice(BigDecimal.ZERO);
        zeroPricePromo.setPromoDescription("Precio cero");
        zeroPricePromo.setPromoStartDate(LocalDateTime.now());
        zeroPricePromo.setPromoEndDate(LocalDateTime.now().plusDays(1));

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(promoRepository.findByMenuItemId(1L)).thenReturn(Optional.empty());
        when(promoRepository.save(any(MenuItemPromo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = menuController.updatePromo(1L, zeroPricePromo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(promoRepository).save(argThat(promo -> {
            assertEquals(BigDecimal.ZERO, promo.getPromoPrice());
            assertEquals("Precio cero", promo.getPromoDescription());
            return true;
        }));
    }
}