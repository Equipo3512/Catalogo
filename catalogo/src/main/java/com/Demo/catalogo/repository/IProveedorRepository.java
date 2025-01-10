package com.Demo.catalogo.repository;

import com.Demo.catalogo.model.Pelicula;
import com.Demo.catalogo.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProveedorRepository extends JpaRepository<Proveedor, Long> {
}