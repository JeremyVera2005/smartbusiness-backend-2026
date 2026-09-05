package com.smartbusiness.ventas.repository;

import com.smartbusiness.ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByIdNegocioOrderByFechaVentaDesc(Long idNegocio);

    @Query("SELECT v FROM Venta v WHERE v.idNegocio = :idNegocio AND v.fechaVenta BETWEEN :desde AND :hasta")
    List<Venta> findEntreFechas(@Param("idNegocio") Long idNegocio,
                                 @Param("desde") LocalDateTime desde,
                                 @Param("hasta") LocalDateTime hasta);
}
