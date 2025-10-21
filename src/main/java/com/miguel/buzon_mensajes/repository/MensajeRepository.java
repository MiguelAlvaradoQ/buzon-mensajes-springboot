package com.miguel.buzon_mensajes.repository;

import com.miguel.buzon_mensajes.model.Mensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // ===== MÉTODOS CON PAGINACIÓN =====

    /**
     * Obtener todos los mensajes con paginación.
     * Ya viene incluido en JpaRepository, no necesitas declararlo.
     * Uso: repository.findAll(PageRequest.of(0, 10))
     */

    /**
     * Buscar mensajes por estado de leído con paginación.
     */
    Page<Mensaje> findByLeido(Boolean leido, Pageable pageable);

    /**
     * Buscar mensajes por email con paginación.
     */
    Page<Mensaje> findByEmail(String email, Pageable pageable);

    /**
     * Buscar mensajes por contenido con paginación.
     */
    Page<Mensaje> findByContenidoContaining(String palabra, Pageable pageable);

    // ===== MÉTODOS SIN PAGINACIÓN (mantener para casos específicos) =====

    List<Mensaje> findByLeido(Boolean leido);
    List<Mensaje> findByEmail(String email);
    Long countByLeido(Boolean leido);
    List<Mensaje> findByNombreIgnoreCase(String nombre);
    List<Mensaje> findByEmailAndLeido(String email, Boolean leido);
    List<Mensaje> findByContenidoContaining(String palabra);
    List<Mensaje> findByFechaCreacionAfter(LocalDateTime fecha);
    List<Mensaje> findTop10ByLeidoOrderByFechaCreacionDesc(Boolean leido);
}