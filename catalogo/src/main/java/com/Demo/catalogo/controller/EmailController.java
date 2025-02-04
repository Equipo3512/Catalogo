package com.Demo.catalogo.controller;

import com.Demo.catalogo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * Ruta para enviar el correo con el nombre del destinatario.
     * @param nombreDestinatario El nombre del destinatario del correo.
     * @return Mensaje indicando si el correo fue enviado o si ocurrió un error.
     */
    @GetMapping("/send-email")
    public String sendEmail(@RequestParam(required = false) String nombreDestinatario) {
        try {
            // Llamar al servicio para enviar el correo, pasando el nombre del destinatario
            emailService.sendEmail(nombreDestinatario);

            return "Correo enviado con éxito";  // Mensaje de éxito
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al enviar el correo";  // Mensaje de error
        }
    }
}


