package com.Demo.catalogo.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.imageio.ImageIO;
@Service
public class BarcodeService {

    @Value("${app.user.name}")
    private String userName;  // Nombre del usuario desde las propiedades

    private static final int BARCODE_WIDTH = 300;
    private static final int BARCODE_HEIGHT = 100;

    // Método para generar un código de barras con la estructura requerida
    public byte[] generateBarcode() throws Exception {
        // Generar el número aleatorio con 4 dígitos y 2 decimales
        Random random = new Random();
        double randomNumber = Math.round((random.nextInt(9999) + 1) + random.nextDouble() * 100.0) / 100.0;
        // Obtener la fecha actual en formato "dd/MM/yyyy HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentTime = sdf.format(new Date());
        // Crear el contenido del código de barras con la estructura solicitada
        String barcodeContent = "Neo|" + userName + "|" + randomNumber + "|" + currentTime + "|Ris";
        // Generar el código de barras a partir del contenido
        BitMatrix matrix = new MultiFormatWriter()
                .encode(barcodeContent, BarcodeFormat.CODE_128, BARCODE_WIDTH, BARCODE_HEIGHT);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", baos);
        return baos.toByteArray();
    }

    // Método para generar el PDF con el código de barras
    public byte[] generateBarcodePdf() throws Exception {
        byte[] barcodeBytes = generateBarcode();  // Genera el código de barras

        // Crear el PDF con el código de barras
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        ImageData imageData = ImageDataFactory.create(barcodeBytes);
        com.itextpdf.layout.element.Image barcodeImage = new com.itextpdf.layout.element.Image(imageData);
        document.add(barcodeImage);  // Agregar la imagen del código de barras al documento PDF
        document.close();
        return outputStream.toByteArray();  // Retornar el PDF en formato byte[]
    }

    // Método para generar el PDF con título, código de barras y tabla
    public byte[] PDFcompuesto() throws Exception {
        byte[] barcodeBytes = generateBarcode();  // Genera el código de barras

        // Crear el PDF con título, código de barras y tabla
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);  // Creación del PdfDocument
        Document document = new Document(pdfDoc);  // Crear el Document con PdfDocument

        // Título con fuente predeterminada
        Paragraph title = new Paragraph("Codigo de barras")
                .setFontSize(24);
        document.add(title);

        // Salto de línea antes del código de barras
        document.add(new Paragraph("\n"));

        // Insertar el código de barras generado
        ImageData imageData = ImageDataFactory.create(barcodeBytes);
        com.itextpdf.layout.element.Image barcodeImage = new com.itextpdf.layout.element.Image(imageData);
        document.add(barcodeImage);  // Agregar la imagen del código de barras al documento

        // Salto de línea después del código de barras
        document.add(new Paragraph("\n"));

        // Crear la tabla 5x3
        Table table = new Table(5);  // 5 columnas

        // Llenar la tabla con celdas
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                Cell cell = new Cell().add(new Paragraph("Fila " + (row + 1) + " Columna " + (col + 1)))
                        .setTextAlignment(TextAlignment.CENTER)  // Alineación del texto
                        .setVerticalAlignment(VerticalAlignment.MIDDLE);  // Alineación vertical centrada
                table.addCell(cell);
            }
        }
        document.add(table);  // Agregar la tabla al documento

        // Cerrar el documento
        document.close();
        return outputStream.toByteArray();  // Retornar el PDF en formato byte[]
    }

    // Método para generar el Word con título, código de barras y tabla
    public byte[] generateWord() throws Exception {
        // Obtener la imagen del código de barras (simulando la generación del PDF)
        byte[] barcodeBytes = generateBarcode(); // Asegúrate de tener este método implementado
        BufferedImage barcodeImage = ImageIO.read(new ByteArrayInputStream(barcodeBytes));

        // Crear documento Word
        XWPFDocument doc = new XWPFDocument();

        // --- 1. Agregar el Título ---
        XWPFParagraph titleParagraph = doc.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(24);
        titleRun.setText("Código de barras");

        // --- 2. Insertar el código de barras como imagen ---
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(barcodeImage, "png", baos);
        baos.flush();
        byte[] barcodeImageBytes = baos.toByteArray();
        baos.close();

        // Insertar la imagen en el Word
        XWPFParagraph imageParagraph = doc.createParagraph();
        imageParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun imageRun = imageParagraph.createRun();
        InputStream barcodeStream = new ByteArrayInputStream(barcodeImageBytes);
        imageRun.addPicture(barcodeStream, XWPFDocument.PICTURE_TYPE_PNG, "barcode.png", Units.toEMU(150), Units.toEMU(80));
        barcodeStream.close();

        // --- 3. Crear la tabla de 3 filas x 5 columnas ---
        XWPFTable table = doc.createTable(3, 5); // 3 filas, 5 columnas

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                table.getRow(row).getCell(col).setText("Fila " + (row + 1) + " Columna " + (col + 1));
            }
        }

        // Guardar el Word en un array de bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doc.write(outputStream);
        doc.close();

        return outputStream.toByteArray(); // Retornar el archivo DOCX como byte[]
    }
}



