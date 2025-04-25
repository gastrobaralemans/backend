package com.gastrobar_alemans_backend.DTO;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ReserveDTO {

    @NotBlank(message = "telefono obligatorio")
    @Pattern(regexp = "\\d{8}", message = "telefono debe de tener solo 8 caracteres")
    private String numero;

    @NotNull(message = "fecha obligatoria")
    @Future(message = "la fecha tiene q ser futura")
    private LocalDateTime fecha;

    @Min(value = 1, message = "debes agrgar al menos una persona")
    @Max(value = 20, message = "maximo 20 personas")
    private int cantidad;

    @Size(max = 100, message = "el campo no puede exceder los 100 caracteres")
    private String decoracion;

    @Size(max = 100, message = "el campo no puede exceder los 100 caracteres")
    private String comentarios;

    @NotBlank(message = "seleccione el tipo de evento")
    private String tipoEvento;

    // --- Getters y Setters ---

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
