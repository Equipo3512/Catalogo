package com.Demo.catalogo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;


@Service
public class QrCodeService {

    // Leer valores desde application.properties
    @Value("${app.qr.url}")
    private String url;
    @Value("${app.user.name}")
    private String name;
    @Value("${app.user.email}")
    private String email;
    @Value("${app.user.organization}")
    private String organization;

    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;

    // Genera un código QR en formato byte array.-----------------------------------------------------------------------
    public byte[] generateQrCode() throws Exception {
        // Crear la cadena de datos a incluir en el QR
        String qrContent = String.format("URL: %s\nNombre: %s\nCorreo: %s\nOrganización: %s",
                url, name, email, organization);
        // Generar la matriz de bits del código QR
        BitMatrix matrix = new MultiFormatWriter()
                .encode(qrContent, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
        // Convertir la matriz en una imagen
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        // Convertir la imagen en un array de bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", baos);
        return baos.toByteArray();
    }

    // Genera un PDF que contiene el código QR.-------------------------------------------------------------------------
    public byte[] generatePdf() throws Exception {
        // Genera el código QR y lo obtiene en formato de bytes
        byte[] qrImageBytes = generateQrCode();

        // Crea un flujo de salida en memoria para almacenar el PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Configura el escritor del PDF utilizando el flujo de salida
        PdfWriter writer = new PdfWriter(outputStream);
        // Crea un documento PDF en memoria usando el escritor
        PdfDocument pdf = new PdfDocument(writer);
        // Crea un objeto de documento de iText para manejar el contenido del PDF
        Document document = new Document(pdf);

        // Convierte los bytes de la imagen del código QR en un objeto de imagen utilizable en el PDF
        ImageData imageData = ImageDataFactory.create(qrImageBytes);
        Image qrImage = new Image(imageData);

        // Agrega la imagen QR al documento PDF
        document.add(qrImage);
        // Cierra el documento para asegurarse de que los datos se escriban correctamente
        document.close();

        // Convierte el contenido del PDF en un arreglo de bytes y lo devuelve
        return outputStream.toByteArray();
    }


}
