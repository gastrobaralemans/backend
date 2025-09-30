package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.DTO.ReserveDTO;
import com.gastrobar_alemans_backend.model.Notis;
import com.gastrobar_alemans_backend.model.Reserve;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.NotisRepository;
import com.gastrobar_alemans_backend.repository.ReserveRepository;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveControllerTest {

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private NotisRepository notisRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ReserveController reserveController;

    private Person testPerson;
    private ReserveDTO testReserveDTO;
    private Reserve testReserve;
    private LocalDateTime futureDate;

    @BeforeEach
    void setUp() {
        futureDate = LocalDateTime.now().plusDays(2);

        testPerson = new Person();
        testPerson.setNombre("Juan Perez");
        testPerson.setCorreo("juan@test.com");

        testReserveDTO = new ReserveDTO();
        testReserveDTO.setNumero("12345678");
        testReserveDTO.setFecha(futureDate);
        testReserveDTO.setCantidad(5);
        testReserveDTO.setDecoracion("Balones");
        testReserveDTO.setComentarios("Sin comentarios");
        testReserveDTO.setTipoEvento("Cumpleaños");

        testReserve = new Reserve();
        testReserve.setNombre("Juan Perez");
        testReserve.setCorreo("juan@test.com");
        testReserve.setNumero("12345678");
        testReserve.setFecha(futureDate);
        testReserve.setCantidad(5);
        testReserve.setDecoracion("Balones");
        testReserve.setComentarios("Sin comentarios");
        testReserve.setTipoEvento("cumpleaños");
        testReserve.setEstado("pendiente");
        testReserve.setUsuario(testPerson);
    }

    @Test
    void crearReserva_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("juan@test.com");
        when(personRepository.findByCorreo("juan@test.com")).thenReturn(Optional.of(testPerson));
        when(reserveRepository.existsByNumeroAndEstadoIn("12345678", List.of("pendiente", "aceptado")))
                .thenReturn(false);
        when(reserveRepository.findByCorreoAndEstadoIn("juan@test.com", List.of("pendiente", "aceptado")))
                .thenReturn(Collections.emptyList());
        when(reserveRepository.save(any(Reserve.class))).thenReturn(testReserve);

        ResponseEntity<?> response = reserveController.crearReserva(testReserveDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reserva registrada.", response.getBody());
        verify(reserveRepository, times(1)).save(any(Reserve.class));
    }

    @Test
    void crearReserva_UsuarioNoAutorizado() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("juan@test.com");
        when(personRepository.findByCorreo("juan@test.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = reserveController.crearReserva(testReserveDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Usuario sin autorización.", response.getBody());
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void crearReserva_NumeroEnUso() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("juan@test.com");
        when(personRepository.findByCorreo("juan@test.com")).thenReturn(Optional.of(testPerson));
        when(reserveRepository.existsByNumeroAndEstadoIn("12345678", List.of("pendiente", "aceptado")))
                .thenReturn(true);

        ResponseEntity<?> response = reserveController.crearReserva(testReserveDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Ese número fue registrado por otro usuario.", response.getBody());
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void crearReserva_ReservaActivaExistente() {
        Reserve reservaActiva = new Reserve();
        reservaActiva.setFecha(futureDate.plusDays(1));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("juan@test.com");
        when(personRepository.findByCorreo("juan@test.com")).thenReturn(Optional.of(testPerson));
        when(reserveRepository.existsByNumeroAndEstadoIn("12345678", List.of("pendiente", "aceptado")))
                .thenReturn(false);
        when(reserveRepository.findByCorreoAndEstadoIn("juan@test.com", List.of("pendiente", "aceptado")))
                .thenReturn(List.of(reservaActiva));

        ResponseEntity<?> response = reserveController.crearReserva(testReserveDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Ya tienes una solicitud de reserva activa.", response.getBody());
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void crearReserva_FechaSuficienteAnticipacion() {
        ReserveDTO reservaDTOFechaValida = new ReserveDTO();
        reservaDTOFechaValida.setNumero("12345678");
        reservaDTOFechaValida.setFecha(LocalDateTime.now().plusHours(25));
        reservaDTOFechaValida.setCantidad(5);
        reservaDTOFechaValida.setTipoEvento("Cumpleaños");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("juan@test.com");
        when(personRepository.findByCorreo("juan@test.com")).thenReturn(Optional.of(testPerson));
        when(reserveRepository.existsByNumeroAndEstadoIn("12345678", List.of("pendiente", "aceptado")))
                .thenReturn(false);
        when(reserveRepository.findByCorreoAndEstadoIn("juan@test.com", List.of("pendiente", "aceptado")))
                .thenReturn(Collections.emptyList());
        when(reserveRepository.save(any(Reserve.class))).thenReturn(testReserve);

        ResponseEntity<?> response = reserveController.crearReserva(reservaDTOFechaValida);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reserva registrada.", response.getBody());
        verify(reserveRepository, times(1)).save(any(Reserve.class));
    }

    @Test
    void listarReservas_AdminSuccess() {
        setupAdminAuthentication();
        when(reserveRepository.findAll()).thenReturn(List.of(testReserve));

        ResponseEntity<?> response = reserveController.listarReservas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reserveRepository, times(1)).findAll();
    }

    @Test
    void listarReservas_NoAdmin() {
        setupUserAuthentication();

        ResponseEntity<?> response = reserveController.listarReservas();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No autorizado.", response.getBody());
        verify(reserveRepository, never()).findAll();
    }

    @Test
    void aceptarReserva_AdminSuccess() {
        setupAdminAuthentication();
        when(reserveRepository.findById(1L)).thenReturn(Optional.of(testReserve));
        when(notisRepository.save(any(Notis.class))).thenReturn(new Notis());

        ResponseEntity<?> response = reserveController.aceptarReserva(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reserva aceptada.", response.getBody());
        assertEquals("aceptado", testReserve.getEstado());
        verify(reserveRepository, times(1)).save(testReserve);
        verify(notisRepository, times(2)).save(any(Notis.class));
    }

    @Test
    void aceptarReserva_NoAdmin() {
        setupUserAuthentication();

        ResponseEntity<?> response = reserveController.aceptarReserva(1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No autorizado.", response.getBody());
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void aceptarReserva_NoEncontrada() {
        setupAdminAuthentication();
        when(reserveRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = reserveController.aceptarReserva(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Reserva no encontrada.", response.getBody());
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void rechazarReserva_AdminSuccess() {
        setupAdminAuthentication();
        when(reserveRepository.findById(1L)).thenReturn(Optional.of(testReserve));
        when(notisRepository.save(any(Notis.class))).thenReturn(new Notis());

        ResponseEntity<?> response = reserveController.rechazarReserva(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reserva rechazada.", response.getBody());
        assertEquals("rechazado", testReserve.getEstado());
        verify(reserveRepository, times(1)).save(testReserve);
        verify(notisRepository, times(2)).save(any(Notis.class));
    }

    @Test
    void rechazarReserva_NoAdmin() {
        setupUserAuthentication();

        ResponseEntity<?> response = reserveController.rechazarReserva(1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No autorizado.", response.getBody());
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void rechazarReserva_NoEncontrada() {
        setupAdminAuthentication();
        when(reserveRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = reserveController.rechazarReserva(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Reserva no encontrada.", response.getBody());
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void obtenerNotificaciones_Success() {
        Notis testNoti = new Notis();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("juan@test.com");
        when(notisRepository.buscarPorCorreo("juan@test.com")).thenReturn(List.of(testNoti));

        ResponseEntity<?> response = reserveController.obtenerNotificaciones();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(notisRepository, times(1)).buscarPorCorreo("juan@test.com");
    }

    @Test
    void crearReserva_Exception() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("juan@test.com");
        when(personRepository.findByCorreo("juan@test.com")).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = reserveController.crearReserva(testReserveDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }

    private void setupAdminAuthentication() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        doReturn(authorities).when(authentication).getAuthorities();
    }

    private void setupUserAuthentication() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        doReturn(authorities).when(authentication).getAuthorities();
    }
}
