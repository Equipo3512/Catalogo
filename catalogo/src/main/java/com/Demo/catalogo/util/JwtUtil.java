package com.Demo.catalogo.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

    // Define una clave secreta fija
    private static final String SECRET_KEY = "CarlosSama555CarlosSama555CarlosSama555CarlosSama555"; // La clave secreta fija

    // Método para generar un JWT
    public static String generateToken(String username, String role) {
        System.out.println("Generando token para el usuario: " + username + " con rol: " + role);  // Muestra los datos del token

        String token = Jwts.builder()
                .setSubject(username)  // Es el usuario
                .claim("role", role)   // El rol asignado
                .setIssuedAt(new Date())  // Fecha de emisión del token
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Expiración: 1 día
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // Firma con la clave secreta fija
                .compact();  // Genera el token

        System.out.println("Token generado: " + token);  // Muestra el token generado

        return token;
    }
}


