package com.smartbusiness.ia.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaDemanda {
    private Long idProducto;
    private String nombreProducto;
    private Integer unidadesSemanaActual;
    private Integer unidadesSemanaAnterior;
    private Double variacionPorcentual;
    private String tipo; // "ALTA_DEMANDA" o "CAIDA_VENTAS"
}
