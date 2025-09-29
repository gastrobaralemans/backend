package com.gastrobar_alemans_backend.DTO;

import lombok.Data;
import java.util.List;

@Data
public class PedidosMeseroRequestDTO {
    private List<PedidoItemDTO> platillos;
    public PedidosMeseroRequestDTO() {}

    public PedidosMeseroRequestDTO(List<PedidoItemDTO> platillos) {
        this.platillos = platillos;
    }
}
