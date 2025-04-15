package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final PersonRepository personRepo;

    public AuthController(PersonRepository personaRepo) {
        this.personRepo= personaRepo;
    }

    @PostMapping("/login")
    public String login(@RequestBody Person persona) {
        return personRepo.findByCorreo(persona.getCorreo())
                .filter(p -> p.getContra().equals(persona.getContra()))
                .map(p -> "te logueaste")
                .orElse("error en credenciales");
    }
}
