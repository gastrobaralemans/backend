package com.gastrobar_alemans_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyRequest {
    @NotBlank private String email;
    @NotBlank @Size(min = 6, max = 6, message = "6 dígitos") private String code;
}