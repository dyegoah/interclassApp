package br.com.higitech.interclasseApp.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

    // =========================================================
    // 🛡️ DTO: Evita vazar dados sensíveis na rota pública
    // =========================================================
    public static class AlunoPublicoDTO {
        public Long id;
        public String nome;
        
        public AlunoPublicoDTO(Aluno aluno) {
            this.id = aluno.getId();
            this.nome = aluno.getNome();
        }
    }

    // =========================================================
    // 🔓 ROTA PÚBLICA BLINDADA: Aluno se cadastra pelo celular
    // =========================================================
    @PostMapping("/public/{professorId}")
    public ResponseEntity<?> inscreverAluno(@PathVariable Long professorId, @RequestBody Aluno novoAluno) {
        
        // 🛡️ TRAVA ANTI-SPAM: Impede nomes vazios, muito curtos ou gigantes
        if(novoAluno.getNome() == null || novoAluno.getNome().trim().length() < 3 || novoAluno.getNome().length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome inválido ou suspeito.");
        }
        
        // 🛡️ PROTEÇÃO XSS: Limpa tags HTML (hacker) do nome do aluno
        novoAluno.setNome(novoAluno.getNome().replaceAll("<[^>]*>", ""));
        
        Optional<Professor> profOpt = professorRepository.findById(professorId);
        if (profOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Escola não encontrada. Link inválido.");
        }
        
        novoAluno.setProfessor(profOpt.get()); 
        alunoRepository.save(novoAluno);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Inscrição realizada com sucesso!");
    }

    // =========================================================
    // 🔒 ROTA PRIVADA: Professor lista os seus próprios alunos
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Aluno>> listarMeusAlunos(@AuthenticationPrincipal Professor professorLogado) {
        List<Aluno> meusAlunos = alunoRepository.findByProfessorId(professorLogado.getId());
        return ResponseEntity.ok(meusAlunos);
    }

    // =========================================================
    // 🔒 ROTA PRIVADA: Professor exclui um aluno
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirAluno(@PathVariable Long id, @AuthenticationPrincipal Professor professorLogado) {
        Optional<Aluno> alunoOpt = alunoRepository.findById(id);
        
        if (alunoOpt.isPresent() && alunoOpt.get().getProfessor().getId().equals(professorLogado.getId())) {
            alunoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para excluir este aluno.");
    }
    
    // =========================================================
    // 🔓 ROTA PÚBLICA BLINDADA: Retorna apenas o ID e Nome (DTO)
    // =========================================================
    @GetMapping("/public/{professorId}")
    public ResponseEntity<?> listarAlunosPublico(@PathVariable Long professorId) {
        try {
            List<Aluno> alunos = alunoRepository.findByProfessorId(professorId);
            
            // Converte a lista crua do banco para a lista segura (apenas ID e Nome)
            List<AlunoPublicoDTO> alunosSeguros = alunos.stream()
                .map(AlunoPublicoDTO::new)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(alunosSeguros);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro interno no servidor.");
        }
    }
}