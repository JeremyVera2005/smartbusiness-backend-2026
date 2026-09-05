package com.smartbusiness.ia.controller;

import com.smartbusiness.ia.dto.AlertaDemanda;
import com.smartbusiness.ia.dto.AlertaStock;
import com.smartbusiness.ia.service.AsistenteIAService;
import com.smartbusiness.ia.service.DeteccionAnomaliaService;
import com.smartbusiness.ia.service.RecomendacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ia")
public class RecomendacionController {

    @Autowired
    private RecomendacionService recomendacionService;

    @Autowired
    private DeteccionAnomaliaService deteccionAnomaliaService;

    @Autowired
    private AsistenteIAService asistenteIAService;

    @GetMapping("/alertas/stock")
    public ResponseEntity<List<AlertaStock>> alertasStock() {
        return ResponseEntity.ok(recomendacionService.detectarRiesgoQuiebre());
    }

    @GetMapping("/alertas/demanda")
    public ResponseEntity<List<AlertaDemanda>> alertasDemanda() {
        return ResponseEntity.ok(deteccionAnomaliaService.detectarAnomalias());
    }

    @PostMapping("/asistente")
    public ResponseEntity<Map<String, String>> preguntarAsistente(@RequestBody Map<String, String> body) {
        String respuesta = asistenteIAService.responderPregunta(body.getOrDefault("pregunta", ""));
        return ResponseEntity.ok(Map.of("respuesta", respuesta));
    }
}
