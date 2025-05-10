package com.gastrobar_alemans_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String mensaje;
    private boolean leida = false;
    private LocalDateTime fecha = LocalDateTime.now();
    private Integer cantidad;
    private String comentarios;
    private String correo;
    private String decoracion;
    private String estado;
    private String nombre;
    private String numero;
    @Column(name = "tipo_evento")
    private String tipoEvento;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person receptor;

    public Notis() {}
    public Notis(String titulo, String mensaje, Person receptor,
                 Integer cantidad, String comentarios, String correo,
                 String decoracion, String estado, String nombre,
                 String numero, String tipoEvento) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.receptor = receptor;
        this.cantidad = cantidad;
        this.comentarios = comentarios;
        this.correo = correo;
        this.decoracion = decoracion;
        this.estado = estado;
        this.nombre = nombre;
        this.numero = numero;
        this.tipoEvento = tipoEvento;
        this.leida = false;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDecoracion() {
        return decoracion;
    }

    public void setDecoracion(String decoracion) {
        this.decoracion = decoracion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Person getReceptor() {
        return receptor;
    }

    public void setReceptor(Person receptor) {
        this.receptor = receptor;
    }
}
