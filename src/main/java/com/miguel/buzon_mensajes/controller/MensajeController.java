package com.miguel.buzon_mensajes.controller;

import com.miguel.buzon_mensajes.dto.MensajeRequestDTO;
import com.miguel.buzon_mensajes.dto.MensajeResponseDTO;
import com.miguel.buzon_mensajes.service.MensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar mensajes de contacto.
 *
 * Este controller maneja todas las operaciones CRUD relacionadas con mensajes.
 * Todos los endpoints están bajo el path base: /api/mensajes
 */
@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
@Tag(
        name = "Mensajes",
        description = "API para gestionar mensajes de contacto. " +
                "Permite crear, listar, actualizar y eliminar mensajes del buzón."
)
public class MensajeController {

    private final MensajeService mensajeService;

    /**
     * Crear un nuevo mensaje.
     *
     * Endpoint: POST /api/mensajes
     *
     * @param request DTO con los datos del mensaje (nombre, email, contenido)
     * @return Mensaje creado con status 201 (Created)
     */
    @PostMapping
    @Operation(
            summary = "Crear un nuevo mensaje",
            description = """
            Crea un nuevo mensaje de contacto en el sistema.
            
            El mensaje se crea con estado 'leido = false' por defecto.
            Se validan todos los campos según las reglas de negocio:
            - Nombre: obligatorio, no vacío
            - Email: obligatorio, formato válido
            - Contenido: obligatorio, entre 10 y 1000 caracteres
            
            Retorna el mensaje creado con su ID generado automáticamente.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Mensaje creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MensajeResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de respuesta exitosa",
                                    value = """
                        {
                          "id": 1,
                          "nombre": "Miguel Alvarado",
                          "email": "miguel@example.com",
                          "contenido": "Este es un mensaje de ejemplo de contacto",
                          "fechaCreacion": "2025-10-19T02:40:15",
                          "leido": false
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos (validaciones fallidas)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ejemplo de error de validación",
                                    value = """
                        {
                          "timestamp": "2025-10-19T02:40:15",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Errores de validación en los datos enviados",
                          "path": "/api/mensajes",
                          "errors": [
                            "El nombre es obligatorio",
                            "El email debe ser válido",
                            "El contenido debe tener entre 10 y 1000 caracteres"
                          ]
                        }
                        """
                            )
                    )
            )
    })
    public ResponseEntity<MensajeResponseDTO> crear(
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del mensaje a crear",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MensajeRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de mensaje válido",
                                    value = """
                        {
                          "nombre": "Miguel Alvarado",
                          "email": "miguel@example.com",
                          "contenido": "Hola, me gustaría obtener más información sobre sus servicios"
                        }
                        """
                            )
                    )
            )
            MensajeRequestDTO request
    ) {
        MensajeResponseDTO creado = mensajeService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Obtener todos los mensajes.
     *
     * Endpoint: GET /api/mensajes
     *
     * @return Lista de todos los mensajes (leídos y no leídos)
     */
    @GetMapping
    @Operation(
            summary = "Obtener todos los mensajes",
            description = """
            Retorna una lista con todos los mensajes del sistema,
            tanto leídos como no leídos, ordenados por fecha de creación.
            
            Si no hay mensajes, retorna una lista vacía.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de mensajes obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MensajeResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de respuesta con mensajes",
                                    value = """
                        [
                          {
                            "id": 1,
                            "nombre": "Miguel Alvarado",
                            "email": "miguel@example.com",
                            "contenido": "Primer mensaje de contacto",
                            "fechaCreacion": "2025-10-19T02:40:15",
                            "leido": false
                          },
                          {
                            "id": 2,
                            "nombre": "Ana García",
                            "email": "ana@example.com",
                            "contenido": "Segundo mensaje de contacto",
                            "fechaCreacion": "2025-10-19T02:45:30",
                            "leido": true
                          }
                        ]
                        """
                            )
                    )
            )
    })
    public ResponseEntity<List<MensajeResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(mensajeService.obtenerTodos());
    }

    /**
     * Obtener un mensaje por su ID.
     *
     * Endpoint: GET /api/mensajes/{id}
     *
     * @param id ID del mensaje a buscar
     * @return Mensaje encontrado con status 200
     * @throws "MensajeNotFoundException si el ID no existe (status 404)
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un mensaje por ID",
            description = """
            Busca y retorna un mensaje específico por su ID.
            
            Si el mensaje no existe, retorna error 404 (Not Found).
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Mensaje encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MensajeResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Mensaje no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ejemplo de mensaje no encontrado",
                                    value = """
                        {
                          "timestamp": "2025-10-19T02:40:15",
                          "status": 404,
                          "error": "Not Found",
                          "message": "Mensaje no encontrado con ID: 999",
                          "path": "/api/mensajes/999"
                        }
                        """
                            )
                    )
            )
    })
    public ResponseEntity<MensajeResponseDTO> obtenerPorId(
            @Parameter(
                    description = "ID del mensaje a buscar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(mensajeService.obtenerPorId(id));
    }

    /**
     * Obtener mensajes no leídos.
     *
     * Endpoint: GET /api/mensajes/no-leidos
     *
     * @return Lista de mensajes con leido = false
     */
    @GetMapping("/no-leidos")
    @Operation(
            summary = "Obtener mensajes no leídos",
            description = """
            Retorna únicamente los mensajes que no han sido marcados como leídos.
            
            Útil para mostrar notificaciones de mensajes pendientes.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de mensajes no leídos",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MensajeResponseDTO.class)
            )
    )
    public ResponseEntity<List<MensajeResponseDTO>> obtenerNoLeidos() {
        return ResponseEntity.ok(mensajeService.obtenerNoLeidos());
    }

    /**
     * Obtener mensajes por email.
     *
     * Endpoint: GET /api/mensajes/email/{email}
     *
     * @param email Email del remitente
     * @return Lista de mensajes de ese email
     */
    @GetMapping("/email/{email}")
    @Operation(
            summary = "Obtener mensajes por email",
            description = """
            Busca todos los mensajes enviados desde un email específico.
            
            Útil para ver el historial de mensajes de un contacto.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de mensajes del email especificado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MensajeResponseDTO.class)
            )
    )
    public ResponseEntity<List<MensajeResponseDTO>> obtenerPorEmail(
            @Parameter(
                    description = "Email del remitente",
                    required = true,
                    example = "miguel@example.com"
            )
            @PathVariable String email
    ) {
        return ResponseEntity.ok(mensajeService.obtenerPorEmail(email));
    }

    /**
     * Marcar un mensaje como leído.
     *
     * Endpoint: PATCH /api/mensajes/{id}/leido
     *
     * @param id ID del mensaje a marcar
     * @return Mensaje actualizado con leido = true
     * @throws "MensajeNotFoundException si el ID no existe (status 404)
     */
    @PatchMapping("/{id}/leido")
    @Operation(
            summary = "Marcar mensaje como leído",
            description = """
            Actualiza el estado del mensaje a 'leído'.
            
            Cambia el campo 'leido' de false a true.
            Si el mensaje no existe, retorna error 404.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Mensaje marcado como leído exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MensajeResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Mensaje no encontrado",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<MensajeResponseDTO> marcarComoLeido(
            @Parameter(
                    description = "ID del mensaje a marcar como leído",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(mensajeService.marcarComoLeido(id));
    }

    /**
     * Eliminar un mensaje.
     *
     * Endpoint: DELETE /api/mensajes/{id}
     *
     * @param id ID del mensaje a eliminar
     * @return Status 204 (No Content) si se eliminó exitosamente
     * @throws "MensajeNotFoundException si el ID no existe (status 404)
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un mensaje",
            description = """
            Elimina permanentemente un mensaje del sistema.
            
            Esta operación no se puede deshacer.
            Si el mensaje no existe, retorna error 404.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Mensaje eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Mensaje no encontrado",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(
                    description = "ID del mensaje a eliminar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        mensajeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Contar mensajes no leídos.
     *
     * Endpoint: GET /api/mensajes/no-leidos/count
     *
     * @return Cantidad de mensajes con leido = false
     */
    @GetMapping("/no-leidos/count")
    @Operation(
            summary = "Contar mensajes no leídos",
            description = """
            Retorna la cantidad total de mensajes no leídos.
            
            Útil para mostrar badges o notificaciones (ej: "Tienes 5 mensajes nuevos").
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cantidad de mensajes no leídos",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Ejemplo de respuesta",
                            value = "5"
                    )
            )
    )
    public ResponseEntity<Long> contarNoLeidos() {
        return ResponseEntity.ok(mensajeService.contarNoLeidos());
    }
}