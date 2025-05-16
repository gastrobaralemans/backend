package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.MenuItemMODEL;
import com.gastrobar_alemans_backend.model.PlatilloIngrediente;
import com.gastrobar_alemans_backend.repository.MenuItemRepository;
import com.gastrobar_alemans_backend.repository.PlatilloIngredienteRepository;
import com.gastrobar_alemans_backend.repository.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
public class PlatilloIngredienteController {

    private final PlatilloIngredienteRepository recetaRepo;
    private final MenuItemRepository platilloRepo;
    private final IngredienteRepository ingredienteRepo;

    @GetMapping("/{platilloId}")
    public List<PlatilloIngrediente> obtenerPorPlatillo(@PathVariable Long platilloId) {
        MenuItemMODEL platillo = platilloRepo.findById(platilloId).orElseThrow();
        return recetaRepo.findByPlatillo(platillo);
    }

    @PostMapping
    public ResponseEntity<?> agregarIngredienteAPlatillo(@RequestBody PlatilloIngrediente receta) {
        System.out.println("Recibido: " + receta);
        if (receta.getPlatillo() == null || receta.getIngrediente() == null) {
            return ResponseEntity.badRequest().body("Faltan datos para guardar receta");
        }
        return ResponseEntity.ok(recetaRepo.save(receta));
    }



    @DeleteMapping("/{id}")
    public void eliminarIngredienteDePlatillo(@PathVariable Long id) {
        recetaRepo.deleteById(id);
    }
}
