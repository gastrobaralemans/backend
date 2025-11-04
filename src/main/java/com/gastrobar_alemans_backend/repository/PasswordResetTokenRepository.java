package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.PasswordResetToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiry < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
