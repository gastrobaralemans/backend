package com.gastrobar_alemans_backend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginModel {

    @NotBlank(message = "Correo obligatorio")
    @Email(message = "Correo inválido")
    private String correo;

    @NotBlank(message = "Contraseña obligatoria")
    private String pass;
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
        return "LoginModel{" +
                "correo='" + correo + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
