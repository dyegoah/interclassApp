package br.com.higitech.interclasseApp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Aluno;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.AlunoRepository;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    public static class InscricaoRequestDTO {
        public String nome;
        public String turma;
        public String fotoUrl;
        public String esporte;
        public String iconeEsporte;
        public String genero;
    }

    // 🔓 ROTA PÚBLICA BLINDADA: Com Trava de 100 Alunos e Interruptor do Professor
    @PostMapping("/public/{professorHash}")
    public ResponseEntity<?> inscreverAluno(@PathVariable String professorHash, @RequestBody InscricaoRequestDTO dto) {
        
        Optional<Professor> profOpt = professorRepository.findByHashPublico(professorHash);
        if (profOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Escola não encontrada. Link inválido.");
        }
        
        Professor professor = profOpt.get();

        // 🛑 TRAVA 1: O professor fechou as inscrições pelo painel?
        if (!professor.isInscricoesAbertas()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("As inscrições para este evento foram encerradas pelo professor.");
        }

        if (dto.nome == null || dto.nome.trim().length() < 2 || dto.nome.length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome inválido ou suspeito.");
        }

        // 🛑 TRAVA 2: Limite máximo de 100 alunos por modalidade para este professor (SINTAXE CORRIGIDA AQUI 🚀)
        long totalInscritosNaModalidade = alunoRepository.findByProfessorId(professor.getId()).stream()
                .filter(a -> a.getEsporte() != null && a.getEsporte().equalsIgnoreCase(dto.esporte) && 
                             a.getGenero() != null && a.getGenero().equalsIgnoreCase(dto.genero))
                .count();

        if (totalInscritosNaModalidade >= 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Desculpe, as 100 vagas para " + dto.esporte + " (" + dto.genero + ") já foram esgotadas!");
        }
        
        Aluno novoAluno = new Aluno();
        novoAluno.setNome(dto.nome.replaceAll("<[^>]*>", ""));
        novoAluno.setTurma(dto.turma.replaceAll("<[^>]*>", ""));
        novoAluno.setFotoUrl(dto.fotoUrl);
        novoAluno.setEsporte(dto.esporte);
        novoAluno.setIconeEsporte(dto.iconeEsporte);
        novoAluno.setGenero(dto.genero);
        novoAluno.setProfessor(professor); 
        
        alunoRepository.save(novoAluno);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Inscrição confirmada na Modalidade!");
    }

    // 🚀 NOVO: Rota para o Professor alternar o status do link (Abrir/Travar Inscrições)
    @PutMapping("/status-inscricoes")
    public ResponseEntity<?> alterarStatusInscricoes(@AuthenticationPrincipal Professor professorLogado) {
        professorLogado.setInscricoesAbertas(!professorLogado.isInscricoesAbertas());
        professorRepository.save(professorLogado);
        return ResponseEntity.ok(professorLogado.isInscricoesAbertas());
    }

    // 🚀 NOVO: Rota para checar se o link está aberto (usado pela tela do aluno)
    @GetMapping("/public/status/{professorHash}")
    public ResponseEntity<?> checarStatusInscricoes(@PathVariable String professorHash) {
        Optional<Professor> profOpt = professorRepository.findByHashPublico(professorHash);
        if (profOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(profOpt.get().isInscricoesAbertas());
    }

    @GetMapping
    public ResponseEntity<List<Aluno>> listarMeusAlunos(@AuthenticationPrincipal Professor professorLogado) {
        List<Aluno> meusAlunos = alunoRepository.findByProfessorId(professorLogado.getId());
        return ResponseEntity.ok(meusAlunos);
    }

    @DeleteMapping("/{hashAluno}")
    public ResponseEntity<?> excluirAluno(@PathVariable String hashAluno, @AuthenticationPrincipal Professor professorLogado) {
        Optional<Aluno> alunoOpt = alunoRepository.findByHashPublico(hashAluno);
        if (alunoOpt.isPresent() && alunoOpt.get().getProfessor().getId().equals(professorLogado.getId())) {
            alunoRepository.delete(alunoOpt.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
    }
}