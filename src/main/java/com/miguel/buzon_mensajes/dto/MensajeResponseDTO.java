package com.miguel.buzon_mensajes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponseDTO {

    private Long id;

    private String nombre;

    private String email;

    private String contenido;

    private LocalDateTime fechaCreacion;

    private Boolean leido;
}