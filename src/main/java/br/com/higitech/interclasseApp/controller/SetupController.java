package br.com.higitech.interclasseApp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.service.SetupService;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    @Autowired
    private SetupService setupService;

    @PostMapping
    public ResponseEntity<Void> salvarConfiguracoesIniciais(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal Professor professorLogado) {
        setupService.processarLote(payload, professorLogado);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/reset")
    public ResponseEntity<?> zerarTemporada(@AuthenticationPrincipal Professor professorLogado) {
        try {
            setupService.resetarTudo(professorLogado);
            return ResponseEntity.ok().body("Temporada zerada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao limpar a base de dados: " + e.getMessage());
        }
    }
}