package com.gastrobar_alemans_backend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public class RegisterModel {
    @NotBlank(message = "Nombre obligatorio")
    private String nombre;

    @NotBlank(message = "Correo obligatorio")
    @Email(message = "Correo inválido")
    private String correo;

    @NotBlank(message = "Contraseña obligatoria")
    @Size(min=8, message ="La contraseña debera tener al menos 8 caracteres")
    private String pass;
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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "RegisterModel{" +
                "nombre='" + nombre + '\'' +
                "correo='" + correo + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
