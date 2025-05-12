package com.gastrobar_alemans_backend.model;

import com.gastrobar_alemans_backend.DTO.PedidoItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PedidoRequest {
    private List<PedidoItemDTO> platillos;
    private BigDecimal total;
    private String metodoPago;
}