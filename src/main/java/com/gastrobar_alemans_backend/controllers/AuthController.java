package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.LoginModel;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final PersonRepository personRepo;

    public AuthController(PersonRepository personRepo) {
        this.personRepo = personRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginModel request) {
        Optional<Person> personaDb = personRepo.findByCorreo(request.getCorreo());

        if (personaDb.isEmpty()) {
            return ResponseEntity.status(401).body("correo no registrado");
        }

        if (!request.getCorreo().equals("admin@admin.com") || !request.getContraseña().equals("admin123")) {
            return ResponseEntity.status(401).body("malas credenciales");
        }


        return ResponseEntity.ok("Login exitoso");
    }
}
