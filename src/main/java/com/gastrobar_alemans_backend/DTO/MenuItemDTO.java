package com.gastrobar_alemans_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MenuItemDTO {
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal promoPrice;
}
