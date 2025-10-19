package com.gastrobar_alemans_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetRequest {
    @NotBlank private String email;
    @NotBlank @Size(min = 6, max = 6) private String code;
    @NotBlank @Size(min = 6, message = "Mínimo 6 caracteres") private String newPassword;
}