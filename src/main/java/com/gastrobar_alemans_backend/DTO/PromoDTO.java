package com.gastrobar_alemans_backend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromoDTO {
    private BigDecimal promoPrice;
    private String promoDescription;
    private LocalDateTime promoStartDate;
    private LocalDateTime promoEndDate;
}
