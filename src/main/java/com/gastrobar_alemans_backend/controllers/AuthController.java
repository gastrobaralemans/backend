package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.LoginModel;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import com.gastrobar_alemans_backend.security.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final PersonRepository personRepo;
    private final JWTUtil jwt;
    private final PasswordEncoder passwordEncoder;

    public AuthController(PersonRepository personRepo, JWTUtil jwt, PasswordEncoder passwordEncoder) {
        this.personRepo = personRepo;
        this.jwt = jwt;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/hash")
    public String generarHash(@RequestParam String pass) {
        return passwordEncoder.encode(pass);
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        String correo = jwt.validateTokenAndRetrieveSubject(refreshToken);
        if (correo == null) {
            return ResponseEntity.status(401).body("refresh token inválido o expirado");
        }

        String newAccessToken = jwt.generateToken(correo);
        return ResponseEntity.ok().body(
                String.format("{\"token\":\"%s\"}", newAccessToken)
        );
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginModel request) {
        Optional<Person> personaDb = personRepo.findByCorreo(request.getCorreo());


        if (personaDb.isEmpty()) {
            return ResponseEntity.status(401).body("Correo no registrado");
        }

        Person user = personaDb.get();
        if (!passwordEncoder.matches(request.getPass(), user.getPass())) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        String token = jwt.generateToken(user.getCorreo());
        String refresh = jwt.generateRefreshToken(user.getCorreo());

        return ResponseEntity.ok().body(
                String.format("{\"token\":\"%s\", \"refreshToken\":\"%s\", \"nombre\": \"%s\", \"rol\": \"%s\"}",
                        token, refresh, user.getNombre(), user.getRol())
        );

    }
}
