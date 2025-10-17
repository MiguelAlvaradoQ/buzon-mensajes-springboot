package com.miguel.buzon_mensajes.service.impl;

import com.miguel.buzon_mensajes.dto.MensajeRequestDTO;
import com.miguel.buzon_mensajes.dto.MensajeResponseDTO;
import com.miguel.buzon_mensajes.exception.MensajeNotFoundException;
import com.miguel.buzon_mensajes.model.Mensaje;
import com.miguel.buzon_mensajes.repository.MensajeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MensajeServiceImpl.
 *
 * Estos tests verifican que la lógica de negocio del servicio funcione correctamente
 * de forma aislada, sin depender de la base de datos real.
 *
 * Usamos:
 * - @ExtendWith(MockitoExtension.class) para habilitar Mockito
 * - @Mock para simular dependencias (MensajeRepository)
 * - @InjectMocks para inyectar los mocks en el servicio
 * - AssertJ para aserciones más legibles
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para MensajeServiceImpl")
class MensajeServiceImplTest {

    /**
     * Mock del repositorio.
     *
     * @Mock crea un "simulacro" del repositorio.
     * No es el repositorio real, es una imitación que podemos controlar.
     *
     * ¿Por qué?
     * - No queremos usar la base de datos real en tests
     * - Queremos controlar exactamente qué devuelve
     * - Los tests son mucho más rápidos
     */
    @Mock
    private MensajeRepository mensajeRepository;

    /**
     * Instancia del servicio que vamos a probar.
     *
     * @InjectMocks crea una instancia real de MensajeServiceImpl
     * y automáticamente le inyecta los mocks (mensajeRepository).
     *
     * Es como si hicieras:
     * mensajeService = new MensajeServiceImpl(mensajeRepository);
     *
     * Pero mensajeRepository es un mock, no el real.
     */
    @InjectMocks
    private MensajeServiceImpl mensajeService;

    // Variables para reutilizar en los tests
    private Mensaje mensaje;
    private MensajeRequestDTO mensajeRequestDTO;

    /**
     * Se ejecuta ANTES de cada test.
     *
     * Aquí preparamos datos de prueba que usaremos en múltiples tests.
     * Esto evita duplicar código en cada test.
     *
     * @BeforeEach garantiza que cada test empiece con datos "frescos".
     */
    @BeforeEach
    void setUp() {
        // Crear un mensaje de ejemplo para usar en los tests
        mensaje = new Mensaje();
        mensaje.setId(1L);
        mensaje.setNombre("Miguel Alvarado");
        mensaje.setEmail("miguel@example.com");
        mensaje.setContenido("Este es un mensaje de prueba para testing");
        mensaje.setFechaCreacion(LocalDateTime.now());
        mensaje.setLeido(false);

        // Crear un DTO de request para tests de creación
        mensajeRequestDTO = new MensajeRequestDTO();
        mensajeRequestDTO.setNombre("Miguel Alvarado");
        mensajeRequestDTO.setEmail("miguel@example.com");
        mensajeRequestDTO.setContenido("Este es un mensaje de prueba para testing");
    }

