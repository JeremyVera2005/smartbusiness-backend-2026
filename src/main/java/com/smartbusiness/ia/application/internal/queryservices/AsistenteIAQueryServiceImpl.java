package com.smartbusiness.ia.application.internal.queryservices;

import com.smartbusiness.ia.domain.model.queries.ConsultarAsistenteQuery;
import com.smartbusiness.ia.domain.model.queries.ObtenerAlertasDemandaQuery;
import com.smartbusiness.ia.domain.model.queries.ObtenerAlertasStockQuery;
import com.smartbusiness.ia.domain.model.valueobjects.AlertaDemanda;
import com.smartbusiness.ia.domain.model.valueobjects.AlertaStock;
import com.smartbusiness.ia.domain.services.AsistenteIAQueryService;
import com.smartbusiness.ia.domain.services.DeteccionAnomaliaQueryService;
import com.smartbusiness.ia.domain.services.RecomendacionQueryService;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.infrastructure.tenant.TenantContext;
import com.smartbusiness.ventas.domain.model.aggregates.Venta;
import com.smartbusiness.ventas.infrastructure.persistence.jpa.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Asistente del negocio. Por ahora responde con reglas: identifica el tema de
 * la pregunta (reposición, ventas o demanda) y arma la respuesta en lenguaje
 * claro con las alertas y las ventas registradas.
 *
 * TODO (fase C): reemplazar handle(ConsultarAsistenteQuery) por una llamada a un modelo de
 * lenguaje usando construirContextoNegocio() + la pregunta del usuario.
 */
@Service
@Transactional(readOnly = true)
public class AsistenteIAQueryServiceImpl implements AsistenteIAQueryService {

    private final RecomendacionQueryService recomendacionQueryService;

    private final DeteccionAnomaliaQueryService deteccionAnomaliaQueryService;

    private final VentaRepository ventaRepository;

    public AsistenteIAQueryServiceImpl(RecomendacionQueryService recomendacionQueryService,
            DeteccionAnomaliaQueryService deteccionAnomaliaQueryService,
            VentaRepository ventaRepository) {
        this.recomendacionQueryService = recomendacionQueryService;
        this.deteccionAnomaliaQueryService = deteccionAnomaliaQueryService;
        this.ventaRepository = ventaRepository;
    }

    private enum Tema { REPOSICION, VENTAS, DEMANDA, GENERAL }

    public String construirContextoNegocio() {
        List<AlertaStock> alertasStock = recomendacionQueryService.handle(new ObtenerAlertasStockQuery());
        List<AlertaDemanda> alertasDemanda = deteccionAnomaliaQueryService.handle(new ObtenerAlertasDemandaQuery());

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

    @Override
    public String handle(ConsultarAsistenteQuery query) {
        String preguntaUsuario = query.pregunta();
        return switch (detectarTema(preguntaUsuario)) {
            case REPOSICION -> responderReposicion();
            case VENTAS -> responderVentas();
            case DEMANDA -> responderDemanda();
            case GENERAL -> responderReposicion() + "\n\n" + responderVentas();
        };
    }

    private Tema detectarTema(String pregunta) {
        String q = Normalizer.normalize(pregunta == null ? "" : pregunta, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        if (q.matches(".*(agot|reabast|repon|repos|quiebre|stock|comprar|falta).*")) {
            return Tema.REPOSICION;
        }
        if (q.matches(".*(demanda|rota|tendencia|menos|mas vendid|vende mas|venden mas|bajaron|subieron).*")) {
            return Tema.DEMANDA;
        }
        if (q.matches(".*(venta|vend|ingres|gan|semana|caja|cobr).*")) {
            return Tema.VENTAS;
        }
        return Tema.GENERAL;
    }

    private String responderReposicion() {
        List<AlertaStock> alertas = recomendacionQueryService.handle(new ObtenerAlertasStockQuery());
        if (alertas.isEmpty()) {
            return "Por ahora ningún producto está en riesgo de agotarse: con el ritmo de ventas de las últimas dos semanas, todos tienen stock para más de 5 días.";
        }
        StringBuilder r = new StringBuilder(alertas.size() == 1
                ? "Hay 1 producto que conviene reponer pronto:\n"
                : "Hay " + alertas.size() + " productos que conviene reponer pronto:\n");
        alertas.stream()
                .sorted((a, b) -> Double.compare(a.getDiasRestantesEstimados(), b.getDiasRestantesEstimados()))
                .forEach(a -> r.append(String.format(Locale.ROOT, "• %s: quedan %d unidades, alcanzan para unos %.1f días%s.%n",
                        a.getNombreProducto(), a.getStockActual(), a.getDiasRestantesEstimados(),
                        "CRITICO".equals(a.getNivel()) ? " (urgente)" : "")));
        r.append("Los marcados como urgentes podrían agotarse en menos de 2 días.");
        return r.toString();
    }

    private String responderVentas() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        LocalDateTime ahora = LocalDateTime.now();
        List<Venta> actual = ventaRepository.findEntreFechas(idNegocio, ahora.minusDays(7), ahora);
        List<Venta> anterior = ventaRepository.findEntreFechas(idNegocio, ahora.minusDays(14), ahora.minusDays(7));
        BigDecimal totalActual = sumar(actual);
        BigDecimal totalAnterior = sumar(anterior);

        StringBuilder r = new StringBuilder(String.format(Locale.ROOT,
                "En los últimos 7 días registraste %d ventas por S/ %.2f.", actual.size(), totalActual));
        if (totalAnterior.signum() > 0) {
            double variacion = totalActual.subtract(totalAnterior).doubleValue() / totalAnterior.doubleValue() * 100;
            r.append(String.format(Locale.ROOT, " La semana anterior fueron %d ventas por S/ %.2f, es decir, %s %.0f %%.",
                    anterior.size(), totalAnterior, variacion >= 0 ? "un aumento de" : "una disminución de", Math.abs(variacion)));
        }
        return r.toString();
    }

    private String responderDemanda() {
        List<AlertaDemanda> alertas = deteccionAnomaliaQueryService.handle(new ObtenerAlertasDemandaQuery());
        if (alertas.isEmpty()) {
            return "No hay cambios fuertes de demanda: ningún producto varió 30 % o más entre esta semana y la anterior.";
        }
        StringBuilder r = new StringBuilder("Estos productos cambiaron su demanda respecto de la semana anterior:\n");
        alertas.forEach(a -> r.append(String.format(Locale.ROOT, "• %s: %s, de %d a %d unidades (%+.0f %%).%n",
                a.getNombreProducto(), "ALTA_DEMANDA".equals(a.getTipo()) ? "se vende más" : "se vende menos",
                a.getUnidadesSemanaAnterior(), a.getUnidadesSemanaActual(), a.getVariacionPorcentual())));
        r.append("Revisa si conviene ajustar las compras de estos productos.");
        return r.toString();
    }

    private BigDecimal sumar(List<Venta> ventas) {
        return ventas.stream().map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
