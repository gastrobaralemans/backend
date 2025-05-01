package com.gastrobar_alemans_backend.controllers;
import com.gastrobar_alemans_backend.DTO.ReserveDTO;
import com.gastrobar_alemans_backend.model.Reserve;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.ReserveRepository;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    public ResponseEntity<?> crearReserva(@Valid @RequestBody ReserveDTO reservaDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String correo = auth.getName();
            Optional<Person> usuarioOpt = personRepository.findByCorreo(correo);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(403).body("Usuario sin autorización");
            }

            Person usuario = usuarioOpt.get();
            if (reserveRepository.existsByNumero(reservaDTO.getNumero())) {
                return ResponseEntity.badRequest().body("telefono ya registradoo");
            }

            List<Reserve> reservasExistentes = reserveRepository.findByCorreoAndEstadoIn(
                    correo, List.of("pendiente", "aceptado")
            );

            LocalDateTime ahora = LocalDateTime.now();
            for (Reserve r : reservasExistentes) {
                if (r.getFecha().isAfter(ahora)) {
                    return ResponseEntity.badRequest().body("ya tienes una solicitudes de reserva, no puedes registrar otra");
                }
            }

            if (reservaDTO.getFecha().toLocalDate().isBefore(ahora.plusDays(1).toLocalDate())) {
                return ResponseEntity.badRequest().body("reservar al menos con un dia de anticipacion");
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

            reserveRepository.save(nuevaReserva);

            return ResponseEntity.ok("Reserva registrada correctamente");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Reserve> listarReservas() {
        return reserveRepository.findAll();
    }
    @PutMapping("/{id}/aceptar")
    public String aceptarReserva(@PathVariable Long id) {
        Optional<Reserve> reservaOpt = reserveRepository.findById(id);
        if (reservaOpt.isPresent()) {
            Reserve reserva = reservaOpt.get();
            reserva.setEstado("aceptado");
            reserveRepository.save(reserva);
            return "Reserva aceptada";
        } else {
            return "Reserva no encontrada";
        }
    }
    @PutMapping("/{id}/rechazar")
    public String rechazarReserva(@PathVariable Long id) {
        Optional<Reserve> reservaOpt = reserveRepository.findById(id);
        if (reservaOpt.isPresent()) {
            Reserve reserva = reservaOpt.get();
            reserva.setEstado("rechazado");
            reserveRepository.save(reserva);
            return "Reserva rechazada";
        } else {
            return "Reserva no encontrada";
        }
    }

}
