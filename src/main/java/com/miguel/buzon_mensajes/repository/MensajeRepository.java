package com.miguel.buzon_mensajes.repository;

import com.miguel.buzon_mensajes.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // Método personalizado: buscar mensajes no leídos
    List<Mensaje> findByLeido(Boolean leido);

    // Método personalizado: buscar por email
    List<Mensaje> findByEmail(String email);

    // Método personalizado: contar mensajes no leídos
    Long countByLeido(Boolean leido);

    // Buscar por nombre (case insensitive)
    List<Mensaje> findByNombreIgnoreCase(String nombre);

    // Buscar por email Y leído
    List<Mensaje> findByEmailAndLeido(String email, Boolean leido);

    // Buscar mensajes con contenido que contiene una palabra
    List<Mensaje> findByContenidoContaining(String palabra);

    // Buscar mensajes creados después de una fecha
    List<Mensaje> findByFechaCreacionAfter(LocalDateTime fecha);

    // Buscar los primeros 10 no leídos
    List<Mensaje> findTop10ByLeidoOrderByFechaCreacionDesc(Boolean leido);
}