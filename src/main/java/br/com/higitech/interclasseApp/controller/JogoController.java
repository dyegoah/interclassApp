package br.com.higitech.interclasseApp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private JogoRepository jogoRepository;

    @Autowired
    private JogoService jogoService; 

    // =========================================================
    // 🛡️ DTO DE BLINDAGEM: Impede o vazamento dos dados do Professor!
    // =========================================================
    public static class JogoPublicoDTO {
        public Long id;
        public String titulo;
        public String esporte;
        public String genero;
        public String quadra;
        public String dataJogo;
        public String hora;
        public String status;
        public String icone;
        public Long equipeAId;
        public String equipeANome;
        public Long equipeBId;
        public String equipeBNome;
        public Integer placarA;
        public Integer placarB;

        public JogoPublicoDTO(Jogo jogo) {
            this.id = jogo.getId();
            this.titulo = jogo.getTitulo();
            this.genero = jogo.getGenero();
            this.quadra = jogo.getQuadra();
            this.status = jogo.getStatus();
            
            // 🔥 CORREÇÃO 1: Acessa o Esporte e o Ícone pela Modalidade
            this.esporte = jogo.getModalidade() != null ? jogo.getModalidade().getNomeEsporte() : "A Definir";
            this.icone = jogo.getModalidade() != null ? jogo.getModalidade().getIcone() : "🏆";

            // 🔥 CORREÇÃO 2: Converte os tipos LocalDate e LocalTime para String com segurança
            this.dataJogo = jogo.getDataJogo() != null ? jogo.getDataJogo().toString() : "Data Indefinida";
            this.hora = jogo.getHorario() != null ? jogo.getHorario().toString() : "Hora Indefinida";
            
            this.equipeAId = jogo.getEquipeAId();
            this.equipeANome = jogo.getEquipeANome();
            this.equipeBId = jogo.getEquipeBId();
            this.equipeBNome = jogo.getEquipeBNome();
            this.placarA = jogo.getPlacarA();
            this.placarB = jogo.getPlacarB();
            // 🚨 ATENÇÃO: O objeto 'Professor' NÃO é incluído aqui propositalmente!
        }
    }

    // =========================================================
    // 🔒 ROTA PRIVADA: Painel PlayHub (Dashboard do Professor)
    // =========================================================
    @GetMapping("/lote/{genero}")
    public ResponseEntity<List<Jogo>> listarJogosDoProfessor(
            @PathVariable String genero, 
            @AuthenticationPrincipal Professor professorLogado) {
        
        List<Jogo> jogos = jogoRepository.findByProfessorIdAndGenero(professorLogado.getId(), genero);
        return ResponseEntity.ok(jogos);
    }

    // =========================================================
    // 🔓 ROTA PÚBLICA BLINDADA: Tela de Resumo (Celular do Aluno)
    // =========================================================
    @GetMapping("/public/{professorHash}/lote/{genero}")
    public ResponseEntity<?> listarJogosPublicos(
            @PathVariable String professorHash, 
            @PathVariable String genero) {
        try {
            // Busca apenas se o Hash coincidir
            List<Jogo> jogos = jogoRepository.findByProfessorHashAndGenero(professorHash, genero);
            
            // Converte a entidade crua para o DTO seguro, cortando vazamentos
            List<JogoPublicoDTO> jogosSeguros = jogos.stream()
                .map(JogoPublicoDTO::new)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(jogosSeguros);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar as chaves públicas.");
        }
    }

    // =========================================================
    // 🔒 ROTA PRIVADA: Exclusão de Torneio inteiro
    // =========================================================
    @DeleteMapping("/torneio/{genero}/{esporte}")
    public ResponseEntity<?> excluirTorneioCompleto(
            @PathVariable String genero, 
            @PathVariable String esporte, 
            @AuthenticationPrincipal Professor professorLogado) {
        
        try {
            jogoService.excluirTorneioEspecifico(genero, esporte, professorLogado);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha ao excluir o torneio.");
        }
    }
}