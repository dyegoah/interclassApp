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
import br.com.higitech.interclasseApp.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    public static class InscricaoRequestDTO {
        public String nome;
        public String turma;
        public String fotoUrl;
        public String esporte;
        public String iconeEsporte;
        public String genero;
        public String honeypot; 
    }

    @PostMapping("/public/{professorHash}")
    public ResponseEntity<?> inscreverAluno(@PathVariable String professorHash, @RequestBody InscricaoRequestDTO dto, HttpServletRequest request) {
        
        if (dto.honeypot != null && !dto.honeypot.trim().isEmpty()) {
            loginAttemptService.registrarLog("SPAM: " + dto.nome, "ATAQUE SPAM BLOQUEADO (HONEYPOT)", request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Inscrição confirmada na Modalidade!");
        }

        Optional<Professor> profOpt = professorRepository.findByHashPublico(professorHash);
        if (profOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Escola não encontrada. Link inválido.");
        }
        Professor professor = profOpt.get();

        if (!professor.isInscricoesAbertas()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("As inscrições para este evento foram encerradas.");
        }

        if (dto.nome == null || dto.nome.trim().length() < 2 || dto.nome.length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome inválido ou suspeito.");
        }

        // 🔥 BLINDAGEM DE UPLOAD (Validação de Base64) 🔥
        if (dto.fotoUrl != null && !dto.fotoUrl.trim().isEmpty()) {
            // 1. Prevenção contra DoS (Exaustão de Memória)
            // 5MB em Base64 representa aproximadamente 6.8MB de caracteres. O limite trava strings abusivas.
            if (dto.fotoUrl.length() > 7 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A imagem excede o tamanho máximo de 5MB.");
            }
            
            // 2. Prevenção contra Malware e XSS Avançado (SVG/Scripts)
            // Aceita estritamente os cabeçalhos MIME de imagens seguras
            if (!dto.fotoUrl.startsWith("data:image/jpeg;base64,") &&
                !dto.fotoUrl.startsWith("data:image/png;base64,") &&
                !dto.fotoUrl.startsWith("data:image/webp;base64,") &&
                !dto.fotoUrl.startsWith("data:image/gif;base64,")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de imagem inválido. Envie apenas JPG, PNG, WEBP ou GIF.");
            }
        }

        long totalAlunos = alunoRepository.countByProfessorId(professor.getId());

        if (totalAlunos >= 250) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O limite total de alunos inscritos para esta instituição foi atingido.");
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

    @GetMapping("/public/status/{professorHash}")
    public ResponseEntity<?> checarStatusInscricoes(@PathVariable String professorHash) {
        Optional<Professor> profOpt = professorRepository.findByHashPublico(professorHash);
        if (profOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(profOpt.get().isInscricoesAbertas());
    }

    @GetMapping("/public/{hashPublico}")
    public ResponseEntity<List<Aluno>> getAlunosPublicos(@PathVariable String hashPublico) {
        List<Aluno> alunos = alunoRepository.findByProfessorHashPublico(hashPublico);
        return ResponseEntity.ok(alunos);
    }

    @PutMapping("/status-inscricoes")
    public ResponseEntity<?> alterarStatusInscricoes(@AuthenticationPrincipal Professor professorLogado) {
        professorLogado.setInscricoesAbertas(!professorLogado.isInscricoesAbertas());
        professorRepository.save(professorLogado);
        return ResponseEntity.ok(professorLogado.isInscricoesAbertas());
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