package com.smartbusiness.ia.service;

import com.smartbusiness.ia.dto.AlertaDemanda;
import com.smartbusiness.ia.dto.AlertaStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Fase C: arma el contexto para el asistente conversacional a partir de lo
 * ya calculado en las fases A y B (nunca manda datos crudos de ventas).
 * La llamada real a la API del LLM se agrega aquí cuando el equipo llegue
 * a esa fase (con RestTemplate/WebClient hacia el proveedor que elijan).
 */
@Service
public class AsistenteIAService {

    @Autowired
    private RecomendacionService recomendacionService;

    @Autowired
    private DeteccionAnomaliaService deteccionAnomaliaService;

    public String construirContextoNegocio() {
        List<AlertaStock> alertasStock = recomendacionService.detectarRiesgoQuiebre();
        List<AlertaDemanda> alertasDemanda = deteccionAnomaliaService.detectarAnomalias();

        StringBuilder contexto = new StringBuilder();

        if (!alertasStock.isEmpty()) {
            contexto.append("Alertas de stock:\n");
            contexto.append(alertasStock.stream()
                    .map(a -> String.format("- %s: stock %d, %.1f días restantes estimados (%s)",
                            a.getNombreProducto(), a.getStockActual(), a.getDiasRestantesEstimados(), a.getNivel()))
                    .collect(Collectors.joining("\n")));
            contexto.append("\n");
        }

        if (!alertasDemanda.isEmpty()) {
            contexto.append("Cambios de demanda detectados:\n");
            contexto.append(alertasDemanda.stream()
                    .map(a -> String.format("- %s: variación %.1f%% (%s)",
                            a.getNombreProducto(), a.getVariacionPorcentual(), a.getTipo()))
                    .collect(Collectors.joining("\n")));
        }

        return contexto.length() == 0 ? "Sin alertas relevantes en este momento." : contexto.toString();
    }

    // TODO (fase C real): recibir preguntaUsuario, armar el prompt con
    // construirContextoNegocio() + preguntaUsuario, y llamar a la API del LLM.
    public String responderPregunta(String preguntaUsuario) {
        String contexto = construirContextoNegocio();
        return "Contexto actual del negocio:\n" + contexto +
               "\n\n[Pendiente: conectar con la API del LLM usando este contexto + la pregunta: \"" +
               preguntaUsuario + "\"]";
    }
}
