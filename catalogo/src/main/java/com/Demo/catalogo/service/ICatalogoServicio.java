package com.Demo.catalogo.service;

import com.Demo.catalogo.DTO.PeliculaActualizarDTO;
import com.Demo.catalogo.DTO.PeliculaDTO;
import com.Demo.catalogo.model.Pelicula;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICatalogoServicio {

    Page<PeliculaDTO> obtenerPeliculasPorProveedor(String proveedor, int pagina, int tamanoPagina);

    String actualizarPelicula(Long id, PeliculaActualizarDTO pelicula);

    String eliminarPelicula(Long id);

    String crearPelicula(Pelicula nuevaPelicula);

    Page<PeliculaDTO> obtenerTodasLasPeliculas(int pagina, int tamanoPagina);
}
