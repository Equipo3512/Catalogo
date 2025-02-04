package com.Demo.catalogo.service;

import com.Demo.catalogo.service.DocumentacionService;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private DocumentacionService excelService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.user.organization}")
    private String nombreEmpresa;

    @Value("${mail.to}")
    private String toEmail;

    //Método para enviar el correo con el nombre del destinatario.
    public void sendEmail(String nombreDestinatario) throws MessagingException {
        // Si el nombre está vacío, asignar un nombre genérico
        if (nombreDestinatario == null || nombreDestinatario.isEmpty()) {
            nombreDestinatario = "Cinefilo";
        }

        // Crear el mensaje MIME
        MimeMessage message = javaMailSender.createMimeMessage(); //Crea el mensaje de correo.
        //Facilita la configuración del correo (remitente, destinatarios, asunto, cuerpo HTML, etc.).
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        try {
            // Establecer el remitente
            helper.setFrom(fromEmail);
            helper.setTo(toEmail.split(","));// Dirección del destinatario
            helper.setSubject("Catálogo de Películas Actualizado");  // Asunto

            // Crear el contexto Thymeleaf
            Context context = new Context();

            // Agregar las variables dinámicas al contexto
            context.setVariable("nombreDestinatario", nombreDestinatario);
            context.setVariable("nombreEmpresa", nombreEmpresa);  // Nombre de la empresa desde las propiedades
            context.setVariable("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));  // Fecha actual
            // Procesar el template y obtener el cuerpo del correo
            String body = templateEngine.process("email-template", context);

            // Establecer el cuerpo del correo en formato HTML
            helper.setText(body, true);

            try {
                // Obtener el archivo Excel desde el servicio
                byte[] excelFile = excelService.generarExcelPeliculas(); // Capturamos IOException
                DataSource dataSource = new ByteArrayDataSource(excelFile,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                helper.addAttachment("CatalogoPeliculas.xlsx", dataSource);
            } catch (IOException e) {
                System.err.println("Error al generar el archivo Excel: " + e.getMessage());
            }

            // Enviar el correo
            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new MessagingException("Error al enviar el correo", e);
        }
    }
}

