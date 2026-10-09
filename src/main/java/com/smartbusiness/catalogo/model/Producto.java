package com.smartbusiness.catalogo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    // Todo producto pertenece a un negocio (multi-tenant)
    @Column(name = "id_negocio", nullable = false)
    private Long idNegocio;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    private String categoria;

    // Campos específicos por rubro (fecha de vencimiento en farmacia,
    // tipo de material en ferretería, etc.) sin crear tablas nuevas por tipo.
    // Sin @Lob a propósito: con MySQL fuerza a Hibernate a mandarlo como binario
    // y rompe columnas JSON ("Cannot create a JSON value from a string with
    // CHARACTER SET 'binary'").
    @Column(name = "atributos_extra", columnDefinition = "JSON")
    private String atributosExtra;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "activo")
    private Boolean activo;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }
}
