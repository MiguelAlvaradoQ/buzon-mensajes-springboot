package com.miguel.buzon_mensajes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para las vistas web (Thymeleaf).
 */
@Controller
public class WebController {

    /**
     * Página principal: formulario de contacto PÚBLICO.
     *
     * Esta página es accesible para todos sin autenticación.
     */
    @GetMapping({"/", "/index", "/contacto"})
    public String index() {
        return "index"; // templates/index.html
    }

    /**
     * Panel de administración PRIVADO.
     *
     * Solo accesible con autenticación.
     * Muestra todos los mensajes con paginación.
     */
    @GetMapping("/admin/mensajes")
    public String adminMensajes() {
        return "admin"; // templates/admin.html (LO CREAREMOS)
    }

    /**
     * Página de login personalizada (opcional).
     *
     * Si no la creas, Spring Security usa una por defecto.
     */
    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html (LO CREAREMOS)
    }
}