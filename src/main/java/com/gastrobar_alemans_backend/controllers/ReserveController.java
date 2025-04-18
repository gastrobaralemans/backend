package com.gastrobar_alemans_backend.controllers;
import com.gastrobar_alemans_backend.model.Reserve;
import com.gastrobar_alemans_backend.repository.ReserveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:5173")
public class ReserveController {
    @Autowired
    private ReserveRepository reserveRepository;
    @PostMapping
    public String crearReserva(@RequestBody Reserve reserva) {
        try {
            reserveRepository.save(reserva);
            return "reserva registrada";
        }catch (Exception e){
        return "error" + e.getMessage();}

    }
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
