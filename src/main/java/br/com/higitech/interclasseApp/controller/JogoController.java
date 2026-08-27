package br.com.higitech.interclasseApp.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @PostMapping("/calendario")
    public ResponseEntity<Void> salvarCalendarioOficial(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal Professor professorLogado) {
        jogoService.salvarCalendario(payload, professorLogado);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 🔄 ROTA QUE A TABELAS.HTML USA PARA EXIBIR OS JOGOS POR GÊNERO
    @GetMapping("/lote/{genero}")
    public ResponseEntity<List<Jogo>> getJogosDoLote(@PathVariable String genero, @AuthenticationPrincipal Professor professorLogado) {
        List<Jogo> todos = jogoService.buscarJogosPorProfessor(professorLogado);
        
        // 🔥 FILTRO BLINDADO: Puxa só as chaves correspondentes para não misturar Masculino com Feminino
        List<Jogo> filtrados = todos.stream().filter(j -> {
            try {
                String gen = (String) j.getClass().getMethod("getGenero").invoke(j);
                return genero.equalsIgnoreCase(gen) || "geral".equalsIgnoreCase(gen);
            } catch (Exception e) {
                return true; // Se o modelo for diferente, envia tudo por precaução
            }
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(filtrados);
    }

    @GetMapping
    public ResponseEntity<List<Jogo>> getAllJogos(@AuthenticationPrincipal Professor professorLogado) {
        return ResponseEntity.ok(jogoService.buscarJogosPorProfessor(professorLogado));
    }
    
 // 🌐 ROTA PÚBLICA: Permite que alunos vejam os jogos do professor sem token de login
    @GetMapping("/public/{professorId}/lote/{genero}")
    public ResponseEntity<List<Jogo>> getJogosPublicoLote(@PathVariable Long professorId, @PathVariable String genero) {
        List<Jogo> todos = jogoService.buscarJogosPorProfessorId(professorId);
        
        List<Jogo> filtrados = todos.stream().filter(j -> {
            try {
                String gen = (String) j.getClass().getMethod("getGenero").invoke(j);
                return genero.equalsIgnoreCase(gen) || "geral".equalsIgnoreCase(gen);
            } catch (Exception e) {
                return true;
            }
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(filtrados);
    }
}