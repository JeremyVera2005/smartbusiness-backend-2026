package com.smartbusiness.negocio.application.internal.commandservices;

import com.smartbusiness.negocio.domain.model.aggregates.Negocio;
import com.smartbusiness.negocio.domain.model.aggregates.UsuarioNegocio;
import com.smartbusiness.negocio.domain.model.commands.CrearNegocioCommand;
import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;
import com.smartbusiness.negocio.domain.services.NegocioCommandService;
import com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories.NegocioRepository;
import com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories.UsuarioNegocioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NegocioCommandServiceImpl implements NegocioCommandService {

    private final NegocioRepository negocioRepository;
    private final UsuarioNegocioRepository usuarioNegocioRepository;

    public NegocioCommandServiceImpl(NegocioRepository negocioRepository,
                                     UsuarioNegocioRepository usuarioNegocioRepository) {
        this.negocioRepository = negocioRepository;
        this.usuarioNegocioRepository = usuarioNegocioRepository;
    }

    @Override
    @Transactional
    public Negocio handle(CrearNegocioCommand command) {
        Negocio negocio = Negocio.builder()
                .idUsuarioPropietario(command.idUsuario())
                .nombre(command.nombre())
                .tipoNegocio(command.tipoNegocio())
                .ubicacion(command.ubicacion())
                .tamano(command.tamano())
                .horarioApertura(command.horarioApertura())
                .horarioCierre(command.horarioCierre())
                .build();

        negocio = negocioRepository.save(negocio);

        UsuarioNegocio.UsuarioNegocioId relacionId =
                new UsuarioNegocio.UsuarioNegocioId(command.idUsuario(), negocio.getIdNegocio());

        UsuarioNegocio relacion = UsuarioNegocio.builder()
                .id(relacionId)
                .rol(RolNegocio.DUENO)
                .build();

        usuarioNegocioRepository.save(relacion);

        return negocio;
    }
}
