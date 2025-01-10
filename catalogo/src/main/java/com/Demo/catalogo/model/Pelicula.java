package com.Demo.catalogo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;  // Importa JsonBackReference
import jakarta.persistence.*; // JPA annotations

@Entity // Marca esta clase como una entidad JPA
@Table(name = "pelicula") // Especifica el nombre de la tabla en la BD
public class Pelicula {

    @Id
    @SequenceGenerator(name = "pelicula_id",
            sequenceName = "pelicula_id",
            initialValue = 15,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pelicula_id")
    private Long id; // Es la clave primaria de la tabla, generada automáticamente.

    private String nombre;  // El nombre de la película.
    private Integer anio;  // El año de estreno de la película.

    @ManyToOne  // Relación de muchos a uno con Proveedor
    @JoinColumn(name = "proveedor_id")  // La clave foránea que relaciona la película con su proveedor.
    private Proveedor proveedor;  // El proveedor que ofrece la película.

    private String genero;
    private Integer duracion;

    // Constructor por defecto
    public Pelicula() {}

    // Constructor con parámetros
    public Pelicula(String nombre, Integer anio, Proveedor proveedor, String genero, Integer duracion) {
        this.nombre = nombre;
        this.anio = anio;
        this.proveedor = proveedor;
        this.genero = genero;
        this.duracion = duracion;
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

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
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
}
