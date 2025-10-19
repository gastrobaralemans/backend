package com.gastrobar_alemans_backend.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PasswordResetToken {
    @Id
    private String email;

    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private LocalDateTime expiry;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiry);
    }
}
