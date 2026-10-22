package com.smartbusiness.negocio.domain.model.aggregates;

import com.smartbusiness.negocio.domain.model.valueobjects.TamanoNegocio;
import com.smartbusiness.negocio.domain.model.valueobjects.TipoNegocio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "negocio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_negocio")
    private Long idNegocio;

    @Column(name = "id_usuario_propietario", nullable = false)
    private Long idUsuarioPropietario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_negocio", nullable = false, length = 30)
    private TipoNegocio tipoNegocio;

    @Column(length = 150)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TamanoNegocio tamano;

    @Column(name = "horario_apertura")
    private LocalTime horarioApertura;

    @Column(name = "horario_cierre")
    private LocalTime horarioCierre;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
