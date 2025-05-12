package com.gastrobar_alemans_backend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "personas")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String rol;

    private String nombre;

    @Column(unique = true)
    private String correo;

    private String pass;
    public Long getId() {return id;}
    public String getRol() {return rol;}
    public void setRol(String Rol) {this.rol = rol;}
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
    public String getPass(){
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }



}
