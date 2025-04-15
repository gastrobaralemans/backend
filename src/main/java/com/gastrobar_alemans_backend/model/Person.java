package com.gastrobar_alemans_backend.model;
import jakarta.persistence.*;
@Entity
@Table(name = "personas")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true)
    private String correo;
    @Column(unique = true)
    private String contra;

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
    public String getContra(){
        return contra;
    }
    public void setContra(String contra) {
        this.contra = contra;
    }
}
