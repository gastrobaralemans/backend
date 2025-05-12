package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.DTO.PedidoDTO;
import com.gastrobar_alemans_backend.model.*;
import com.gastrobar_alemans_backend.repository.MenuItemRepository;
import com.gastrobar_alemans_backend.repository.PedidoRepository;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final MenuItemRepository menuItemRepository;
    private final PedidoRepository pedidoRepository;
    private final PersonRepository personRepository;

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest pedidoReq,
                                         @AuthenticationPrincipal UserDetails user) {

        Person cliente = personRepository.findByCorreo(user.getUsername()).orElseThrow();

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        List<PedidoDetalle> detalles = pedidoReq.getPlatillos().stream().map(p -> {
            MenuItemMODEL item = menuItemRepository.findById(p.getId()).orElseThrow();
            PedidoDetalle d = new PedidoDetalle();
            BigDecimal precioFinal = item.getPromoPrice() != null ? item.getPromoPrice() : item.getPrice();
            d.setPrecioUnitario(precioFinal);
            d.setPlatillo(item);
            d.setCantidad(p.getCantidad());
            d.setPedido(pedido);
            return d;
        }).toList();

        pedido.setDetalles(detalles);
        pedidoRepository.save(pedido);

        return ResponseEntity.ok("Pedido creado");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<PedidoDTO> obtenerPedidos() {
        return pedidoRepository.findAll().stream()
                .map(PedidoDTO::fromEntity)
                .toList();
    }
}
