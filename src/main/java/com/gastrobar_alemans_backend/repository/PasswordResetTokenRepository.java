package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
}