package br.com.higitech.interclasseApp.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict; // 🔥 INJEÇÃO DE PERFORMANCE
import org.springframework.cache.annotation.Cacheable; // 🔥 INJEÇÃO DE PERFORMANCE
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.JogoRepository;
import br.com.higitech.interclasseApp.service.JogoService;

@RestController
@RequestMapping("/api/jogos")
public class JogoController {

    @Autowired
    private JogoService jogoService;
    
    @Autowired
    private JogoRepository jogoRepository; 

    // 🔥 CACHEEVICT: Quando o professor salvar um calendário novo, o sistema "apaga" a memória antiga e renova o cache!
    @PostMapping("/calendario")
    @CacheEvict(value = "jogosPublicos", allEntries = true)
    public ResponseEntity<Void> salvarCalendarioOficial(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal Professor professorLogado) {
        jogoService.salvarCalendario(payload, professorLogado);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/lote/{genero}")
    public ResponseEntity<List<Jogo>> getJogosDoLote(@PathVariable String genero, @AuthenticationPrincipal Professor professorLogado) {
        List<Jogo> todos = jogoService.buscarJogosPorProfessor(professorLogado);
        
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

    @GetMapping
    public ResponseEntity<List<Jogo>> getAllJogos(@AuthenticationPrincipal Professor professorLogado) {
        return ResponseEntity.ok(jogoService.buscarJogosPorProfessor(professorLogado));
    }
    
    // 🌐 ROTA PÚBLICA BLINDADA: Cache ativo. Quando 1.000 alunos derem F5 para ver quem vai jogar, o banco só será chamado 1 vez!
    @GetMapping("/public/{hashPublico}/lote/{genero}")
    @Cacheable(value = "jogosPublicos", key = "#hashPublico + '-' + #genero")
    public ResponseEntity<List<Jogo>> getJogosPublicoLote(@PathVariable String hashPublico, @PathVariable String genero) {
        List<Jogo> todos = jogoService.buscarJogosPorProfessorHash(hashPublico);
        
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
    
    // 🔥 CORREÇÃO: Rota de exclusão blindada e segura, buscando apenas os jogos do professor logado e deletando em lote.
    @DeleteMapping("/torneio/{genero}/{esporte}")
    public ResponseEntity<?> excluirTorneioEspecifico(
            @PathVariable String genero, 
            @PathVariable String esporte,
            @AuthenticationPrincipal Professor professorLogado) {
        try {
            // 1. Busca os jogos com filtro de segurança Multi-Tenant (apenas os do professor atual)
            List<Jogo> todos = jogoService.buscarJogosPorProfessor(professorLogado);
            
            // 2. Filtra cirurgicamente os jogos que pertencem ao esporte e gênero selecionados
            List<Jogo> paraDeletar = todos.stream().filter(j -> {
                try {
                    String gen = "";
                    try { gen = (String) j.getClass().getMethod("getGenero").invoke(j); } catch (Exception e) {}
                    
                    String esp = "";
                    try { esp = (String) j.getClass().getMethod("getEsporte").invoke(j); } catch (Exception e) {}
                    if (esp == null || esp.isEmpty()) {
                        try { esp = (String) j.getClass().getMethod("getTitulo").invoke(j); } catch (Exception e) {}
                    }
                    
                    boolean matchGen = genero.equalsIgnoreCase(gen) || "geral".equalsIgnoreCase(gen) || gen == null || gen.isEmpty();
                    boolean matchEsp = esp != null && esp.toLowerCase().contains(esporte.toLowerCase());
                    
                    return matchGen && matchEsp;
                } catch (Exception e) {
                    return false;
                }
            }).collect(Collectors.toList());
            
            // 3. Executa a exclusão de forma limpa e nativa do Hibernate
            if (!paraDeletar.isEmpty()) {
                jogoRepository.deleteAll(paraDeletar);
            }
            
            return ResponseEntity.ok().body("Torneio excluído com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir o torneio no banco de dados.");
        }
    }
}