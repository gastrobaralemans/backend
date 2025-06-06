package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.DTO.PedidoDTO;
import com.gastrobar_alemans_backend.DTO.PedidosMeseroRequestDTO;
import com.gastrobar_alemans_backend.model.*;
import com.gastrobar_alemans_backend.repository.*;
import com.gastrobar_alemans_backend.service.PedidoService;
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
    private final PedidoService pedidoService;

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

        return ResponseEntity.ok("Pedido creado.");
    }

    @PutMapping("/{id}/entregado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> marcarComoEntregado(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();
        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);
        return ResponseEntity.ok("Pedido marcado como entregado.");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COCINERO')or hasRole('MESERO')")
    @GetMapping
    public List<PedidoDTO> obtenerPedidos() {
        return pedidoRepository.findAll().stream()
                .map(PedidoDTO::fromEntity)
                .toList();
    }

    @PreAuthorize("hasRole('COCINERO')")
    @GetMapping("/cocinero")
    public List<PedidoDTO> obtenerPedidosParaCocinero() {
        return pedidoRepository.findAll().stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE || p.getEstado() == EstadoPedido.EN_PREPARACION)
                .map(PedidoDTO::fromEntity)
                .toList();
    }
    @PreAuthorize("hasRole('MESERO')")
    @GetMapping("/mesero/historial")
    public List<PedidoDTO> obtenerHistorialMesero(@AuthenticationPrincipal UserDetails user) {
        Person mesero = personRepository.findByCorreo(user.getUsername()).orElseThrow();

        return pedidoRepository.findAll().stream()
                .filter(p -> p.getCliente() != null
                        && p.getCliente().equals(mesero)
                        && p.getEstado() == EstadoPedido.ENTREGADO)
                .map(PedidoDTO::fromEntity)
                .toList();
    }

    @PutMapping("/{id}/preparando")
    @PreAuthorize("hasRole('COCINERO')")
    public ResponseEntity<?> marcarComoEnPreparacion(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();

        if (!pedido.getEstado().equals(EstadoPedido.PENDIENTE)) {
            return ResponseEntity.badRequest().body("El pedido no está pendiente.");
        }

        pedidoService.descontarIngredientes(pedido);
        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        pedidoRepository.save(pedido);
        return ResponseEntity.ok("Pedido en preparación");
    }

    @PutMapping("/{id}/listo")
    @PreAuthorize("hasRole('COCINERO')")
    public ResponseEntity<?> marcarComoListo(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();

        if (!pedido.getEstado().equals(EstadoPedido.EN_PREPARACION)) {
            return ResponseEntity.badRequest().body("El pedido no está en preparación.");
        }

        pedido.setEstado(EstadoPedido.LISTO);
        pedidoRepository.save(pedido);
        return ResponseEntity.ok("Pedido listo.");
    }
    @PostMapping("/mesero")
    @PreAuthorize("hasRole('MESERO')")
    public ResponseEntity<?> crearPedidoMesero(@RequestBody PedidosMeseroRequestDTO pedidoReq,
                                               @AuthenticationPrincipal UserDetails user) {
        Person cliente = personRepository.findByCorreo(user.getUsername()).orElseThrow();

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        List<PedidoDetalle> detalles = pedidoReq.getPlatillos().stream().map(p -> {
            MenuItemMODEL item = menuItemRepository.findById(p.getId()).orElseThrow();
            PedidoDetalle d = new PedidoDetalle();
            d.setPrecioUnitario(item.getPromoPrice() != null ? item.getPromoPrice() : item.getPrice());
            d.setPlatillo(item);
            d.setCantidad(p.getCantidad());
            d.setPedido(pedido);
            return d;
        }).toList();

        pedido.setDetalles(detalles);
        pedidoRepository.save(pedido);

        return ResponseEntity.ok("Mesero ha enviado un pedido.");
    }



}
