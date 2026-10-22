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
public class AlertaStock {
    private Long idProducto;
    private String nombreProducto;
    private Integer stockActual;
    private Double promedioVentaDiario;
    private Double diasRestantesEstimados;
    private String nivel; // "CRITICO" (<2 dias), "ALERTA" (<5 dias)
}
