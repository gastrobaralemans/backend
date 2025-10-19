package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.DTO.ForgotRequest;
import com.gastrobar_alemans_backend.DTO.VerifyRequest;
import com.gastrobar_alemans_backend.DTO.ResetRequest;
import com.gastrobar_alemans_backend.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService service;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@Valid @RequestBody ForgotRequest dto) {
        try {
            service.generateAndSend(dto.getEmail());
            return ResponseEntity.ok(Map.of("message", "Código enviado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo enviar el correo"));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyRequest dto) {
        boolean ok = service.verify(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(Map.of("valid", ok));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@Valid @RequestBody ResetRequest dto) {
        try {
            service.reset(dto.getEmail(), dto.getCode(), dto.getNewPassword());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}