    /**
     * Test para el método crear().
     *
     * Escenario: Cliente envía datos válidos para crear un mensaje
     * Resultado esperado: El mensaje se guarda y se devuelve el DTO con los datos correctos
     */
    @Test
    @DisplayName("Crear mensaje con datos válidos debe guardarlo y retornar DTO")
    void crear_cuandoDatosValidos_debeGuardarYRetornarDTO() {
        // ==========================================
        // ARRANGE (Preparar)
        // ==========================================

        // 1. Configurar el mock del repositorio
        // Le decimos: "Cuando alguien llame a save() con CUALQUIER Mensaje,
        //              devuelve el 'mensaje' que preparamos en setUp()"
        when(mensajeRepository.save(any(Mensaje.class))).thenReturn(mensaje);

        // En este punto:
        // - mensajeRequestDTO tiene los datos de entrada (ya preparado en setUp)
        // - mensaje tiene los datos que "simularemos" que devuelve la BD (ya preparado en setUp)

        // ==========================================
        // ACT (Actuar)
        // ==========================================

        // 2. Ejecutar el método que queremos probar
        MensajeResponseDTO resultado = mensajeService.crear(mensajeRequestDTO);

        // Internamente esto hace:
        // 1. Convierte mensajeRequestDTO a Mensaje
        // 2. Llama a mensajeRepository.save(mensaje)  ← Aquí usa nuestro mock
        // 3. El mock devuelve nuestro 'mensaje' preparado
        // 4. Convierte ese 'mensaje' a MensajeResponseDTO
        // 5. Lo retorna

        // ==========================================
        // ASSERT (Afirmar/Verificar)
        // ==========================================

        // 3. Verificar que el resultado NO es null
        assertThat(resultado).isNotNull();

        // 4. Verificar que el DTO tiene los datos correctos
        assertThat(resultado.getNombre()).isEqualTo(mensajeRequestDTO.getNombre());
        assertThat(resultado.getEmail()).isEqualTo(mensajeRequestDTO.getEmail());
        assertThat(resultado.getContenido()).isEqualTo(mensajeRequestDTO.getContenido());
        assertThat(resultado.getLeido()).isFalse();  // Debe estar en false por defecto

        // 5. Verificar que se llamó al repositorio EXACTAMENTE una vez
        verify(mensajeRepository, times(1)).save(any(Mensaje.class));

        // Esto verifica que el servicio realmente intentó guardar en la BD
        // times(1) = exactamente 1 vez (ni 0, ni 2)
    }

