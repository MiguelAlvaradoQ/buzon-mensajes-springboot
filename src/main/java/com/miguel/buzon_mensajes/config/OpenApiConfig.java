package com.miguel.buzon_mensajes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI (Swagger) para la documentación de la API.
 *
 * Esta clase configura la información que aparecerá en la interfaz de Swagger UI:
 * - Título y descripción de la API
 * - Versión
 * - Información de contacto
 * - Licencia
 * - Servidores disponibles
 *
 * La documentación estará disponible en:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configuración principal de OpenAPI.
     *
     * @Bean indica que Spring debe gestionar este objeto
     * y estará disponible para SpringDoc.
     *
     * @return Objeto OpenAPI configurado con la información de la API
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Información general de la API
                .info(new Info()
                        // Título que aparece en grande arriba
                        .title("Buzón de Mensajes API")

                        // Versión de tu API (puedes usar versionamiento semántico)
                        .version("1.0.0")

                        // Descripción detallada de qué hace tu API
                        .description("""
                                API REST para gestionar un sistema de mensajes de contacto.
                                
                                Características:
                                - Crear mensajes de contacto
                                - Listar mensajes (todos, no leídos, por email)
                                - Marcar mensajes como leídos
                                - Eliminar mensajes
                                - Contar mensajes no leídos
                                
                                Tecnologías utilizadas:
                                - Spring Boot 3.3.5
                                - Java 21
                                - JPA/Hibernate
                                - H2 Database
                                - Bean Validation
                                - Manejo de excepciones centralizado
                                """)

                        // Información de contacto (tu información)
                        .contact(new Contact()
                                .name("Miguel Alvarado")
                                .email("miguel@example.com")
                                .url("https://github.com/MiguelAlvaradoQ"))

                        // Licencia del proyecto
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))

                // Servidores donde está disponible la API
                .servers(List.of(
                        // Servidor local (desarrollo)
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo local")

                        // Puedes agregar más servidores (producción, staging, etc.)
                        // Cuando subas a la nube, agregas:
                        // new Server()
                        //     .url("https://tu-app.railway.app")
                        //     .description("Servidor de producción")
                ));
    }
}