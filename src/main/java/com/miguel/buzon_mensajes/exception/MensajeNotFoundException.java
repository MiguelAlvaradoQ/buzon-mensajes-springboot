package com.miguel.buzon_mensajes.exception;

/**
 * Excepción personalizada que se lanza cuando no se encuentra un mensaje con el ID especificado.
 *
 * ¿Por qué extends RuntimeException?
 * - Es una excepción NO VERIFICADA (unchecked), no requiere 'throws' en las firmas
 * - Spring Boot la maneja automáticamente con @RestControllerAdvice
 * - Mantiene el código limpio sin try-catch obligatorios
 *
 * Esta excepción será capturada por GlobalExceptionHandler y convertida
 * a una respuesta HTTP 404 (Not Found) con formato ErrorResponse.
 *
 * Ejemplo de uso:
 * <pre>
 * Mensaje mensaje = mensajeRepository.findById(id)
 *     .orElseThrow(() -> new MensajeNotFoundException(id));
 * </pre>
 */
public class MensajeNotFoundException extends RuntimeException {

    /**
     * Constructor que recibe el ID del mensaje no encontrado.
     * Construye automáticamente un mensaje descriptivo.
     *
     * Este es el constructor más usado en el proyecto.
     *
     * @param id El ID del mensaje que no se encontró en la base de datos
     *
     * Ejemplo:
     * <pre>
     * throw new MensajeNotFoundException(999L);
     * // Resultado: "Mensaje no encontrado con ID: 999"
     * </pre>
     */
    public MensajeNotFoundException(Long id) {
        super("Mensaje no encontrado con ID: " + id);
    }

    /**
     * Constructor que permite especificar un mensaje personalizado.
     *
     * Útil para casos especiales donde necesitas un mensaje diferente
     * al formato estándar.
     *
     * @param mensaje Mensaje descriptivo personalizado del error
     *
     * Ejemplo:
     * <pre>
     * throw new MensajeNotFoundException("No se encontró el mensaje del usuario admin");
     * </pre>
     */
    public MensajeNotFoundException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor que permite especificar mensaje Y causa raíz.
     *
     * Permite "encadenar" excepciones (exception chaining).
     * Útil cuando quieres preservar la excepción original que causó el problema.
     *
     * @param mensaje Mensaje descriptivo del error
     * @param causa La excepción original que causó este error
     *
     * Ejemplo:
     * <pre>
     * try {
     *     // operación que puede fallar
     * } catch (DataAccessException e) {
     *     throw new MensajeNotFoundException("Error al buscar mensaje", e);
     * }
     * </pre>
     *
     * Beneficio: En los logs verás:
     * MensajeNotFoundException: Error al buscar mensaje
     *   Caused by: DataAccessException: Connection timeout
     *     at ...
     */
    public MensajeNotFoundException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}