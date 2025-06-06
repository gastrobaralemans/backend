package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.DTO.ReserveDTO;
import com.gastrobar_alemans_backend.model.Notis;
import com.gastrobar_alemans_backend.model.Reserve;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.NotisRepository;
import com.gastrobar_alemans_backend.repository.ReserveRepository;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:5173")
public class ReserveController {

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private NotisRepository notisRepository;

    @PostMapping
    public ResponseEntity<?> crearReserva(@Valid @RequestBody ReserveDTO reservaDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String correo = auth.getName();
            Optional<Person> usuarioOpt = personRepository.findByCorreo(correo);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(403).body("Usuario sin autorización.");
            }

            Person usuario = usuarioOpt.get();

            boolean numeroEnUso = reserveRepository.existsByNumeroAndEstadoIn(
                    reservaDTO.getNumero(),
                    List.of("pendiente", "aceptado")
            );

            if (numeroEnUso) {
                return ResponseEntity.badRequest().body("Ese número fue registrado por otro usuario.");
            }

            List<Reserve> reservasExistentes = reserveRepository.findByCorreoAndEstadoIn(
                    correo, List.of("pendiente", "aceptado")
            );

            LocalDateTime ahora = LocalDateTime.now();
            for (Reserve r : reservasExistentes) {
                if (r.getFecha().isAfter(ahora)) {
                    return ResponseEntity.badRequest().body("Ya tienes una solicitud de reserva activa.");
                }
            }

            if (reservaDTO.getFecha().toLocalDate().isBefore(ahora.plusDays(1).toLocalDate())) {
                return ResponseEntity.badRequest().body("Debes reservar al menos con un día de anticipación.");
            }

            Reserve nuevaReserva = new Reserve();
            nuevaReserva.setNombre(usuario.getNombre());
            nuevaReserva.setCorreo(usuario.getCorreo());
            nuevaReserva.setNumero(reservaDTO.getNumero());
            nuevaReserva.setFecha(reservaDTO.getFecha());
            nuevaReserva.setCantidad(reservaDTO.getCantidad());
            nuevaReserva.setDecoracion(reservaDTO.getDecoracion());
            nuevaReserva.setComentarios(reservaDTO.getComentarios());
            nuevaReserva.setTipoEvento(reservaDTO.getTipoEvento());
            nuevaReserva.setEstado("pendiente");
            nuevaReserva.setUsuario(usuario);

            reserveRepository.save(nuevaReserva);

            return ResponseEntity.ok("Reserva registrada.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listarReservas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin) {
            return ResponseEntity.status(403).body("No autorizado.");
        }

        List<Reserve> reservas = reserveRepository.findAll();
        return ResponseEntity.ok(reservas);
    }
    @PutMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptarReserva(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin) {
            return ResponseEntity.status(403).body("No autorizado.");
        }
        Optional<Reserve> reservaOpt = reserveRepository.findById(id);
        if (reservaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reserva no encontrada.");
        }

        Reserve reserva = reservaOpt.get();

        reserva.setEstado("aceptado");
        reserveRepository.save(reserva);

        if (reserva.getUsuario() != null) {
            Notis noti = new Notis(
                    "Reserva aceptada",
                    "Tu reserva para " + reserva.getTipoEvento() + " fue aceptada.",
                    reserva.getUsuario(),
                    reserva.getCantidad(),
                    reserva.getComentarios(),
                    reserva.getCorreo(),
                    reserva.getDecoracion(),
                    reserva.getEstado(),
                    reserva.getNombre(),
                    reserva.getNumero(),
                    reserva.getTipoEvento()
            );
            notisRepository.save(noti);
            notisRepository.save(noti);
        }

        return ResponseEntity.ok("Reserva aceptada.");
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarReserva(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin) {
            return ResponseEntity.status(403).body("No autorizado.");
        }

        Optional<Reserve> reservaOpt = reserveRepository.findById(id);
        if (reservaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reserva no encontrada.");
        }

        Reserve reserva = reservaOpt.get();
        if (reserva.getUsuario() != null) {
            System.out.println(reserva.getUsuario().getCorreo());
        }

        reserva.setEstado("rechazado");
        reserveRepository.save(reserva);
        if (reserva.getUsuario() != null) {
            Notis noti = new Notis(
                    "Reserva rechazada",
                    "Tu reserva para " + reserva.getTipoEvento() + " fue rechazada.",
                    reserva.getUsuario(),
                    reserva.getCantidad(),
                    reserva.getComentarios(),
                    reserva.getCorreo(),
                    reserva.getDecoracion(),
                    reserva.getEstado(),
                    reserva.getNombre(),
                    reserva.getNumero(),
                    reserva.getTipoEvento()
            );
            notisRepository.save(noti);
            notisRepository.save(noti);
        }

        return ResponseEntity.ok("Reserva rechazada.");
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<?> obtenerNotificaciones() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();
        List<Notis> notis = notisRepository.buscarPorCorreo(correo);
        return ResponseEntity.ok(notis);
    }
}
