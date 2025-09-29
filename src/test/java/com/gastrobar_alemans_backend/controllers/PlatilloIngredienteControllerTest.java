package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.MenuItemMODEL;
import com.gastrobar_alemans_backend.model.PlatilloIngrediente;
import com.gastrobar_alemans_backend.model.Ingrediente;
import com.gastrobar_alemans_backend.repository.MenuItemRepository;
import com.gastrobar_alemans_backend.repository.PlatilloIngredienteRepository;
import com.gastrobar_alemans_backend.repository.IngredienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatilloIngredienteControllerTest {

    @Mock
    private PlatilloIngredienteRepository recetaRepo;

    @Mock
    private MenuItemRepository platilloRepo;

    @Mock
    private IngredienteRepository ingredienteRepo;

    @InjectMocks
    private PlatilloIngredienteController platilloIngredienteController;

    private MenuItemMODEL platillo;
    private Ingrediente ingrediente;
    private PlatilloIngrediente platilloIngrediente;

    @BeforeEach
    void setUp() {
        platillo = new MenuItemMODEL();
        platillo.setId(1L);
        platillo.setName("Hamburguesa");

        ingrediente = new Ingrediente();
        ingrediente.setId(1L);
        ingrediente.setNombre("Pan de hamburguesa");

        platilloIngrediente = new PlatilloIngrediente();
        platilloIngrediente.setId(1L);
        platilloIngrediente.setPlatillo(platillo);
        platilloIngrediente.setIngrediente(ingrediente);
        platilloIngrediente.setCantidadRequerida(2);
    }

    @Test
    void testObtenerPorPlatillo_Success() {
        when(platilloRepo.findById(1L)).thenReturn(Optional.of(platillo));
        when(recetaRepo.findByPlatillo(platillo)).thenReturn(Arrays.asList(platilloIngrediente));

        List<PlatilloIngrediente> result = platilloIngredienteController.obtenerPorPlatillo(1L);

        assertEquals(1, result.size());
        assertEquals(platilloIngrediente, result.get(0));
        verify(platilloRepo).findById(1L);
        verify(recetaRepo).findByPlatillo(platillo);
    }

    @Test
    void testObtenerPorPlatillo_PlatilloNotFound() {
        when(platilloRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            platilloIngredienteController.obtenerPorPlatillo(99L);
        });

        verify(platilloRepo).findById(99L);
        verify(recetaRepo, never()).findByPlatillo(any());
    }

    @Test
    void testObtenerPorPlatillo_EmptyList() {
        when(platilloRepo.findById(1L)).thenReturn(Optional.of(platillo));
        when(recetaRepo.findByPlatillo(platillo)).thenReturn(Arrays.asList());

        List<PlatilloIngrediente> result = platilloIngredienteController.obtenerPorPlatillo(1L);

        assertTrue(result.isEmpty());
        verify(platilloRepo).findById(1L);
        verify(recetaRepo).findByPlatillo(platillo);
    }

    @Test
    void testObtenerPorPlatillo_MultipleIngredients() {
        PlatilloIngrediente platilloIngrediente2 = new PlatilloIngrediente();
        platilloIngrediente2.setId(2L);
        platilloIngrediente2.setPlatillo(platillo);
        platilloIngrediente2.setIngrediente(ingrediente);
        platilloIngrediente2.setCantidadRequerida(1);

        when(platilloRepo.findById(1L)).thenReturn(Optional.of(platillo));
        when(recetaRepo.findByPlatillo(platillo)).thenReturn(Arrays.asList(platilloIngrediente, platilloIngrediente2));

        List<PlatilloIngrediente> result = platilloIngredienteController.obtenerPorPlatillo(1L);

        assertEquals(2, result.size());
        assertEquals(platilloIngrediente, result.get(0));
        assertEquals(platilloIngrediente2, result.get(1));
        verify(platilloRepo).findById(1L);
        verify(recetaRepo).findByPlatillo(platillo);
    }

    @Test
    void testAgregarIngredienteAPlatillo_Success() {
        when(recetaRepo.save(any(PlatilloIngrediente.class))).thenReturn(platilloIngrediente);

        ResponseEntity<?> response = platilloIngredienteController.agregarIngredienteAPlatillo(platilloIngrediente);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(platilloIngrediente, response.getBody());
        verify(recetaRepo).save(platilloIngrediente);
    }

    @Test
    void testAgregarIngredienteAPlatillo_PlatilloNull() {
        PlatilloIngrediente recetaSinPlatillo = new PlatilloIngrediente();
        recetaSinPlatillo.setIngrediente(ingrediente);
        recetaSinPlatillo.setCantidadRequerida(2);

        ResponseEntity<?> response = platilloIngredienteController.agregarIngredienteAPlatillo(recetaSinPlatillo);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Faltan datos para guardar receta.", response.getBody());
        verify(recetaRepo, never()).save(any());
    }

    @Test
    void testAgregarIngredienteAPlatillo_IngredienteNull() {
        PlatilloIngrediente recetaSinIngrediente = new PlatilloIngrediente();
        recetaSinIngrediente.setPlatillo(platillo);
        recetaSinIngrediente.setCantidadRequerida(2);

        ResponseEntity<?> response = platilloIngredienteController.agregarIngredienteAPlatillo(recetaSinIngrediente);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Faltan datos para guardar receta.", response.getBody());
        verify(recetaRepo, never()).save(any());
    }

    @Test
    void testAgregarIngredienteAPlatillo_BothNull() {
        PlatilloIngrediente recetaVacia = new PlatilloIngrediente();
        recetaVacia.setCantidadRequerida(2);

        ResponseEntity<?> response = platilloIngredienteController.agregarIngredienteAPlatillo(recetaVacia);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Faltan datos para guardar receta.", response.getBody());
        verify(recetaRepo, never()).save(any());
    }

    @Test
    void testEliminarIngredienteDePlatillo_Success() {
        doNothing().when(recetaRepo).deleteById(1L);

        platilloIngredienteController.eliminarIngredienteDePlatillo(1L);

        verify(recetaRepo).deleteById(1L);
    }

    @Test
    void testEliminarIngredienteDePlatillo_MultipleDeletes() {
        doNothing().when(recetaRepo).deleteById(1L);
        doNothing().when(recetaRepo).deleteById(2L);

        platilloIngredienteController.eliminarIngredienteDePlatillo(1L);
        platilloIngredienteController.eliminarIngredienteDePlatillo(2L);

        verify(recetaRepo).deleteById(1L);
        verify(recetaRepo).deleteById(2L);
        verify(recetaRepo, times(2)).deleteById(anyLong());
    }

    @Test
    void testAgregarIngredienteAPlatillo_WithDifferentCantidades() {
        PlatilloIngrediente recetaConCantidadAlta = new PlatilloIngrediente();
        recetaConCantidadAlta.setPlatillo(platillo);
        recetaConCantidadAlta.setIngrediente(ingrediente);
        recetaConCantidadAlta.setCantidadRequerida(10);

        when(recetaRepo.save(any(PlatilloIngrediente.class))).thenReturn(recetaConCantidadAlta);

        ResponseEntity<?> response = platilloIngredienteController.agregarIngredienteAPlatillo(recetaConCantidadAlta);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, ((PlatilloIngrediente) response.getBody()).getCantidadRequerida());
        verify(recetaRepo).save(recetaConCantidadAlta);
    }

    @Test
    void testAgregarIngredienteAPlatillo_WithZeroCantidad() {
        PlatilloIngrediente recetaConCantidadCero = new PlatilloIngrediente();
        recetaConCantidadCero.setPlatillo(platillo);
        recetaConCantidadCero.setIngrediente(ingrediente);
        recetaConCantidadCero.setCantidadRequerida(0);

        when(recetaRepo.save(any(PlatilloIngrediente.class))).thenReturn(recetaConCantidadCero);

        ResponseEntity<?> response = platilloIngredienteController.agregarIngredienteAPlatillo(recetaConCantidadCero);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, ((PlatilloIngrediente) response.getBody()).getCantidadRequerida());
        verify(recetaRepo).save(recetaConCantidadCero);
    }
}