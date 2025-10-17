package com.miguel.buzon_mensajes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manejador global de excepciones para toda la aplicación.
 *
 * Esta clase intercepta TODAS las excepciones lanzadas por los controllers
 * y las convierte en respuestas HTTP apropiadas con formato ErrorResponse.
 *
 * @RestControllerAdvice aplica este manejador a todos los @RestController
 * automáticamente usando AOP (Aspect-Oriented Programming).
 *
 * Beneficios:
 * - Respuestas de error consistentes en toda la API
 * - Códigos HTTP correctos (404, 400, 500)
 * - Controllers más limpios (no necesitan try-catch)
 * - Centralización del manejo de errores
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja la excepción MensajeNotFoundException.
     *
     * Se ejecuta automáticamente cuando se lanza MensajeNotFoundException
     * en cualquier parte de la aplicación.
     *
     * @param ex La excepción que contiene el mensaje de error
     * @param request La petición HTTP que causó el error (para obtener la ruta)
     * @return ResponseEntity con ErrorResponse y status 404 (Not Found)
     *
     * Ejemplo de uso:
     * <pre>
     * // En el Service:
     * throw new MensajeNotFoundException(999L);
     *
     * // Este método intercepta y devuelve:
     * {
     *   "timestamp": "2025-10-17T16:05:36",
     *   "status": 404,
     *   "error": "Not Found",
     *   "message": "Mensaje no encontrado con ID: 999",
     *   "path": "/api/mensajes/999"
     * }
     * </pre>
     */
    @ExceptionHandler(MensajeNotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarMensajeNoEncontrado(
            MensajeNotFoundException ex,
            WebRequest request
    ) {
        // Crear la respuesta de error personalizada
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),                    // Timestamp del error
                HttpStatus.NOT_FOUND.value(),           // 404
                HttpStatus.NOT_FOUND.getReasonPhrase(), // "Not Found"
                ex.getMessage(),                         // "Mensaje no encontrado con ID: 999"
                request.getDescription(false).replace("uri=", "") // "/api/mensajes/999"
        );

        // Devolver ResponseEntity con status 404 y el ErrorResponse en el body
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Maneja errores de validación (@Valid en los DTOs).
     *
     * Se ejecuta cuando las validaciones de Bean Validation fallan
     * (por ejemplo: @NotBlank, @Email, @Size, etc.)
     *
     * @param ex La excepción que contiene los errores de validación
     * @param request La petición HTTP que causó el error
     * @return ResponseEntity con ErrorResponse y status 400 (Bad Request)
     *
     * Ejemplo:
     * <pre>
     * // Cliente envía:
     * {
     *   "nombre": "",           // Falla @NotBlank
     *   "email": "invalido",    // Falla @Email
     *   "contenido": "hola"     // Falla @Size(min=10)
     * }
     *
     * // Este método devuelve:
     * {
     *   "timestamp": "2025-10-17T16:05:36",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Errores de validación",
     *   "path": "/api/mensajes",
     *   "errors": [
     *     "El nombre es obligatorio",
     *     "El email debe ser válido",
     *     "El contenido debe tener entre 10 y 1000 caracteres"
     *   ]
     * }
     * </pre>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarErroresDeValidacion(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        // Extraer los mensajes de error de cada campo que falló la validación
        List<String> errores = new ArrayList<>();

        // ex.getBindingResult() contiene todos los errores de validación
        // getFieldErrors() obtiene los errores de los campos
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            // getDefaultMessage() obtiene el mensaje personalizado que pusiste
            // en las anotaciones (@NotBlank(message = "..."))
            errores.add(error.getDefaultMessage());
        }

        // Crear la respuesta de error con la lista de errores
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),           // 400
                HttpStatus.BAD_REQUEST.getReasonPhrase(), // "Bad Request"
                "Errores de validación en los datos enviados",
                request.getDescription(false).replace("uri=", ""),
                errores  // Lista de mensajes de error
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Maneja CUALQUIER excepción no capturada por otros @ExceptionHandler.
     *
     * Este es el manejador "catch-all" (atrapa todo).
     * Se ejecuta cuando ningún otro @ExceptionHandler específico coincide.
     *
     * @param ex La excepción genérica
     * @param request La petición HTTP
     * @return ResponseEntity con ErrorResponse y status 500 (Internal Server Error)
     *
     * Importante:
     * - Este método evita que se expongan stacktraces al cliente
     * - En producción, solo muestra un mensaje genérico
     * - En desarrollo, podrías incluir más detalles
     *
     * Ejemplo:
     * <pre>
     * // Si ocurre NullPointerException, SQLException, etc.
     * // que NO tienen @ExceptionHandler específico
     *
     * // Este método devuelve:
     * {
     *   "timestamp": "2025-10-17T16:05:36",
     *   "status": 500,
     *   "error": "Internal Server Error",
     *   "message": "Ocurrió un error interno en el servidor",
     *   "path": "/api/mensajes"
     * }
     * </pre>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarExcepcionGenerica(
            Exception ex,
            WebRequest request
    ) {
        // En producción: mensaje genérico (no exponer detalles técnicos)
        // En desarrollo: podrías poner ex.getMessage() para ver el error real

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),           // 500
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), // "Internal Server Error"
                "Ocurrió un error interno en el servidor. Por favor, contacta al administrador.",
                request.getDescription(false).replace("uri=", "")
        );

        // En desarrollo, puedes hacer log del error real para debugging
        // logger.error("Error no manejado: ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}