package com.smartbusiness.usuario.domain.services;

import com.smartbusiness.usuario.domain.model.commands.CrearEmpleadoCommand;
import com.smartbusiness.usuario.domain.model.commands.QuitarEmpleadoCommand;
import com.smartbusiness.usuario.domain.model.commands.RestablecerPasswordEmpleadoCommand;
import com.smartbusiness.usuario.domain.model.readmodels.EmpleadoAsignado;

/** Gestión de personal que modifica datos (lado Command de CQRS). */
public interface EmpleadoCommandService {

    EmpleadoAsignado handle(CrearEmpleadoCommand command);

    void handle(QuitarEmpleadoCommand command);

    void handle(RestablecerPasswordEmpleadoCommand command);
}
