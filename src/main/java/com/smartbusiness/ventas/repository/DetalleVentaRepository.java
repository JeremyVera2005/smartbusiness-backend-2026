package com.smartbusiness.ventas.repository;

import com.smartbusiness.ventas.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    // Usado por el motor de IA (fase A/B) para calcular rotación y demanda por producto
    @Query("SELECT d FROM DetalleVenta d WHERE d.venta.idNegocio = :idNegocio " +
           "AND d.idProducto = :idProducto AND d.venta.fechaVenta >= :desde")
    List<DetalleVenta> findPorProductoDesde(@Param("idNegocio") Long idNegocio,
                                              @Param("idProducto") Long idProducto,
                                              @Param("desde") LocalDateTime desde);
}
