package com.gastrobar_alemans_backend.service;
import com.gastrobar_alemans_backend.model.PasswordResetToken;
import com.gastrobar_alemans_backend.repository.PasswordResetTokenRepository;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepo;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;

    public void generateAndSend(String email) throws Exception {
        email = email.trim().toLowerCase();

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        tokenRepo.deleteById(email);
        PasswordResetToken token = new PasswordResetToken(email, code, expiry);
        tokenRepo.save(token);
        mailService.sendCode(email, code);
    }


    public boolean verify(String email, String code) {
        return tokenRepo.findById(email)
                .filter(t -> t.getCode().equals(code) && !t.isExpired())
                .isPresent();
    }

    public void reset(String email, String code, String rawPassword) {
        PasswordResetToken t = tokenRepo.findById(email)
                .filter(tok -> tok.getCode().equals(code) && !tok.isExpired())
                .orElseThrow(() -> new IllegalArgumentException("Código inválido o expirado"));
        personRepository.findByCorreo(email).ifPresent(p -> {
            p.setPass(passwordEncoder.encode(rawPassword));
            personRepository.save(p);
        });

        tokenRepo.delete(t);
    }
}
