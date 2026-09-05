package com.smartbusiness.inventario.repository;

import com.smartbusiness.inventario.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByIdNegocioAndIdProductoOrderByFechaMovimientoDesc(Long idNegocio, Long idProducto);

    @Query("SELECT m FROM MovimientoInventario m WHERE m.idNegocio = :idNegocio " +
           "AND m.idProducto = :idProducto AND m.fechaMovimiento >= :desde")
    List<MovimientoInventario> findMovimientosDesde(@Param("idNegocio") Long idNegocio,
                                                       @Param("idProducto") Long idProducto,
                                                       @Param("desde") LocalDateTime desde);
}
