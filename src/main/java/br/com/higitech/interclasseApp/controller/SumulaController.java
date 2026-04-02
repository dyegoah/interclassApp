package br.com.higitech.interclasseApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.dto.SumulaFinalizadaDTO;
import br.com.higitech.interclasseApp.service.SumulaService;

@RestController
@RequestMapping("/api/sumulas")
@CrossOrigin(origins = "*")
public class SumulaController {

    private final SumulaService sumulaService;

    // CONSTRUTOR MANUAL
    public SumulaController(SumulaService sumulaService) {
        this.sumulaService = sumulaService;
    }

    @PostMapping("/{jogoId}/finalizar")
    public ResponseEntity<String> finalizarPartida(
            @PathVariable Long jogoId, 
            @RequestBody SumulaFinalizadaDTO sumulaDTO) {
        
        sumulaService.processarSumula(jogoId, sumulaDTO);
        return ResponseEntity.ok("Súmula processada e gravada no PostgreSQL com sucesso!");
    }
}