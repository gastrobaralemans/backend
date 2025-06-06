package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.Ingrediente;
import com.gastrobar_alemans_backend.model.PlatilloIngrediente;
import com.gastrobar_alemans_backend.repository.IngredienteRepository;
import com.gastrobar_alemans_backend.repository.PlatilloIngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredientes")
@RequiredArgsConstructor
public class IngredienteController {

    private final IngredienteRepository ingredienteRepository;
    private final PlatilloIngredienteRepository recetaRepo;

    @GetMapping
    public List<Ingrediente> listar() {
        return ingredienteRepository.findAll();
    }

    @PostMapping
    public Ingrediente crear(@RequestBody Ingrediente ingrediente) {
        return ingredienteRepository.save(ingrediente);
    }

    @PutMapping("/{id}")
    public Ingrediente actualizar(@PathVariable Long id, @RequestBody Ingrediente ingrediente) {
        Ingrediente existente = ingredienteRepository.findById(id).orElseThrow();
        existente.setNombre(ingrediente.getNombre());
        existente.setCantidadDisponible(ingrediente.getCantidadDisponible());
        return ingredienteRepository.save(existente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        List<PlatilloIngrediente> relaciones = recetaRepo.findByIngredienteId(id);
        recetaRepo.deleteAll(relaciones);
        ingredienteRepository.deleteById(id);

        return ResponseEntity.ok("Ingrediente eliminado.");
    }

}
