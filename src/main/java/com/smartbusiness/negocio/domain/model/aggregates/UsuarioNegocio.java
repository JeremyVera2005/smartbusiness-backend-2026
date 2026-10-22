package com.smartbusiness.negocio.domain.model.aggregates;

import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "usuario_negocio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioNegocio {

    @EmbeddedId
    private UsuarioNegocioId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolNegocio rol;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioNegocioId implements Serializable {

        @Column(name = "id_usuario")
        private Long idUsuario;

        @Column(name = "id_negocio")
        private Long idNegocio;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UsuarioNegocioId)) return false;
            UsuarioNegocioId that = (UsuarioNegocioId) o;
            return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(idNegocio, that.idNegocio);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idUsuario, idNegocio);
        }
    }
}
