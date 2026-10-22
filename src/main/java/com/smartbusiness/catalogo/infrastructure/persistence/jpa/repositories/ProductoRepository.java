package com.smartbusiness.catalogo.infrastructure.persistence.jpa.repositories;

import com.smartbusiness.catalogo.domain.model.aggregates.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByIdNegocioAndActivoTrue(Long idNegocio);

    List<Producto> findByIdNegocioAndCategoria(Long idNegocio, String categoria);

    @Query("SELECT p FROM Producto p WHERE p.idNegocio = :idNegocio AND p.stockActual <= p.stockMinimo")
    List<Producto> findConStockBajo(@Param("idNegocio") Long idNegocio);

    List<Producto> findByIdNegocioAndNombreContainingIgnoreCase(Long idNegocio, String nombre);
}
