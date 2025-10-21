package com.miguel.buzon_mensajes;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("Contraseña sin encriptar: " + rawPassword);
        System.out.println("Contraseña encriptada: " + encodedPassword);
    }
}