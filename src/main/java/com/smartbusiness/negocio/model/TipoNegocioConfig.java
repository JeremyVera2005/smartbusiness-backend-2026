package com.smartbusiness.negocio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_negocio_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoNegocioConfig {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_negocio", length = 30)
    private TipoNegocio tipoNegocio;

    // JSON crudo, ej: [{"nombre":"fechaVencimiento","tipo":"date","label":"Fecha de vencimiento"}]
    @Lob
    @Column(name = "campos_producto_extra", columnDefinition = "JSON")
    private String camposProductoExtra;

    // JSON crudo, ej: {"proveedores":true,"vencimientos":true,"lotes":true}
    @Lob
    @Column(name = "modulos_activos", columnDefinition = "JSON")
    private String modulosActivos;
}
