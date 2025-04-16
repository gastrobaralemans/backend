package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/register")
@CrossOrigin(origins = {"http://localhost:3000"})
public class RegisterController {
    private final PersonRepository personRepo;

    public RegisterController(PersonRepository personaRepo) {
        this.personRepo= personaRepo;
    }

    @PostMapping("/register")
    public String register(@RequestBody Person persona) {
        return personRepo.findByCorreo(persona.getCorreo())
                .filter(p -> p.getCorreo().equals(persona.getCorreo()) &&
                        p.getContraseña().equals(persona.getContraseña()))
                .map(p -> "te registraste")
                .orElse("error en credenciales");
    }
}
