package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.Ingrediente;
import com.gastrobar_alemans_backend.model.PlatilloIngrediente;
import com.gastrobar_alemans_backend.repository.IngredienteRepository;
import com.gastrobar_alemans_backend.repository.PlatilloIngredienteRepository;
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
class IngredienteControllerTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private PlatilloIngredienteRepository recetaRepo;

    @InjectMocks
    private IngredienteController ingredienteController;

    private Ingrediente ingrediente;
    private Ingrediente ingrediente2;

    @BeforeEach
    void setUp() {
        ingrediente = new Ingrediente();
        ingrediente.setId(1L);
        ingrediente.setNombre("Tomate");
        ingrediente.setCantidadDisponible(50);

        ingrediente2 = new Ingrediente();
        ingrediente2.setId(2L);
        ingrediente2.setNombre("Cebolla");
        ingrediente2.setCantidadDisponible(30);
    }

    @Test
    void testListar_Success() {
        List<Ingrediente> ingredientes = Arrays.asList(ingrediente, ingrediente2);
        when(ingredienteRepository.findAll()).thenReturn(ingredientes);

        List<Ingrediente> result = ingredienteController.listar();

        assertEquals(2, result.size());
        assertEquals("Tomate", result.get(0).getNombre());
        assertEquals("Cebolla", result.get(1).getNombre());
        verify(ingredienteRepository).findAll();
    }

    @Test
    void testListar_EmptyList() {
        when(ingredienteRepository.findAll()).thenReturn(Arrays.asList());

        List<Ingrediente> result = ingredienteController.listar();

        assertTrue(result.isEmpty());
        verify(ingredienteRepository).findAll();
    }

    @Test
    void testCrear_Success() {
        Ingrediente nuevoIngrediente = new Ingrediente();
        nuevoIngrediente.setNombre("Pimiento");
        nuevoIngrediente.setCantidadDisponible(20);

        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        Ingrediente result = ingredienteController.crear(nuevoIngrediente);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Tomate", result.getNombre());
        verify(ingredienteRepository).save(nuevoIngrediente);
    }

    @Test
    void testActualizar_Success() {
        Ingrediente ingredienteActualizado = new Ingrediente();
        ingredienteActualizado.setNombre("Tomate Actualizado");
        ingredienteActualizado.setCantidadDisponible(100);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        Ingrediente result = ingredienteController.actualizar(1L, ingredienteActualizado);

        assertNotNull(result);
        assertEquals("Tomate Actualizado", ingrediente.getNombre());
        assertEquals(100, ingrediente.getCantidadDisponible());
        verify(ingredienteRepository).findById(1L);
        verify(ingredienteRepository).save(ingrediente);
    }

    @Test
    void testActualizar_NotFound() {
        Ingrediente ingredienteActualizado = new Ingrediente();
        ingredienteActualizado.setNombre("Tomate Actualizado");
        ingredienteActualizado.setCantidadDisponible(100);

        when(ingredienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            ingredienteController.actualizar(99L, ingredienteActualizado);
        });

        verify(ingredienteRepository).findById(99L);
        verify(ingredienteRepository, never()).save(any(Ingrediente.class));
    }

    @Test
    void testEliminar_Success() {
        List<PlatilloIngrediente> relaciones = Arrays.asList(new PlatilloIngrediente(), new PlatilloIngrediente());
        when(recetaRepo.findByIngredienteId(1L)).thenReturn(relaciones);
        doNothing().when(recetaRepo).deleteAll(relaciones);
        doNothing().when(ingredienteRepository).deleteById(1L);

        ResponseEntity<?> response = ingredienteController.eliminar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ingrediente eliminado.", response.getBody());
        verify(recetaRepo).findByIngredienteId(1L);
        verify(recetaRepo).deleteAll(relaciones);
        verify(ingredienteRepository).deleteById(1L);
    }

    @Test
    void testEliminar_NoRelations() {
        when(recetaRepo.findByIngredienteId(1L)).thenReturn(Arrays.asList());
        doNothing().when(recetaRepo).deleteAll(anyList());
        doNothing().when(ingredienteRepository).deleteById(1L);

        ResponseEntity<?> response = ingredienteController.eliminar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ingrediente eliminado.", response.getBody());
        verify(recetaRepo).findByIngredienteId(1L);
        verify(recetaRepo).deleteAll(anyList());
        verify(ingredienteRepository).deleteById(1L);
    }

    @Test
    void testEliminar_MultipleRelations() {
        PlatilloIngrediente relacion1 = new PlatilloIngrediente();
        PlatilloIngrediente relacion2 = new PlatilloIngrediente();
        PlatilloIngrediente relacion3 = new PlatilloIngrediente();
        List<PlatilloIngrediente> relaciones = Arrays.asList(relacion1, relacion2, relacion3);

        when(recetaRepo.findByIngredienteId(1L)).thenReturn(relaciones);
        doNothing().when(recetaRepo).deleteAll(relaciones);
        doNothing().when(ingredienteRepository).deleteById(1L);

        ResponseEntity<?> response = ingredienteController.eliminar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ingrediente eliminado.", response.getBody());
        verify(recetaRepo).findByIngredienteId(1L);
        verify(recetaRepo).deleteAll(relaciones);
        verify(ingredienteRepository).deleteById(1L);
    }

    @Test
    void testCrear_WithNullName() {
        Ingrediente ingredienteNull = new Ingrediente();
        ingredienteNull.setNombre(null);
        ingredienteNull.setCantidadDisponible(0);

        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteNull);

        Ingrediente result = ingredienteController.crear(ingredienteNull);

        assertNotNull(result);
        assertNull(result.getNombre());
        assertEquals(0, result.getCantidadDisponible());
        verify(ingredienteRepository).save(ingredienteNull);
    }

    @Test
    void testActualizar_PartialUpdate() {
        Ingrediente ingredienteParcial = new Ingrediente();
        ingredienteParcial.setNombre("Nuevo Nombre");

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        Ingrediente result = ingredienteController.actualizar(1L, ingredienteParcial);

        assertNotNull(result);
        assertEquals("Nuevo Nombre", ingrediente.getNombre());
        assertEquals(50, ingrediente.getCantidadDisponible());
        verify(ingredienteRepository).findById(1L);
        verify(ingredienteRepository).save(ingrediente);
    }
}