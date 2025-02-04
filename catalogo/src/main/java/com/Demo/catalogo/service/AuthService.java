package com.Demo.catalogo.service;

import com.Demo.catalogo.repository.UserRepository;
import com.Demo.catalogo.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    private UserRepository userRepository;

    // Mapeo de usuarios a roles
    private final Map<String, String> userRoles = new HashMap<>();

    // Mapeo de roles a permisos de métodos HTTP
    private final Map<String, Set<String>> rolePermissions = new HashMap<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;

        // Mapeo de usuarios a roles
        userRoles.put("admin", "ADMIN");
        userRoles.put("viewer", "VIEWER");
        userRoles.put("editor", "EDITOR");
        userRoles.put("updater", "UPDATER");

        // Mapeo de roles a permisos de métodos HTTP
        rolePermissions.put("ADMIN", Set.of("GET", "POST", "PUT", "DELETE"));
        rolePermissions.put("VIEWER", Set.of("GET"));
        rolePermissions.put("EDITOR", Set.of("POST", "DELETE"));
        rolePermissions.put("UPDATER", Set.of("PUT"));
    }

    // Método para validar el usuario y generar un token
    public String validateUser(String username, String password) {
        System.out.println("Validando usuario: " + username);  // Muestra el usuario que está siendo validado

        // Llama al método del repositorio y obtiene el resultado (puede ser un mensaje de error o un rol)
        String result = userRepository.findUserByUsername(username, password);

        // Verifica si el resultado es un mensaje de error
        if ("Usuario no encontrado".equals(result)) {
            System.out.println("Error: Usuario no encontrado");
            return result;  // Devuelve el mensaje de error si el usuario no se encuentra
        }

        if ("Contraseña incorrecta".equals(result)) {
            System.out.println("Error: Contraseña incorrecta");
            return result;  // Devuelve el mensaje de error si la contraseña es incorrecta
        }

        // Si el resultado es un rol (ADMIN, VIEWER, EDITOR, UPDATER), genera el token
        if ("ADMIN".equals(result) || "VIEWER".equals(result) || "EDITOR".equals(result) || "UPDATER".equals(result)) {
            System.out.println("Rol encontrado: " + result);  // Muestra el rol encontrado
            String token = JwtUtil.generateToken(username, result);  // Llamada al método para generar el token
            System.out.println("Token generado: " + token);  // Muestra el token generado
            return token;  // Devuelve el token generado
        }

        // Si el resultado no es ni un error ni un rol válido, se puede manejar aquí si es necesario
        System.out.println("Error: Rol desconocido");
        return "Rol desconocido";  // O cualquier otro valor de manejo de error
    }

    // Método para verificar si el rol tiene permiso para un método HTTP específico
    public boolean hasPermission(String role, String method) {
        System.out.println("Verificando permisos para el rol: " + role + " y método: " + method);  // Muestra el rol y el método

        Set<String> permissions = rolePermissions.get(role);
        if (permissions != null && permissions.contains(method)) {
            System.out.println("Permiso otorgado: El rol tiene permiso para este método");
            return true;  // Si el rol tiene el permiso
        }

        System.out.println("Permiso denegado: El rol no tiene permiso para este método");
        return false;  // Si el rol no tiene el permiso
    }
}

