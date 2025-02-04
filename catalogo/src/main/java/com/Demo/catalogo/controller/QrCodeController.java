package com.Demo.catalogo.controller;

import com.Demo.catalogo.service.QrCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @Operation(summary = "Genera un código QR", description = "Devuelve un código QR en formato PNG basado en la configuración del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código QR generado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<byte[]> generateQrCode() {
        try {
            byte[] qrImage = qrCodeService.generateQrCode();
            // Crea los encabezados HTTP de la respuesta
            HttpHeaders headers = new HttpHeaders();
            // Especifica que el contenido de la respuesta será una imagen en formato PNG
            headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
            // Retorna la imagen QR como un arreglo de bytes con el estado HTTP 200 (OK)
            return new ResponseEntity<>(qrImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            // En caso de error, devuelve una respuesta con el estado HTTP 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Genera un PDF con el código QR", description = "Devuelve un archivo PDF con el QR generado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF generado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generateQrPdf() {
        try {
            byte[] pdfBytes = qrCodeService.generatePdf();
            // Crea los encabezados de la respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            // Indica que el archivo se enviará como un adjunto con el nombre "qr.pdf"
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr.pdf");
            // Especifica que el tipo de contenido de la respuesta es un archivo PDF
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
            // Retorna la respuesta con el archivo PDF, los encabezados y el estado HTTP 200 (OK)
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            // En caso de error, devuelve una respuesta con el estado HTTP 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
