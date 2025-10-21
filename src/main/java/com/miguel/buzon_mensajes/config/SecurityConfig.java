package com.miguel.buzon_mensajes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // DESHABILITAR CSRF PARA DESARROLLO (cambiar en producción)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/", "/index", "/contacto", "/login", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // Para H2 console

                        // Solo POST a /api/mensajes es público
                        .requestMatchers(HttpMethod.POST, "/api/mensajes").permitAll()

                        // Rutas privadas
                        .requestMatchers("/admin/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/mensajes/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/mensajes/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/mensajes/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .permitAll()
                        .defaultSuccessUrl("/admin/mensajes", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                // Deshabilitar frameOptions para H2 console
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}