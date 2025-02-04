package com.Demo.catalogo.controller;

import com.Demo.catalogo.service.DocumentacionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("/api/documentacion")
public class DocumentacionController {

    private final DocumentacionService documentacionService;

    public DocumentacionController(DocumentacionService documentacionService) {
        this.documentacionService = documentacionService;
    }

    @GetMapping("/peliculas/excel")
    public ResponseEntity<byte[]> descargarExcelPeliculas() throws IOException {
        byte[] excelBytes = documentacionService.generarExcelPeliculas();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Peliculas.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}
