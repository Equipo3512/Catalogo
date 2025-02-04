package com.Demo.catalogo.service;

import com.Demo.catalogo.DTO.PeliculaActualizarDTO;
import com.Demo.catalogo.DTO.PeliculaDTO;
import com.Demo.catalogo.model.Pelicula;
import com.Demo.catalogo.model.Proveedor;
import com.Demo.catalogo.repository.ICatalogoRepository;
import com.Demo.catalogo.repository.IProveedorRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogoServicio implements ICatalogoServicio {

    private final ICatalogoRepository catalogoRepositorio;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private IProveedorRepository proveedorRepositorio;  // Inyección del repositorio de Proveedor

    @Autowired
    public CatalogoServicio(ICatalogoRepository catalogoRepositorio) {
        this.catalogoRepositorio = catalogoRepositorio;
    }
    @Override
    public Page<PeliculaDTO> obtenerPeliculasPorProveedor(String proveedor, int pagina, int tamanoPagina) {
        try {
            // Paginación y ordenación por nombre
            Pageable pageable = PageRequest.of(pagina, tamanoPagina, Sort.by(Sort.Order.asc("nombre")));
            // Obtener las películas del proveedor con paginación
            Page<Pelicula> peliculasPage = catalogoRepositorio.findByProveedor_NombreOrderByNombreAsc(proveedor, pageable);
            if (peliculasPage.isEmpty()) {
                // Si no se encuentran películas exactas, buscamos por nombre similar
                peliculasPage = catalogoRepositorio.findByProveedor_NombreContainingOrderByNombreAsc(proveedor, pageable);
                System.out.println("Se devolvieron las películas del proveedor más parecido.");
            } else {
                System.out.println("Se encontró el proveedor exacto.");
            }

            // Convertir la página de películas a lista de DTOs
            return peliculasPage.map(PeliculaDTO::fromEntity);
        } catch (Exception e) {
            System.err.println("Error al obtener las películas: " + e.getMessage());
            throw new RuntimeException("Error en la consulta de películas.");
        }
    }

    @Override
    public String actualizarPelicula(Long id, PeliculaActualizarDTO peliculaDTO) {
        // Verificar si la película existe
        if (catalogoRepositorio.existsById(id)) {
            String MSG = "";
            // Recuperar la película existente de la base de datos
            Pelicula peliculaExistente = catalogoRepositorio.findById(id).orElse(null);
            if (peliculaExistente != null) {
                // Compara cada campo y actualiza si el campo no está vacío en el DTO
                if (peliculaDTO.getNombre() != null && !peliculaDTO.getNombre().trim().isEmpty()) {
                    peliculaExistente.setNombre(peliculaDTO.getNombre());
                }
                if (peliculaDTO.getAnio() != null) {
                    peliculaExistente.setAnio(peliculaDTO.getAnio());
                }
                if (peliculaDTO.getGenero() != null && !peliculaDTO.getGenero().trim().isEmpty()) {
                    peliculaExistente.setGenero(peliculaDTO.getGenero());
                }
                if (peliculaDTO.getDuracion() != null) {
                    peliculaExistente.setDuracion(peliculaDTO.getDuracion());
                }
                if (peliculaDTO.getProveedor() != null && peliculaDTO.getProveedor().getId() != null) {
                    Proveedor proveedor = proveedorRepositorio.findById(peliculaDTO.getProveedor().getId()).orElse(null);
                    if (proveedor != null) {
                        peliculaExistente.setProveedor(proveedor);
                    }else {
                        // Si el proveedor no se encuentra, imprime un mensaje en consola
                        MSG  =" el ID del proveedor proporcionado no fue encontrado en la base de datos, no se realizará el cambio";
                    }
                }
                // Guardar la película actualizada
                catalogoRepositorio.save(peliculaExistente);
                return "Película actualizada con éxito"+MSG+".";
            } else {
                return "Error al recuperar la película.";
            }
        }
        return "Película no encontrada.";
    }

    @Override
    public String eliminarPelicula(Long id) {
        if (catalogoRepositorio.existsById(id)) {
            catalogoRepositorio.deleteById(id);
            return "Película eliminada con éxito.";
        }
        return "Película no encontrada.";
    }

    @Override
    public String crearPelicula(Pelicula nuevaPelicula) {
        boolean existe = catalogoRepositorio.existsByNombreAndProveedor_Id(
                nuevaPelicula.getNombre(), nuevaPelicula.getProveedor().getId()
        );
        if (existe) {
            return "Película duplicada: ya existe un registro con el mismo nombre y proveedor.";
        }
        catalogoRepositorio.save(nuevaPelicula);
        return "Película creada con éxito.";
    }

    @Override
    public Page<PeliculaDTO> obtenerTodasLasPeliculas(int pagina, int tamanoPagina) {
        // Crear el objeto Pageable para la paginación y ordenar por año (de más reciente a más antigua)
        Pageable pageable = PageRequest.of(pagina, tamanoPagina, Sort.by(Sort.Order.desc("anio")));

        // Crear el timer para medir el tiempo de ejecución
        Timer timer = Timer.builder("catalogo.obtener_todas_las_peliculas")
                .description("Tiempo de respuesta para obtener todas las películas")
                .register(meterRegistry);  // Asegúrate de inyectar MeterRegistry

        // Medir el tiempo de ejecución de la consulta
        return timer.record(() -> {
            // Obtener las películas de la base de datos con paginación
            Page<Pelicula> peliculasPage = catalogoRepositorio.findAll(pageable);

            // Convertir la página de entidades Pelicula a una página de DTOs
            return peliculasPage.map(PeliculaDTO::fromEntity);
        });
    }



}

