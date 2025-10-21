package com.miguel.buzon_mensajes.service;

import com.miguel.buzon_mensajes.dto.MensajeRequestDTO;
import com.miguel.buzon_mensajes.dto.MensajeResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MensajeService {

    // ===== MÉTODOS CON PAGINACIÓN (NUEVOS) =====

    /**
     * Obtener mensajes paginados.
     * @param page Número de página (0-indexed)
     * @param size Cantidad de elementos por página
     * @return Página de mensajes
     */
    Page<MensajeResponseDTO> obtenerTodosPaginado(int page, int size);

    /**
     * Obtener mensajes no leídos paginados.
     */
    Page<MensajeResponseDTO> obtenerNoLeidosPaginado(int page, int size, Boolean leido);

    /**
     * Obtener mensajes por email paginados.
     */
    Page<MensajeResponseDTO> obtenerPorEmailPaginado(String email, int page, int size);

    /**
     * Buscar mensajes por palabra en contenido (paginado).
     */
    Page<MensajeResponseDTO> buscarPorContenido(String palabra, int page, int size);

    // ===== MÉTODOS SIN PAGINACIÓN (MANTENER) =====

    MensajeResponseDTO crear(MensajeRequestDTO request);
    List<MensajeResponseDTO> obtenerTodos();
    MensajeResponseDTO obtenerPorId(Long id);
    List<MensajeResponseDTO> obtenerNoLeidos();
    List<MensajeResponseDTO> obtenerPorEmail(String email);
    MensajeResponseDTO marcarComoLeido(Long id);
    void eliminar(Long id);
    Long contarNoLeidos();
}