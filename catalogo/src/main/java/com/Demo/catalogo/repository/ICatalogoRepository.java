package com.Demo.catalogo.repository;

import com.Demo.catalogo.model.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Indica que esta interfaz es un componente de acceso a datos (DAO).
public interface ICatalogoRepository extends JpaRepository<Pelicula, Long> {

    /*
     * JPQL Generado:
     * SELECT p FROM Pelicula p JOIN p.proveedor pr
     * WHERE pr.nombre = :proveedorNombre
     * ORDER BY p.nombre ASC
     * @return una página de películas relacionadas con el proveedor, ordenadas alfabéticamente
     */
    Page<Pelicula> findByProveedor_NombreOrderByNombreAsc(String proveedorNombre, Pageable pageable);

    /*
     * JPQL Generado:
     * SELECT p FROM Pelicula p JOIN p.proveedor pr
     * WHERE pr.nombre LIKE %:proveedorNombre%
     * ORDER BY p.nombre ASC
     * @return una página de películas cuyos proveedores coincidan parcialmente, ordenadas alfabéticamente
     */
    Page<Pelicula> findByProveedor_NombreContainingOrderByNombreAsc(String proveedorNombre, Pageable pageable);

    /*
     * JPQL Generado:
     * SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END
     * FROM Pelicula p JOIN p.proveedor pr
     * WHERE p.nombre = :nombre AND pr.id = :proveedorId
     * @return true si la película ya existe, false en caso contrario
     */
    boolean existsByNombreAndProveedor_Id(String nombre, Long proveedorId);


}
