package com.Demo.catalogo.config;

import com.Demo.catalogo.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthService authService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;  // Inyecta el filtro JWT

    public SecurityConfig(AuthService authService, JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.authService = authService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("Configurando PasswordEncoder: BCryptPasswordEncoder");  // Muestra cuando se configura el PasswordEncoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Iniciando configuración de seguridad con HttpSecurity...");  // Muestra cuando se empieza a configurar la seguridad
        http
                .csrf(AbstractHttpConfigurer::disable)  // Desactiva la protección CSRF
                .authorizeRequests(req -> {
                    System.out.println("Configurando autorización de solicitudes...");  // Muestra cuando se empieza a configurar los permisos

                    // Permite acceso público a los endpoints de autenticación, swagger, etc.
                    req.requestMatchers("/api/**", "/swagger-ui/**", "/v3/api-docs/**","/actuator/**")
                            .permitAll();
                    System.out.println("Acceso permitido para autenticación y documentación Swagger");

                    // Restricciones por método y rol
                    req.requestMatchers(HttpMethod.GET, "/api/**")
                            .hasAnyRole("ADMIN", "VIEWER", "EDITOR", "UPDATER");
                    System.out.println("Acceso GET restringido a roles ADMIN, VIEWER, EDITOR, UPDATER");

                    req.requestMatchers(HttpMethod.POST, "/api/**")
                            .hasAnyRole("ADMIN", "EDITOR", "UPDATER");
                    System.out.println("Acceso POST restringido a roles ADMIN, EDITOR, UPDATER");

                    req.requestMatchers(HttpMethod.PUT, "/api/**")
                            .hasAnyRole("ADMIN", "UPDATER");
                    System.out.println("Acceso PUT restringido a roles ADMIN, UPDATER");

                    req.requestMatchers(HttpMethod.DELETE, "/api/**")
                            .hasAnyRole("ADMIN", "EDITOR");
                    System.out.println("Acceso DELETE restringido a roles ADMIN, EDITOR");

                    // Cualquier otra solicitud requiere autenticación
                    req.anyRequest().authenticated();
                    System.out.println("Restricciones de acceso configuradas por roles y métodos HTTP");

                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // Sin estado (usando JWT)
                    System.out.println("Configuración de manejo de sesión: STATELESS");
                });

        // Agregar el filtro JWT antes del filtro de autenticación de Spring
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        System.out.println("Filtro JWT agregado antes del filtro de autenticación de Spring");

        return http.build();
    }
}




