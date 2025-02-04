package com.Demo.catalogo.controller;

import com.Demo.catalogo.service.BarcodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/barcode")
public class BarcodeController {

    private final BarcodeService barcodeService;

    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @Operation(summary = "Genera un código de barras", description = "Devuelve un código de barras en formato PNG basado en el contenido proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código de barras generado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<byte[]> generateBarcode() {
        try {
            // Genera el código de barras pasando el contenido desde el parámetro
            byte[] barcodeImage = barcodeService.generateBarcode();

            // Configura los encabezados para la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/png");

            // Devuelve la imagen como respuesta con código de barras generado
            return new ResponseEntity<>(barcodeImage, headers, HttpStatus.OK);

        } catch (Exception e) {
            // En caso de error, devuelve un estado 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Genera un PDF con el código de barras", description = "Devuelve un archivo PDF con el código de barras generado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF generado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generateBarcodePdf() {
        try {
            byte[] pdfBytes = barcodeService.generateBarcodePdf();  // Llamar al método que genera el PDF
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=barcode.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);  // Retornar el PDF como archivo descargable
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Manejar errores
        }
    }

    @Operation(summary = "Genera un pdf con el código de barras y tabla estructurada",
            description = "Devuelve un archivo pdf con el código de barras generado, título centrado y una tabla estructurada de 5x3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento PDF generado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/PDFmodificado")
    public ResponseEntity<byte[]> generatePdf() throws Exception {
        try {
            // Generar el PDF con tabla y código de barras
            byte[] pdfBytes = barcodeService.PDFcompuesto();

            // Crear los encabezados para la respuesta del archivo PDF
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=archivo.pdf");

            // Devolver el archivo PDF
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Manejo de errores
        }
    }


    @Operation(summary = "Genera un word con el pdfmodificado",
            description = "Devuelve un archivo word con el código del pdfmodificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento Word generado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/WORDmodificado")
    public ResponseEntity<byte[]> generateWORD() {
        try {
            // Generar el documento Word con la tabla y el código de barras
            byte[] wordBytes = barcodeService.generateWord();

            // Configurar los encabezados para descargar el archivo Word
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=archivo.docx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(wordBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Manejo de errores
        }
    }

}

