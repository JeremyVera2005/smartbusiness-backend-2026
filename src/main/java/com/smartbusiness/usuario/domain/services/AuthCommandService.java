package com.smartbusiness.usuario.domain.services;

import com.smartbusiness.usuario.domain.model.commands.CambiarPasswordCommand;
import com.smartbusiness.usuario.domain.model.commands.RegistrarDuenoCommand;
import com.smartbusiness.usuario.domain.model.readmodels.SesionUsuario;

/** Operaciones de cuenta que modifican datos (lado Command de CQRS). */
public interface AuthCommandService {

    SesionUsuario handle(RegistrarDuenoCommand command);

    void handle(CambiarPasswordCommand command);
}