    /**
     * Test para obtenerPorId() cuando el mensaje NO existe.
     *
     * Escenario: Cliente busca un mensaje con un ID que no existe en la BD
     * Resultado esperado: Se lanza MensajeNotFoundException con mensaje apropiado
     */
    @Test
    @DisplayName("Obtener mensaje por ID inexistente debe lanzar MensajeNotFoundException")
    void obtenerPorId_cuandoMensajeNoExiste_debeLanzarExcepcion() {
        // ==========================================
        // ARRANGE (Preparar)
        // ==========================================

        // 1. ID que NO existe
        Long idInexistente = 999L;

        // 2. Configurar el mock para que devuelva Optional.empty()
        // Esto simula que la BD NO encontró el mensaje
        when(mensajeRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        // En este punto:
        // - Si alguien busca ID 999, el mock devolverá Optional.empty()
        // - El servicio debería lanzar MensajeNotFoundException

        // ==========================================
        // ACT & ASSERT (Actuar y Verificar juntos)
        // ==========================================

        // 3. Verificar que se lanza la excepción correcta
        assertThatThrownBy(() -> mensajeService.obtenerPorId(idInexistente))
                .isInstanceOf(MensajeNotFoundException.class)
                .hasMessageContaining("Mensaje no encontrado con ID: 999");

        // Desglose:
        // assertThatThrownBy(() -> ...)  → Ejecuta el código y espera una excepción
        // .isInstanceOf(X.class)          → Verifica que la excepción es del tipo correcto
        // .hasMessageContaining("...")    → Verifica que el mensaje contiene el texto

        // 4. Verificar que se intentó buscar en el repositorio
        verify(mensajeRepository, times(1)).findById(idInexistente);
    }

    /**
     * Test para obtenerPorId() cuando el mensaje SÍ existe.
     *
     * Escenario: Cliente busca un mensaje que existe en la BD
     * Resultado esperado: Retorna el MensajeResponseDTO con los datos correctos
     */
    @Test
    @DisplayName("Obtener mensaje por ID existente debe retornar el DTO correcto")
    void obtenerPorId_cuandoMensajeExiste_debeRetornarDTO() {
        // ==========================================
        // ARRANGE
        // ==========================================
        Long idExistente = 1L;

        // Configurar mock para devolver Optional con el mensaje
        when(mensajeRepository.findById(idExistente))
                .thenReturn(Optional.of(mensaje));

        // ==========================================
        // ACT
        // ==========================================
        MensajeResponseDTO resultado = mensajeService.obtenerPorId(idExistente);

        // ==========================================
        // ASSERT
        // ==========================================
        // Verificar que el resultado no es null
        assertThat(resultado).isNotNull();

        // Verificar que tiene los datos correctos
        assertThat(resultado.getId()).isEqualTo(mensaje.getId());
        assertThat(resultado.getNombre()).isEqualTo(mensaje.getNombre());
        assertThat(resultado.getEmail()).isEqualTo(mensaje.getEmail());
        assertThat(resultado.getContenido()).isEqualTo(mensaje.getContenido());
        assertThat(resultado.getLeido()).isEqualTo(mensaje.getLeido());

        // Verificar que se buscó en el repositorio
        verify(mensajeRepository, times(1)).findById(idExistente);
    }

    /**
     * Test para marcarComoLeido() cuando el mensaje existe.
     *
     * Escenario: Cliente marca un mensaje existente como leído
     * Resultado esperado: El mensaje se actualiza a leido=true y se guarda
     */
    @Test
    @DisplayName("Marcar mensaje como leído debe actualizar el estado")
    void marcarComoLeido_cuandoMensajeExiste_debeActualizarEstado() {
        // ==========================================
        // ARRANGE
        // ==========================================
        Long idExistente = 1L;

        // El mensaje inicialmente está NO leído
        mensaje.setLeido(false);

        // Configurar mock para findById (devuelve el mensaje)
        when(mensajeRepository.findById(idExistente))
                .thenReturn(Optional.of(mensaje));

        // Configurar mock para save (devuelve el mensaje actualizado)
        when(mensajeRepository.save(any(Mensaje.class)))
                .thenAnswer(invocation -> {
                    // Simular que la BD guardó el mensaje
                    Mensaje mensajeGuardado = invocation.getArgument(0);
                    return mensajeGuardado;
                });

        // ==========================================
        // ACT
        // ==========================================
        MensajeResponseDTO resultado = mensajeService.marcarComoLeido(idExistente);

        // ==========================================
        // ASSERT
        // ==========================================
        // Verificar que el resultado tiene leido=true
        assertThat(resultado).isNotNull();
        assertThat(resultado.getLeido()).isTrue();

        // Verificar que se buscó el mensaje
        verify(mensajeRepository, times(1)).findById(idExistente);

        // Verificar que se guardó el mensaje actualizado
        verify(mensajeRepository, times(1)).save(any(Mensaje.class));
    }

    /**
     * Test para eliminar() cuando el mensaje NO existe.
     *
     * Escenario: Cliente intenta eliminar un mensaje que no existe
     * Resultado esperado: Se lanza MensajeNotFoundException
     */
    @Test
    @DisplayName("Eliminar mensaje inexistente debe lanzar MensajeNotFoundException")
    void eliminar_cuandoMensajeNoExiste_debeLanzarExcepcion() {
        // ==========================================
        // ARRANGE
        // ==========================================
        Long idInexistente = 999L;

        // Configurar mock para que diga que NO existe
        when(mensajeRepository.existsById(idInexistente))
                .thenReturn(false);

        // ==========================================
        // ACT & ASSERT
        // ==========================================
        // Verificar que se lanza la excepción
        assertThatThrownBy(() -> mensajeService.eliminar(idInexistente))
                .isInstanceOf(MensajeNotFoundException.class)
                .hasMessageContaining("Mensaje no encontrado con ID: 999");

        // Verificar que se verificó la existencia
        verify(mensajeRepository, times(1)).existsById(idInexistente);

        // Verificar que NO se intentó eliminar (porque no existe)
        verify(mensajeRepository, never()).deleteById(any(Long.class));
    }


}