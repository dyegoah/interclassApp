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
    // 🔓 ROTA PÚBLICA: Aluno se cadastra pelo celular
    // =========================================================
    @PostMapping("/public/{professorId}")
    public ResponseEntity<?> inscreverAluno(@PathVariable Long professorId, @RequestBody Aluno novoAluno) {
        Optional<Professor> profOpt = professorRepository.findById(professorId);
        
        if (profOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professor/Escola não encontrado(a). Link inválido.");
        }
        
        novoAluno.setProfessor(profOpt.get()); // Carimba o aluno com o ID do professor
        alunoRepository.save(novoAluno);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Inscrição realizada com sucesso!");
    }

    // =========================================================
    // 🔒 ROTA PRIVADA: Professor lista os seus próprios alunos
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Aluno>> listarMeusAlunos(@AuthenticationPrincipal Professor professorLogado) {
        // O Token JWT já nos diz quem é o professor. Buscamos apenas os alunos dele!
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
}