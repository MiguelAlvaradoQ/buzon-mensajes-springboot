package com.miguel.buzon_mensajes.controller;

import com.miguel.buzon_mensajes.dto.MensajeRequestDTO;
import com.miguel.buzon_mensajes.dto.MensajeResponseDTO;
import com.miguel.buzon_mensajes.service.MensajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MensajeController {

    private final MensajeService mensajeService;

    // POST /api/mensajes - Crear un nuevo mensaje
    @PostMapping
    public ResponseEntity<MensajeResponseDTO> crear(@Valid @RequestBody MensajeRequestDTO request) {
        MensajeResponseDTO response = mensajeService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/mensajes - Obtener todos los mensajes
    @GetMapping
    public ResponseEntity<List<MensajeResponseDTO>> obtenerTodos() {
        List<MensajeResponseDTO> mensajes = mensajeService.obtenerTodos();
        return ResponseEntity.ok(mensajes);
    }

    // GET /api/mensajes/{id} - Obtener un mensaje por ID
    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponseDTO> obtenerPorId(@PathVariable Long id) {
        MensajeResponseDTO mensaje = mensajeService.obtenerPorId(id);
        return ResponseEntity.ok(mensaje);
    }

    // GET /api/mensajes/no-leidos - Obtener mensajes no leídos
    @GetMapping("/no-leidos")
    public ResponseEntity<List<MensajeResponseDTO>> obtenerNoLeidos() {
        List<MensajeResponseDTO> mensajes = mensajeService.obtenerNoLeidos();
        return ResponseEntity.ok(mensajes);
    }

    // GET /api/mensajes/email/{email} - Obtener mensajes por email
    @GetMapping("/email/{email}")
    public ResponseEntity<List<MensajeResponseDTO>> obtenerPorEmail(@PathVariable String email) {
        List<MensajeResponseDTO> mensajes = mensajeService.obtenerPorEmail(email);
        return ResponseEntity.ok(mensajes);
    }

    // PATCH /api/mensajes/{id}/leido - Marcar mensaje como leído
    @PatchMapping("/{id}/leido")
    public ResponseEntity<MensajeResponseDTO> marcarComoLeido(@PathVariable Long id) {
        MensajeResponseDTO mensaje = mensajeService.marcarComoLeido(id);
        return ResponseEntity.ok(mensaje);
    }

    // DELETE /api/mensajes/{id} - Eliminar un mensaje
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mensajeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/mensajes/contar-no-leidos - Contar mensajes no leídos
    @GetMapping("/contar-no-leidos")
    public ResponseEntity<Long> contarNoLeidos() {
        Long cantidad = mensajeService.contarNoLeidos();
        return ResponseEntity.ok(cantidad);
    }
}
