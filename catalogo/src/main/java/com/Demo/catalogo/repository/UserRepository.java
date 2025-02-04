package com.Demo.catalogo.repository;

import java.util.ArrayList;
import java.util.List;
import com.Demo.catalogo.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    // Lista en memoria para almacenar usuarios
    private List<User> users;

    public UserRepository() {
        users = new ArrayList<>();
        // Usuarios predefinidos
        users.add(new User(1, "admin", "admin123", "ADMIN")); //Puede hacer todo
        users.add(new User(2, "viewer", "viewer123", "VIEWER")); // Solo puede usar los "GET"
        users.add(new User(3, "editor", "editor123", "EDITOR")); // Solo puede usar los "POST" y "DELETE"
        users.add(new User(4, "updater", "updater123", "UPDATER")); //Solo puede usar los "PUT"
    }

    // Método para buscar usuario por nombre de usuario
    public String findUserByUsername(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (user.getPassword().equals(password)) {
                    return user.getRole(); // Usuario válido, retornar su rol
                } else {
                    return "Contraseña incorrecta"; // Usuario encontrado pero contraseña inválida
                }
            }
        }
        return "Usuario no encontrado"; // Usuario no existe en el repositorio
    }
}

