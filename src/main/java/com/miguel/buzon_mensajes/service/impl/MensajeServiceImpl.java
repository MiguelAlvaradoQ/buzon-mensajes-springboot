package com.miguel.buzon_mensajes.service.impl;

import com.miguel.buzon_mensajes.dto.MensajeRequestDTO;
import com.miguel.buzon_mensajes.dto.MensajeResponseDTO;
import com.miguel.buzon_mensajes.model.Mensaje;
import com.miguel.buzon_mensajes.repository.MensajeRepository;
import com.miguel.buzon_mensajes.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miguel.buzon_mensajes.exception.MensajeNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;

    // ===== MÉTODOS CON PAGINACIÓN (NUEVOS) =====

    /**
     * Obtener todos los mensajes con paginación.
     *
     * Ordenados por fecha de creación descendente (más recientes primero).
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MensajeResponseDTO> obtenerTodosPaginado(int page, int size) {
        // Crear Pageable con ordenamiento
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("fechaCreacion").descending()
        );

        // Obtener página de mensajes
        Page<Mensaje> mensajes = mensajeRepository.findAll(pageable);

        // Convertir Page<Mensaje> a Page<MensajeResponseDTO>
        return mensajes.map(this::convertirAResponseDTO);
    }

    /**
     * Obtener mensajes filtrados por estado con paginación.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MensajeResponseDTO> obtenerNoLeidosPaginado(int page, int size, Boolean leido) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("fechaCreacion").descending()
        );

        Page<Mensaje> mensajes = mensajeRepository.findByLeido(leido, pageable);
        return mensajes.map(this::convertirAResponseDTO);
    }

    /**
     * Obtener mensajes por email con paginación.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MensajeResponseDTO> obtenerPorEmailPaginado(String email, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("fechaCreacion").descending()
        );

        Page<Mensaje> mensajes = mensajeRepository.findByEmail(email, pageable);
        return mensajes.map(this::convertirAResponseDTO);
    }

    /**
     * Buscar mensajes por palabra en el contenido con paginación.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MensajeResponseDTO> buscarPorContenido(String palabra, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("fechaCreacion").descending()
        );

        Page<Mensaje> mensajes = mensajeRepository.findByContenidoContaining(palabra, pageable);
        return mensajes.map(this::convertirAResponseDTO);
    }

    // ===== MÉTODOS SIN PAGINACIÓN (MANTENER) =====

    @Override
    public MensajeResponseDTO crear(MensajeRequestDTO request) {
        Mensaje mensaje = new Mensaje();
        mensaje.setNombre(request.getNombre());
        mensaje.setEmail(request.getEmail());
        mensaje.setContenido(request.getContenido());
        mensaje.setLeido(false);

        Mensaje guardado = mensajeRepository.save(mensaje);
        return convertirAResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MensajeResponseDTO> obtenerTodos() {
        return mensajeRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MensajeResponseDTO obtenerPorId(Long id) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new MensajeNotFoundException(id));

        return convertirAResponseDTO(mensaje);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MensajeResponseDTO> obtenerNoLeidos() {
        return mensajeRepository.findByLeido(false)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MensajeResponseDTO> obtenerPorEmail(String email) {
        return mensajeRepository.findByEmail(email)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MensajeResponseDTO marcarComoLeido(Long id) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new MensajeNotFoundException(id));

        mensaje.setLeido(true);
        Mensaje actualizado = mensajeRepository.save(mensaje);

        return convertirAResponseDTO(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        if (!mensajeRepository.existsById(id)) {
            throw new MensajeNotFoundException(id);
        }
        mensajeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarNoLeidos() {
        return mensajeRepository.countByLeido(false);
    }

    // ===== MÉTODOS PRIVADOS =====

    private MensajeResponseDTO convertirAResponseDTO(Mensaje mensaje) {
        MensajeResponseDTO dto = new MensajeResponseDTO();
        dto.setId(mensaje.getId());
        dto.setNombre(mensaje.getNombre());
        dto.setEmail(mensaje.getEmail());
        dto.setContenido(mensaje.getContenido());
        dto.setFechaCreacion(mensaje.getFechaCreacion());
        dto.setLeido(mensaje.getLeido());
        return dto;
    }
}