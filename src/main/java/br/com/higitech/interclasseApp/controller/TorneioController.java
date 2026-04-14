package br.com.higitech.interclasseApp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.dto.TorneioDTO;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.model.Torneio;
import br.com.higitech.interclasseApp.service.TorneioService;

@RestController
@RequestMapping("/api/torneios")
public class TorneioController {

    private final TorneioService torneioService;

    public TorneioController(TorneioService torneioService) {
        this.torneioService = torneioService;
    }

    public static class SetupDTO {
        public String genero;
        public List<EsporteSetup> esportes;
    }
    public static class EsporteSetup {
        public Long id;
        public String formato;
        public Integer qtdTimes;
        public Boolean idaEVolta;
    }

    @PostMapping("/setup")
    public ResponseEntity<?> salvarSetupTorneios(@RequestBody SetupDTO payload, @AuthenticationPrincipal Professor professorLogado) {
        torneioService.salvarSetupCompleto(payload, professorLogado);
        return ResponseEntity.ok().body("{\"mensagem\": \"Torneios configurados com sucesso!\"}");
    }

    @PostMapping
    public ResponseEntity<Torneio> criarTorneio(@RequestBody TorneioDTO dto, @AuthenticationPrincipal Professor professorLogado) {
        Torneio salvo = torneioService.criarTorneio(dto, professorLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Torneio>> listarTorneios(@AuthenticationPrincipal Professor professorLogado) {
        return ResponseEntity.ok(torneioService.listarTodos(professorLogado));
    }
}