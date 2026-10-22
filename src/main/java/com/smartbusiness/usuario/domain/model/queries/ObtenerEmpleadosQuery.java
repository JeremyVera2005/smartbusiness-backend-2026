package com.smartbusiness.usuario.domain.model.queries;

/** Query: personal asignado a un negocio (solo para su dueño). */
public record ObtenerEmpleadosQuery(Long idNegocio, Long idUsuarioSolicitante) {
}
