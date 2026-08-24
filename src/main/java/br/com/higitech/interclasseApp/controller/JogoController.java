package br.com.higitech.interclasseApp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.service.JogoService;

@RestController
@RequestMapping("/api/jogos")
public class JogoController {

    @Autowired
    private JogoService jogoService;

    // 🔥 A ROTA QUE FALTAVA (Erro 404 Resolvido): Recebe os jogos da IA e salva no banco!
    @PostMapping("/calendario")
    public ResponseEntity<Void> salvarCalendarioOficial(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal Professor professorLogado) {
        jogoService.salvarCalendario(payload, professorLogado);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 🔄 ROTA QUE O DASHBOARD E O CALENDÁRIO USAM PARA CARREGAR OS JOGOS NA TELA
    @GetMapping("/lote/{genero}")
    public ResponseEntity<List<Jogo>> getJogosDoLote(@PathVariable String genero, @AuthenticationPrincipal Professor professorLogado) {
        List<Jogo> jogos = jogoService.buscarJogosPorProfessor(professorLogado);
        return ResponseEntity.ok(jogos);
    }
}