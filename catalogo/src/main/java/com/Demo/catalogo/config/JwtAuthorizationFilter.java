package com.Demo.catalogo.config;

import com.Demo.catalogo.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // Usa la misma clave secreta fija para la validación
    private static final String SECRET_KEY = "CarlosSama555CarlosSama555CarlosSama555CarlosSama555";  // La clave secreta fija

    private final AuthService authService;  // Inyectar AuthService

    // Constructor para inyectar AuthService
    public JwtAuthorizationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader("Authorization");

        System.out.println("Token JWT recibido: " + jwtToken);  // Muestra el token recibido en la solicitud

        // Verifica que el token sea válido y tenga el prefijo "Bearer "
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);  // Elimina el "Bearer "

            try {
                System.out.println("Token sin prefijo Bearer: " + jwtToken);  // Muestra el token después de eliminar el prefijo

                // Extrae los claims del token (información del usuario)
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)  // Usa la clave secreta fija para validar
                        .parseClaimsJws(jwtToken)
                        .getBody();

                // Obtén el nombre de usuario (subject) y el rol desde el token
                String username = claims.getSubject();  // Nombre de usuario
                String role = claims.get("role", String.class);  // El rol del usuario desde el token

                System.out.println("Token validado. Usuario: " + username + ", Rol: " + role);  // Muestra la información extraída del token

                // Configura el contexto de seguridad con el usuario autenticado
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establece el contexto de seguridad con el usuario autenticado
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Obtén el método HTTP de la solicitud
                String method = request.getMethod();
                System.out.println("Método HTTP: " + method);  // Muestra el método HTTP de la solicitud

                // Verifica si el rol tiene permiso para este método HTTP
                if (!authService.hasPermission(role, method)) {
                    // Si el rol no tiene acceso al método, responde con un error 403 (Forbidden)
                    System.out.println("Acceso denegado: Rol no permitido para el método " + method);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: Rol no permitido para este método");
                    return;
                }

            } catch (SignatureException e) {
                // Si el JWT no es válido, registra el error
                System.out.println("Firma de JWT inválida");
                logger.warn("Invalid JWT signature");
            } catch (Exception e) {
                // Captura cualquier otro error
                System.out.println("Error al procesar el token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Token JWT no presente o no comienza con 'Bearer '");
        }

        // Continúa con el filtro, independientemente de si el token es válido o no
        System.out.println("llego a 'filterChain.doFilter'");  // Log de paso al siguiente filtro
        filterChain.doFilter(request, response);
    }
}



