package com.gastrobar_alemans_backend.DTO;
import com.gastrobar_alemans_backend.model.Pedido;
import com.gastrobar_alemans_backend.model.PedidoDetalle;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PedidoDTO {
    private Long id;
    private LocalDateTime fecha;
    private String estado;
    private String cliente;
    private List<DetalleDTO> detalles;

    @Data
    public static class DetalleDTO {
        private String nombrePlatillo;
        private int cantidad;
        private double precio;
    }

    public static PedidoDTO fromEntity(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setFecha(pedido.getFecha());
        dto.setEstado(pedido.getEstado().name());

        dto.setCliente(pedido.getCliente() != null ? pedido.getCliente().getNombre() : "Anonimo");


        if (pedido.getDetalles() != null) {
            dto.setDetalles(pedido.getDetalles().stream().map(detalle -> {
                DetalleDTO d = new DetalleDTO();
                d.setNombrePlatillo(detalle.getPlatillo() != null ? detalle.getPlatillo().getName() : "Platillo no disponible");
                d.setCantidad(detalle.getCantidad());
                d.setPrecio(detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario().doubleValue() : 0.0);
                return d;
            }).collect(Collectors.toList()));
        } else {
            dto.setDetalles(List.of());
        }

        return dto;
    }
}