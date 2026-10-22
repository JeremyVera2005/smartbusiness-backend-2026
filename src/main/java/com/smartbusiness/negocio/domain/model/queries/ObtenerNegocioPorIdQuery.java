package com.smartbusiness.negocio.domain.model.queries;

/** Query: detalle de un negocio al que el usuario tiene acceso. */
public record ObtenerNegocioPorIdQuery(Long idNegocio, Long idUsuario) {
}
