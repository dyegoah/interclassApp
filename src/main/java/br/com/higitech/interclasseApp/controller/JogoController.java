package br.com.higitech.interclasseApp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.dto.CalendarioSaveDTO;
import br.com.higitech.interclasseApp.dto.JogoDTO;
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

    @PostMapping("/calendario")
    public ResponseEntity<?> salvarCalendario(@RequestBody CalendarioSaveDTO dto, @AuthenticationPrincipal Professor professorLogado) {
        jogoService.salvarLoteDeJogos(dto, professorLogado);
        return ResponseEntity.ok().body("{\"mensagem\": \"Calendário gravado com segurança!\"}");
    }

    @GetMapping("/lote/{genero}")
    public ResponseEntity<List<JogoDTO>> buscarJogosPorGenero(@PathVariable String genero, @AuthenticationPrincipal Professor professorLogado) {
        List<JogoDTO> jogos = jogoService.buscarJogosParaPlayHub(genero, professorLogado);
        return ResponseEntity.ok(jogos);
    }

    // 🔥 A ROTA RESTAURADA: Devolve os dados de apenas UM jogo para a tela de Súmula
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarJogoParaSumula(@PathVariable Long id, @AuthenticationPrincipal Professor professorLogado) {
        return ResponseEntity.ok(jogoService.buscarJogoDetalhadoParaSumula(id));
    }

    @DeleteMapping("/torneio/{genero}/{esporte}")
    public ResponseEntity<?> excluirTorneioEspecifico(@PathVariable String genero, @PathVariable String esporte, @AuthenticationPrincipal Professor professorLogado) {
        jogoService.excluirTorneioEspecifico(genero, esporte, professorLogado);
        return ResponseEntity.ok().body("{\"mensagem\": \"Torneio removido com sucesso!\"}");
    }
    
   // ==========================================
    // ROTA PÚBLICA PARA O PORTAL DO ALUNO (SEM TOKEN)
    // ==========================================
    @GetMapping("/public/{professorId}/lote/{genero}")
    public ResponseEntity<?> listarJogosPublico(@PathVariable Long professorId, @PathVariable String genero) {
        try {
            // ATENÇÃO: Verifique se o método no seu JogoRepository se chama exatamente assim.
            // Se a sua entidade Jogo se relaciona com 'usuario', pode ser findByUsuarioIdAndGenero.
            List<Jogo> jogos = jogoRepository.findByProfessorIdAndGenero(professorId, genero);
            
            return ResponseEntity.ok(jogos);
            
        } catch (Exception e) {
            System.out.println("🚨 ERRO NA ROTA PÚBLICA DE JOGOS: " + e.getMessage());
            e.printStackTrace(); // Mostra o erro exato no terminal do Eclipse/IntelliJ
            return ResponseEntity.internalServerError().body("Erro no servidor Java: " + e.getMessage());
        }
    }

}
