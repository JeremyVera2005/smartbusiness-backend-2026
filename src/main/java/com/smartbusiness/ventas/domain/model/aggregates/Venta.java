package com.smartbusiness.ventas.domain.model.aggregates;

import com.smartbusiness.ventas.domain.model.entities.DetalleVenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @Column(name = "id_negocio", nullable = false)
    private Long idNegocio;

    @Column(name = "id_usuario_vendedor")
    private Long idUsuarioVendedor;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    @Builder.Default
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.fechaVenta = LocalDateTime.now();
    }
}
