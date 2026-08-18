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

    // 🛡️ DTO Blindado com Hash
    public static class AlunoPublicoDTO {
        public String hash;
        public String nome;
        
        public AlunoPublicoDTO(Aluno aluno) {
            this.hash = aluno.getHashPublico();
            this.nome = aluno.getNome();
        }
    }

    // 🔓 ROTA PÚBLICA TOTALMENTE BLINDADA: Usa HASH em vez de ID 1, 2, 3...
    @PostMapping("/public/{professorHash}")
    public ResponseEntity<?> inscreverAluno(@PathVariable String professorHash, @RequestBody Aluno novoAluno) {
        
        if(novoAluno.getNome() == null || novoAluno.getNome().trim().length() < 3 || novoAluno.getNome().length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome inválido ou suspeito.");
        }
        
        novoAluno.setNome(novoAluno.getNome().replaceAll("<[^>]*>", ""));
        
        // Busca a escola pela chave encriptada
        Optional<Professor> profOpt = professorRepository.findByHashPublico(professorHash);
        if (profOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Escola não encontrada. Link inválido.");
        }
        
        novoAluno.setProfessor(profOpt.get()); 
        alunoRepository.save(novoAluno);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Inscrição realizada com sucesso!");
    }

    // 🔓 LISTAGEM PÚBLICA (Alunos matriculados)
    @GetMapping("/public/{professorHash}")
    public ResponseEntity<?> listarAlunosPublico(@PathVariable String professorHash) {
        try {
            List<Aluno> alunos = alunoRepository.findByProfessorHashPublico(professorHash);
            List<AlunoPublicoDTO> alunosSeguros = alunos.stream().map(AlunoPublicoDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(alunosSeguros);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno no servidor.");
        }
    }

    // 🔒 PRIVADA: Professor lista os seus
    @GetMapping
    public ResponseEntity<List<Aluno>> listarMeusAlunos(@AuthenticationPrincipal Professor professorLogado) {
        List<Aluno> meusAlunos = alunoRepository.findByProfessorId(professorLogado.getId());
        return ResponseEntity.ok(meusAlunos);
    }

    // 🔒 PRIVADA: Exclui através do Hash Público do Aluno
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