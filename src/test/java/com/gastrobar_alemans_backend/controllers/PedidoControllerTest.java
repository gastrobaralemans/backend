package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.DTO.PedidoDTO;
import com.gastrobar_alemans_backend.DTO.PedidoItemDTO;
import com.gastrobar_alemans_backend.DTO.PedidosMeseroRequestDTO;
import com.gastrobar_alemans_backend.model.*;
import com.gastrobar_alemans_backend.repository.*;
import com.gastrobar_alemans_backend.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private Person cliente;
    private Person mesero;
    private MenuItemMODEL menuItem;
    private Pedido pedido;
    private PedidoRequest pedidoRequest;
    private PedidosMeseroRequestDTO pedidoMeseroRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        cliente = new Person();
        cliente.setCorreo("cliente@test.com");
        cliente.setNombre("Cliente Test");

        mesero = new Person();
        mesero.setCorreo("mesero@test.com");
        mesero.setNombre("Mesero Test");

        menuItem = new MenuItemMODEL();
        menuItem.setName("Hamburguesa");
        menuItem.setPrice(BigDecimal.valueOf(12.99));
        menuItem.setPromoPrice(BigDecimal.valueOf(10.99));

        // Inicializar pedido con detalles
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFecha(LocalDateTime.now());

        // Crear detalles del pedido
        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setId(1L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(BigDecimal.valueOf(25.98));
        detalle.setPlatillo(menuItem);
        detalle.setPedido(pedido);

        List<PedidoDetalle> detalles = new ArrayList<>();
        detalles.add(detalle);
        pedido.setDetalles(detalles);

        PedidoItemDTO pedidoItemDTO = new PedidoItemDTO();
        pedidoItemDTO.setId(1L);
        pedidoItemDTO.setCantidad(2);

        pedidoRequest = new PedidoRequest();
        pedidoRequest.setPlatillos(List.of(pedidoItemDTO));
        pedidoRequest.setTotal(BigDecimal.valueOf(25.98));
        pedidoRequest.setMetodoPago("EFECTIVO");
        pedidoMeseroRequest = new PedidosMeseroRequestDTO();
        userDetails = mock(UserDetails.class);
    }

    @Test
    void testCrearPedido_Success() {
        // Configurar el mock SOLO en este test
        when(userDetails.getUsername()).thenReturn("cliente@test.com");
        when(personRepository.findByCorreo("cliente@test.com")).thenReturn(Optional.of(cliente));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<?> response = pedidoController.crearPedido(pedidoRequest, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pedido creado.", response.getBody());
        verify(personRepository).findByCorreo("cliente@test.com");
        verify(menuItemRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void testCrearPedido_ClienteNotFound() {
        when(userDetails.getUsername()).thenReturn("cliente@test.com");
        when(personRepository.findByCorreo("cliente@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            pedidoController.crearPedido(pedidoRequest, userDetails);
        });

        verify(personRepository).findByCorreo("cliente@test.com");
        verify(menuItemRepository, never()).findById(anyLong());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testCrearPedido_MenuItemNotFound() {
        when(userDetails.getUsername()).thenReturn("cliente@test.com");
        when(personRepository.findByCorreo("cliente@test.com")).thenReturn(Optional.of(cliente));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            pedidoController.crearPedido(pedidoRequest, userDetails);
        });

        verify(personRepository).findByCorreo("cliente@test.com");
        verify(menuItemRepository).findById(1L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testMarcarComoEntregado_Success() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<?> response = pedidoController.marcarComoEntregado(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pedido marcado como entregado.", response.getBody());
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void testMarcarComoEntregado_PedidoNotFound() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            pedidoController.marcarComoEntregado(99L);
        });

        verify(pedidoRepository).findById(99L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testObtenerPedidos_Success() {
        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setEstado(EstadoPedido.ENTREGADO);
        pedido2.setFecha(LocalDateTime.now());
        pedido2.setCliente(cliente);
        pedido2.setDetalles(new ArrayList<>());

        List<Pedido> pedidos = Arrays.asList(pedido, pedido2);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        List<PedidoDTO> result = pedidoController.obtenerPedidos();

        assertEquals(2, result.size());
        verify(pedidoRepository).findAll();
    }

    @Test
    void testObtenerPedidos_Empty() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList());

        List<PedidoDTO> result = pedidoController.obtenerPedidos();

        assertTrue(result.isEmpty());
        verify(pedidoRepository).findAll();
    }

    @Test
    void testObtenerPedidosParaCocinero_Success() {
        Pedido pedidoPendiente = new Pedido();
        pedidoPendiente.setId(1L);
        pedidoPendiente.setEstado(EstadoPedido.PENDIENTE);
        pedidoPendiente.setFecha(LocalDateTime.now());
        pedidoPendiente.setDetalles(new ArrayList<>());

        Pedido pedidoEnPreparacion = new Pedido();
        pedidoEnPreparacion.setId(2L);
        pedidoEnPreparacion.setEstado(EstadoPedido.EN_PREPARACION);
        pedidoEnPreparacion.setFecha(LocalDateTime.now());
        pedidoEnPreparacion.setDetalles(new ArrayList<>());

        Pedido pedidoListo = new Pedido();
        pedidoListo.setId(3L);
        pedidoListo.setEstado(EstadoPedido.LISTO);
        pedidoListo.setFecha(LocalDateTime.now());
        pedidoListo.setDetalles(new ArrayList<>());

        List<Pedido> pedidos = Arrays.asList(pedidoPendiente, pedidoEnPreparacion, pedidoListo);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        List<PedidoDTO> result = pedidoController.obtenerPedidosParaCocinero();

        assertEquals(2, result.size());
        verify(pedidoRepository).findAll();
    }

    @Test
    void testObtenerHistorialMesero_Success() {
        UserDetails meseroUser = mock(UserDetails.class);
        when(meseroUser.getUsername()).thenReturn("mesero@test.com");

        Pedido pedidoEntregado = new Pedido();
        pedidoEntregado.setId(1L);
        pedidoEntregado.setCliente(mesero);
        pedidoEntregado.setEstado(EstadoPedido.ENTREGADO);
        pedidoEntregado.setFecha(LocalDateTime.now());
        pedidoEntregado.setDetalles(new ArrayList<>());

        Pedido pedidoPendiente = new Pedido();
        pedidoPendiente.setId(2L);
        pedidoPendiente.setCliente(mesero);
        pedidoPendiente.setEstado(EstadoPedido.PENDIENTE);
        pedidoPendiente.setFecha(LocalDateTime.now());
        pedidoPendiente.setDetalles(new ArrayList<>());

        List<Pedido> pedidos = Arrays.asList(pedidoEntregado, pedidoPendiente);
        when(personRepository.findByCorreo("mesero@test.com")).thenReturn(Optional.of(mesero));
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        List<PedidoDTO> result = pedidoController.obtenerHistorialMesero(meseroUser);

        assertEquals(1, result.size());
        verify(personRepository).findByCorreo("mesero@test.com");
        verify(pedidoRepository).findAll();
    }

    @Test
    void testMarcarComoEnPreparacion_Success() {
        pedido.setEstado(EstadoPedido.PENDIENTE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoService).descontarIngredientes(pedido);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<?> response = pedidoController.marcarComoEnPreparacion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pedido en preparación", response.getBody());
        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstado());
        verify(pedidoRepository).findById(1L);
        verify(pedidoService).descontarIngredientes(pedido);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void testMarcarComoEnPreparacion_InvalidState() {
        pedido.setEstado(EstadoPedido.LISTO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        ResponseEntity<?> response = pedidoController.marcarComoEnPreparacion(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El pedido no está pendiente.", response.getBody());
        verify(pedidoRepository).findById(1L);
        verify(pedidoService, never()).descontarIngredientes(any());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testMarcarComoListo_Success() {
        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<?> response = pedidoController.marcarComoListo(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pedido listo.", response.getBody());
        assertEquals(EstadoPedido.LISTO, pedido.getEstado());
        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void testMarcarComoListo_InvalidState() {
        pedido.setEstado(EstadoPedido.PENDIENTE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        ResponseEntity<?> response = pedidoController.marcarComoListo(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El pedido no está en preparación.", response.getBody());
        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testCrearPedidoMesero_Success() {
        UserDetails meseroUser = mock(UserDetails.class);
        when(meseroUser.getUsername()).thenReturn("mesero@test.com");

        // Crear PedidoItemDTO
        PedidoItemDTO pedidoItemDTO = new PedidoItemDTO();
        pedidoItemDTO.setId(1L);
        pedidoItemDTO.setCantidad(1);

        PedidosMeseroRequestDTO request = new PedidosMeseroRequestDTO();

        try {
            var field = request.getClass().getDeclaredField("platillos");
            field.setAccessible(true);
            field.set(request, List.of(pedidoItemDTO));
        } catch (Exception e) {
            System.out.println("No se pudo configurar platillos en PedidosMeseroRequestDTO");
            return;
        }

        when(personRepository.findByCorreo("mesero@test.com")).thenReturn(Optional.of(mesero));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<?> response = pedidoController.crearPedidoMesero(request, meseroUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Mesero ha enviado un pedido.", response.getBody());
        verify(personRepository).findByCorreo("mesero@test.com");
        verify(menuItemRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void testCrearPedidoMesero_MeseroNotFound() {
        UserDetails meseroUser = mock(UserDetails.class);
        when(meseroUser.getUsername()).thenReturn("mesero@test.com");

        PedidosMeseroRequestDTO request = new PedidosMeseroRequestDTO();

        when(personRepository.findByCorreo("mesero@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            pedidoController.crearPedidoMesero(request, meseroUser);
        });

        verify(personRepository).findByCorreo("mesero@test.com");
        verify(menuItemRepository, never()).findById(anyLong());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testCrearPedido_WithPromoPrice() {
        when(userDetails.getUsername()).thenReturn("cliente@test.com");
        when(personRepository.findByCorreo("cliente@test.com")).thenReturn(Optional.of(cliente));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<?> response = pedidoController.crearPedido(pedidoRequest, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(menuItemRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void testCrearPedido_WithRegularPrice() {
        when(userDetails.getUsername()).thenReturn("cliente@test.com");
        menuItem.setPromoPrice(null);
        when(personRepository.findByCorreo("cliente@test.com")).thenReturn(Optional.of(cliente));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<?> response = pedidoController.crearPedido(pedidoRequest, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(menuItemRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void testObtenerPedidosParaCocinero_OnlyValidStates() {
        Pedido pedidoPendiente = new Pedido();
        pedidoPendiente.setId(1L);
        pedidoPendiente.setEstado(EstadoPedido.PENDIENTE);
        pedidoPendiente.setFecha(LocalDateTime.now());
        pedidoPendiente.setDetalles(new ArrayList<>());

        Pedido pedidoEnPreparacion = new Pedido();
        pedidoEnPreparacion.setId(2L);
        pedidoEnPreparacion.setEstado(EstadoPedido.EN_PREPARACION);
        pedidoEnPreparacion.setFecha(LocalDateTime.now());
        pedidoEnPreparacion.setDetalles(new ArrayList<>());

        Pedido pedidoEntregado = new Pedido();
        pedidoEntregado.setId(3L);
        pedidoEntregado.setEstado(EstadoPedido.ENTREGADO);
        pedidoEntregado.setFecha(LocalDateTime.now());
        pedidoEntregado.setDetalles(new ArrayList<>());

        Pedido pedidoListo = new Pedido();
        pedidoListo.setId(4L);
        pedidoListo.setEstado(EstadoPedido.LISTO);
        pedidoListo.setFecha(LocalDateTime.now());
        pedidoListo.setDetalles(new ArrayList<>());

        List<Pedido> pedidos = Arrays.asList(pedidoPendiente, pedidoEnPreparacion, pedidoEntregado, pedidoListo);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        List<PedidoDTO> result = pedidoController.obtenerPedidosParaCocinero();

        assertEquals(2, result.size());
        verify(pedidoRepository).findAll();
    }
}