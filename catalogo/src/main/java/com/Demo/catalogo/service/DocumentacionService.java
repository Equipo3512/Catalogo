package com.Demo.catalogo.service;

import com.Demo.catalogo.model.Pelicula;
import com.Demo.catalogo.repository.ICatalogoRepository;
import com.Demo.catalogo.DTO.PeliculaDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class DocumentacionService {

    private final ICatalogoRepository catalogoRepository;

    public DocumentacionService(ICatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    public byte[] generarExcelPeliculas() throws IOException {
        // Obtener todas las películas sin paginación y ordenarlas por año
        List<Pelicula> peliculas = catalogoRepository.findAll(); // Aquí obtenemos todas las películas

        // Ordenar las películas por año (descendente)
        peliculas.sort((p1, p2) -> p2.getAnio().compareTo(p1.getAnio())); // Ordenar por año

        // Convertir las películas a DTOs
        List<PeliculaDTO> peliculaDTOs = peliculas.stream()
                .map(PeliculaDTO::fromEntity)
                .toList();

        // Crear un libro de trabajo Excel (XSSFWorkbook)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Peliculas");

        // Crear un estilo para las celdas (gris claro para todas las celdas)
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // Fondo gris claro
        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Crear un estilo para los títulos (gris más claro)
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true); // Títulos en negritas
        headerFont.setFontHeightInPoints((short) 12); // Tamaño de fuente más grande
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER); // Alinear al centro
        headerStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex()); // Fondo gris más oscuro
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // Relleno sólido para el color de fondo

        // Crear la fila de títulos
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Año");
        headerRow.createCell(3).setCellValue("Género");
        headerRow.createCell(4).setCellValue("Duración");
        headerRow.createCell(5).setCellValue("Proveedor");

        // Aplicar estilo de título a las celdas
        for (int i = 0; i < 6; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }

        // Llenar las filas con la información de las películas
        int rowNum = 1;
        for (PeliculaDTO pelicula : peliculaDTOs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(pelicula.getId());
            row.createCell(1).setCellValue(pelicula.getNombre());
            row.createCell(2).setCellValue(pelicula.getAnio());
            row.createCell(3).setCellValue(pelicula.getGenero());
            row.createCell(4).setCellValue(pelicula.getDuracion());
            row.createCell(5).setCellValue(pelicula.getProveedorNombre());

            // Aplicar estilo a toda la fila
            for (int i = 0; i < 6; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Insertar una imagen en el documento Excel (por ejemplo, una imagen de encabezado)
        try (InputStream imageStream = getClass().getClassLoader().getResourceAsStream("images/NEOS.png")) {
            if (imageStream == null) {
                throw new IOException("No se encontró la imagen en el classpath.");
            }

            // Redimensionar la imagen
            byte[] imageBytes = IOUtils.toByteArray(imageStream);
            int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);

            Drawing drawing = sheet.createDrawingPatriarch();

            // Crear un ancla para la imagen
            ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();

            // Colocar la imagen después de la tabla, en la fila 1 pero en la columna G en adelante
            anchor.setCol1(6); // La columna G es la columna 6 (G es la séptima columna, pero la numeración comienza en 0)
            anchor.setRow1(0);

            // Establecer el tamaño de la imagen
            anchor.setCol2(606 / 75); // Se ajusta el ancho
            anchor.setRow2(269 / 35); // Ajuste proporcional para la altura de la imagen

            // Insertar la imagen en la ubicación correcta
            drawing.createPicture(anchor, pictureIdx);
        } catch (IOException e) {
            System.out.println("Error al leer la imagen: " + e.getMessage());
            e.printStackTrace();
        }

        // Alineación de celdas y ajuste automático de columnas
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i); // Ajuste automático del tamaño de la columna
        }

        // Escribir el contenido del Excel a un ByteArrayOutputStream
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();  // Convertir a byte[] para el controlador
        }
    }
}

