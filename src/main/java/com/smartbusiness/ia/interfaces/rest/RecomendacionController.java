package com.smartbusiness.ia.interfaces.rest;

import com.smartbusiness.ia.domain.model.queries.ConsultarAsistenteQuery;
import com.smartbusiness.ia.domain.model.queries.ObtenerAlertasDemandaQuery;
import com.smartbusiness.ia.domain.model.queries.ObtenerAlertasStockQuery;
import com.smartbusiness.ia.domain.model.valueobjects.AlertaDemanda;
import com.smartbusiness.ia.domain.model.valueobjects.AlertaStock;
import com.smartbusiness.ia.domain.services.AsistenteIAQueryService;
import com.smartbusiness.ia.domain.services.DeteccionAnomaliaQueryService;
import com.smartbusiness.ia.domain.services.RecomendacionQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ia")
public class RecomendacionController {

    private final RecomendacionQueryService recomendacionQueryService;
    private final DeteccionAnomaliaQueryService deteccionAnomaliaQueryService;
    private final AsistenteIAQueryService asistenteIAQueryService;

    public RecomendacionController(RecomendacionQueryService recomendacionQueryService,
                                   DeteccionAnomaliaQueryService deteccionAnomaliaQueryService,
                                   AsistenteIAQueryService asistenteIAQueryService) {
        this.recomendacionQueryService = recomendacionQueryService;
        this.deteccionAnomaliaQueryService = deteccionAnomaliaQueryService;
        this.asistenteIAQueryService = asistenteIAQueryService;
    }

    @GetMapping("/alertas/stock")
    public ResponseEntity<List<AlertaStock>> alertasStock() {
        return ResponseEntity.ok(recomendacionQueryService.handle(new ObtenerAlertasStockQuery()));
    }

    @GetMapping("/alertas/demanda")
    public ResponseEntity<List<AlertaDemanda>> alertasDemanda() {
        return ResponseEntity.ok(deteccionAnomaliaQueryService.handle(new ObtenerAlertasDemandaQuery()));
    }

    @PostMapping("/asistente")
    public ResponseEntity<Map<String, String>> preguntarAsistente(@RequestBody Map<String, String> body) {
        String respuesta = asistenteIAQueryService.handle(new ConsultarAsistenteQuery(body.getOrDefault("pregunta", "")));
        return ResponseEntity.ok(Map.of("respuesta", respuesta));
    }
}
