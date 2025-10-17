package com.miguel.buzon_mensajes.service.impl;

import com.miguel.buzon_mensajes.dto.MensajeRequestDTO;
import com.miguel.buzon_mensajes.dto.MensajeResponseDTO;
import com.miguel.buzon_mensajes.model.Mensaje;
import com.miguel.buzon_mensajes.repository.MensajeRepository;
import com.miguel.buzon_mensajes.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;

    @Override
    public MensajeResponseDTO crear(MensajeRequestDTO request) {
        // 1. Convertir RequestDTO a Entidad
        Mensaje mensaje = new Mensaje();
        mensaje.setNombre(request.getNombre());
        mensaje.setEmail(request.getEmail());
        mensaje.setContenido(request.getContenido());
        mensaje.setLeido(false);

        // 2. Guardar en BD
        Mensaje guardado = mensajeRepository.save(mensaje);

        // 3. Convertir Entidad a ResponseDTO y devolver
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
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado con ID: " + id));

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
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado con ID: " + id));

        mensaje.setLeido(true);
        Mensaje actualizado = mensajeRepository.save(mensaje);

        return convertirAResponseDTO(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        if (!mensajeRepository.existsById(id)) {
            throw new RuntimeException("Mensaje no encontrado con ID: " + id);
        }
        mensajeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarNoLeidos() {
        return mensajeRepository.countByLeido(false);
    }

    // Método privado para convertir Entidad a ResponseDTO
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