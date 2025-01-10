package com.Demo.catalogo.controller;

import com.Demo.catalogo.DTO.PeliculaActualizarDTO;
import com.Demo.catalogo.DTO.PeliculaDTO;
import com.Demo.catalogo.model.Pelicula;
import com.Demo.catalogo.service.ICatalogoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class CatalogoControlador {

    private static final Logger logger = LoggerFactory.getLogger(CatalogoControlador.class);

    private final ICatalogoServicio catalogoService;

    @Autowired
    public CatalogoControlador(ICatalogoServicio catalogoService) {
        this.catalogoService = catalogoService;
    }

    @PutMapping("/peliculas/{id}")
    @Operation(
            summary = "Actualizar una película",
            description = "Actualiza los datos de una película existente identificada por su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Película actualizada con éxito"),
                    @ApiResponse(responseCode = "404", description = "Película no encontrada"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<String> ActualizarPelicula(
            @Parameter(description = "ID de la película a actualizar")
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la película")
            @RequestBody PeliculaActualizarDTO peliculaActualizar) {
        logger.info("Iniciando actualización para la película con ID: {}", id);
        try {
            String resultado = catalogoService.actualizarPelicula(id, peliculaActualizar);
            if ("Película no encontrada.".equals(resultado)) {
                logger.warn("Película con ID {} no encontrada", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
            }
            if (resultado.equals("Película actualizada con éxito el ID del proveedor " +
                    "proporcionado no fue encontrado en la base de datos, no se realizará el cambio.")) {
                logger.warn("Película actualizada con éxito: {} pero no se modifico el id del proveedor(el proporcionado fue invalido)", id);
                return ResponseEntity.ok(resultado);
            }
            logger.info("Película actualizada con éxito: {}", id);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            logger.error("Error al actualizar la película con ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la película: " + e.getMessage());
        }
    }

    @DeleteMapping("/peliculas/{id}")
    @Operation(
            summary = "Eliminar una película",
            description = "Elimina una película existente identificada por su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Película eliminada con éxito"),
                    @ApiResponse(responseCode = "404", description = "Película no encontrada"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<String> EliminarPelicula(
            @Parameter(description = "ID de la película a eliminar")
            @PathVariable Long id) {
        logger.info("Iniciando eliminación de la película con ID: {}", id);
        try {
            String resultado = catalogoService.eliminarPelicula(id);
            if ("Película no encontrada.".equals(resultado)) {
                logger.warn("Película con ID {} no encontrada para eliminación", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
            }
            logger.info("Película eliminada con éxito: {}", id);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            logger.error("Error al eliminar la película con ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la película: " + e.getMessage());
        }
    }

    @PostMapping("/peliculas")
    @Operation(
            summary = "Crear una nueva película",
            description = "Crea un nuevo registro de película en el catálogo.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Película creada con éxito"),
                    @ApiResponse(responseCode = "409", description = "Película duplicada"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<String> CrearPelicula(
            @Parameter(description = "Datos de la nueva película a crear")
            @RequestBody Pelicula nuevaPelicula) {
        logger.info("Iniciando creación de una nueva película");
        try {
            String respuesta = catalogoService.crearPelicula(nuevaPelicula);
            if (respuesta.equals("Película duplicada: ya existe un registro con el mismo nombre y proveedor.")) {
                logger.warn("Intento de crear película duplicada: {}", nuevaPelicula);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
            }
            logger.info("Película creada con éxito");
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (Exception e) {
            logger.error("Error al crear la película: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al crear la película: " + e.getMessage());
        }
    }

    @GetMapping("/peliculas/{proveedor}")
    @Operation(
            summary = "Obtener todas las películas de un proveedor ordenadas por nombre",
            description = "Devuelve una lista de todas las películas de un proveedor en el catálogo ordenadas por nombre de la A a la Z.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Películas encontradas"),
                    @ApiResponse(responseCode = "404", description = "No se encontraron películas"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<Page<PeliculaDTO>> obtenerTodasOrdenadasPorNombre(
            @PathVariable String proveedor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {

        logger.info("Iniciando obtención de películas ordenadas por nombre con paginación");

        try {
            // Crear el Pageable con los parámetros de paginación
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());

            // Llamada al servicio pasando el proveedor, la página y el tamaño
            Page<PeliculaDTO> peliculas = catalogoService.obtenerPeliculasPorProveedor(proveedor, page, size);

            // Comprobar si se encontraron películas
            if (peliculas.isEmpty()) {
                logger.warn("No se encontraron películas");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(peliculas);
            }

            logger.info("Se encontraron {} páginas de películas en la base de datos", peliculas.getTotalPages());
            return ResponseEntity.ok(peliculas);
        } catch (Exception e) {
            logger.error("Error al obtener las películas ordenadas por nombre: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/peliculas")
    @Operation(
            summary = "Obtener todas las películas ordenadas por año",
            description = "Devuelve una lista de todas las películas en el catálogo ordenadas por año en orden descendente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Películas encontradas"),
                    @ApiResponse(responseCode = "404", description = "No se encontraron películas"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<Page<PeliculaDTO>> obtenerTodasOrdenadasPorAnio(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        logger.info("Iniciando obtención de películas ordenadas por año con paginación");

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("anio").descending());
            Page<PeliculaDTO> peliculas = catalogoService.obtenerTodasLasPeliculas(page, size);

            if (peliculas.isEmpty()) {
                logger.warn("No se encontraron películas");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(peliculas);
            }

            logger.info("Se encontraron {} páginas de películas en la base de datos", peliculas.getTotalPages());
            return ResponseEntity.ok(peliculas);
        } catch (Exception e) {
            logger.error("Error al obtener las películas ordenadas por año: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
