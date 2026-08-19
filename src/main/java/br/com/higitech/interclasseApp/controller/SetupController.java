package br.com.higitech.interclasseApp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.dto.LoteSetupDTO;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.service.SetupService;

@RestController
@RequestMapping("/api/setup")
@CrossOrigin(origins = "*") 
public class SetupController {

    private final SetupService setupService;

    // CONSTRUTOR MANUAL - Injeta o serviço de forma segura
    public SetupController(SetupService setupService) {
        this.setupService = setupService;
    }

    // 🟢 ROTA ORIGINAL MANTIDA: Salva a configuração da Arena
    @PostMapping
    public ResponseEntity<Void> salvarConfiguracoesIniciais(@RequestBody LoteSetupDTO setupDTO) {
        setupService.processarLote(setupDTO);
        System.out.println("HigTec API: Setup do Lote " + setupDTO.genero() + " recebido com sucesso!");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 🔴 O BOTÃO VERMELHO: Zera completamente a temporada do professor logado
    @DeleteMapping("/reset")
    public ResponseEntity<?> zerarTemporadaDoProfessor(@AuthenticationPrincipal Professor professorLogado) {
        try {
            // A exclusão é blindada e amarrada exclusivamente ao Professor Logado
            setupService.resetarTudo(professorLogado);
            return ResponseEntity.ok().body("Temporada zerada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao limpar o banco de dados.");
        }
    }
}