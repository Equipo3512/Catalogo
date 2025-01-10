package com.Demo.catalogo.DTO;

import com.Demo.catalogo.model.Pelicula;

public class PeliculaDTO {

    private Long id;
    private String nombre;
    private Integer anio;
    private String genero;
    private Integer duracion;
    private String proveedorNombre; // Nombre del proveedor

    // Constructor vacío
    public PeliculaDTO() {}

    // Constructor con parámetros
    public PeliculaDTO(Long id, String nombre, Integer duracion,
                       String genero, Integer anio, String proveedorNombre) {
        this.id = id;
        this.nombre = nombre;
        this.duracion = duracion;
        this.genero = genero;
        this.anio = anio;
        this.proveedorNombre = proveedorNombre;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    // Método para convertir el DTO a la entidad
    public Pelicula toEntity() {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(this.id);
        pelicula.setNombre(this.nombre);
        pelicula.setAnio(this.anio);
        pelicula.setGenero(this.genero);
        pelicula.setDuracion(this.duracion);

        // El proveedor debe ser manejado en otra capa, si es necesario
        return pelicula;
    }

    // Método para convertir la entidad a DTO
    public static PeliculaDTO fromEntity(Pelicula pelicula) {
        return new PeliculaDTO(
                pelicula.getId(),
                pelicula.getNombre(),
                pelicula.getDuracion(),
                pelicula.getGenero(),
                pelicula.getAnio(),
                pelicula.getProveedor() != null ? pelicula.getProveedor().getNombre() : null
        );
    }
}
