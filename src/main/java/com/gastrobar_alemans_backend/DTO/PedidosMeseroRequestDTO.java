package com.gastrobar_alemans_backend.DTO;

import lombok.Data;
import java.util.List;

@Data
public class PedidosMeseroRequestDTO {
    private List<PlatilloCantidadDTO> platillos;
}
