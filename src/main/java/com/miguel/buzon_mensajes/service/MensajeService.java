package com.miguel.buzon_mensajes.service;

import com.miguel.buzon_mensajes.dto.MensajeRequestDTO;
import com.miguel.buzon_mensajes.dto.MensajeResponseDTO;

import java.util.List;

public interface MensajeService {

    // Crear un nuevo mensaje
    MensajeResponseDTO crear(MensajeRequestDTO request);

    // Obtener todos los mensajes
    List<MensajeResponseDTO> obtenerTodos();

    // Obtener un mensaje por ID
    MensajeResponseDTO obtenerPorId(Long id);

    // Obtener mensajes no leídos
    List<MensajeResponseDTO> obtenerNoLeidos();

    // Obtener mensajes por email
    List<MensajeResponseDTO> obtenerPorEmail(String email);

    // Marcar mensaje como leído
    MensajeResponseDTO marcarComoLeido(Long id);

    // Eliminar un mensaje
    void eliminar(Long id);

    // Contar mensajes no leídos
    Long contarNoLeidos();
}