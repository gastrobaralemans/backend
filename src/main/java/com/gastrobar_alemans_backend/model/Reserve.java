package com.gastrobar_alemans_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva_eventos")
public class Reserve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;
    private String numero;

    private LocalDateTime fecha;
    private int cantidad;
    private String decoracion;
    private String comentarios;
    private String tipoEvento;
    private String estado = "pendiente";
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person usuario;

    public Reserve() {
    }

    public Reserve(String nombre, String correo, String numero, LocalDateTime fecha, int cantidad, String decoracion, String comentarios, String tipoEvento) {
        this.nombre = nombre;
        this.correo = correo;
        this.numero = numero;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.decoracion = decoracion;
        this.comentarios = comentarios;
        this.tipoEvento = tipoEvento;
        this.estado = "pendiente";
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getDecoracion() {
        return decoracion;
    }

    public void setDecoracion(String decoracion) {
        this.decoracion = decoracion;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        if (tipoEvento != null) {
            this.tipoEvento = tipoEvento.toLowerCase();
        }
    }
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Person getUsuario() {
        return usuario;
    }
    public void setUsuario(Person usuario) {
        this.usuario = usuario;
    }
}
