package com.Demo.catalogo.controller;

import com.Demo.catalogo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService userService;

    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Generar Token JWT",
            description = "Autentica al usuario mediante usuario y contraseña, y genera un token JWT para futuros accesos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token JWT generado con éxito"),
                    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        System.out.println("Iniciando proceso de login para el usuario: " + username);  // Muestra el usuario que está intentando iniciar sesión

        try {
            // Verificar las credenciales del usuario
            String result = userService.validateUser(username, password);

            if (result.equals("Usuario no encontrado") || result.equals("Contraseña incorrecta")) {
                System.out.println("Login fallido: " + result);  // Muestra el mensaje de error
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }

            System.out.println("Login exitoso para el usuario: " + username + " con rol: " + result);  // Muestra el éxito del login
            return ResponseEntity.ok("Token generado para el rol: '" + result + "'");  // Aquí se generaría el token real

        } catch (Exception e) {
            System.out.println("Error al autenticar al usuario " + username + ": " + e.getMessage());  // Muestra el error en caso de excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el token: " + e.getMessage());
        }
    }
}




