package com.gastrobar_alemans_backend.DTO;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ReserveDTO {

    @NotBlank(message = "Telefono obligatorio.")
    @Pattern(regexp = "\\d{8}", message = "Telefono debe tener 8 caracteres.")
    private String numero;

    @NotNull(message = "Fecha obligatoria.")
    @Future(message = "La fecha debe ser futura.")
    private LocalDateTime fecha;

    @Min(value = 1, message = "Debes añadir al menos un invitado.")
    @Max(value = 20, message = "Maximo 20 invitados.")
    private int cantidad;

    @Size(max = 100, message = "100 carácteres maximo.")
    private String decoracion;

    @Size(max = 100, message = "100 carácteres maximo.")
    private String comentarios;

    @NotBlank(message = "Selecciona un evento.")
    private String tipoEvento;

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getDecoracion() { return decoracion; }
    public void setDecoracion(String decoracion) { this.decoracion = decoracion; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
}
