package com.gastrobar_alemans_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MenuItemDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;

    private BigDecimal promoPrice;
    private String promoDescription;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
