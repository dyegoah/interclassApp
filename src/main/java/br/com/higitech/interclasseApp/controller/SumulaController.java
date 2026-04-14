package br.com.higitech.interclasseApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.dto.SumulaFinalizadaDTO;
import br.com.higitech.interclasseApp.service.SumulaService;

@RestController
@RequestMapping("/api/sumulas")
public class SumulaController {

    private final SumulaService sumulaService;

    public SumulaController(SumulaService sumulaService) {
        this.sumulaService = sumulaService;
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Void> finalizarSumula(@PathVariable Long id, @RequestBody SumulaFinalizadaDTO dto) {
        sumulaService.processarSumula(id, dto);
        return ResponseEntity.ok().build();
    }
}