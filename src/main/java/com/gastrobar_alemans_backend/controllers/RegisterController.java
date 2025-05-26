package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.RegisterModel;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173"})
public class RegisterController {

    private final PersonRepository personRepo;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(PersonRepository personRepo, PasswordEncoder passwordEncoder) {
        this.personRepo = personRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterModel request) {
        Optional<Person> existingPerson = personRepo.findByCorreo(request.getCorreo());
        if (existingPerson.isPresent()) {
            return ResponseEntity.badRequest().body("Correo ya registrado");
        }

        Person newPerson = new Person();
        newPerson.setNombre(request.getNombre());
        newPerson.setCorreo(request.getCorreo());
        newPerson.setPass(passwordEncoder.encode(request.getPass()));
        newPerson.setRol("usuario");

        personRepo.save(newPerson);

        return ResponseEntity.ok("Registro exitoso");
    }
}
