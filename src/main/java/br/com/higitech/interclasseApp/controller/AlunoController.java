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
import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.AlunoRepository;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ModalidadeRepository modalidadeRepository;

    public static class InscricaoRequestDTO {
        public String nome;
        public String turma;
        public String fotoUrl;
        public String esporte;
        public String iconeEsporte;
        public String genero;
    }

    @PostMapping("/public/{professorHash}")
    public ResponseEntity<?> inscreverAluno(@PathVariable String professorHash, @RequestBody InscricaoRequestDTO dto) {
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

        // 🔥 TRAVA DE SEGURANÇA: Confirma usando APENAS o lote "geral" de Inscrições
        List<Modalidade> modalidadesAtivas = modalidadeRepository.findByLoteProfessorHashPublicoAndLoteGenero(professorHash, "geral");
        boolean esportePermitido = modalidadesAtivas.stream()
                .anyMatch(m -> m.getNomeEsporte().equalsIgnoreCase(dto.esporte));

        if (!esportePermitido) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Esta modalidade não está aberta para inscrições nesta escola.");
        }

        long totalInscritosNaModalidade = alunoRepository.findByProfessorId(professor.getId()).stream()
                .filter(a -> a.getEsporte() != null && a.getEsporte().equalsIgnoreCase(dto.esporte) &&
                             a.getGenero() != null && a.getGenero().equalsIgnoreCase(dto.genero))
                .count();

        if (totalInscritosNaModalidade >= 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Desculpe, as 100 vagas para " + dto.esporte + " estão esgotadas!");
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

    @PutMapping("/status-inscricoes")
    public ResponseEntity<?> alterarStatusInscricoes(@AuthenticationPrincipal Professor professorLogado) {
        professorLogado.setInscricoesAbertas(!professorLogado.isInscricoesAbertas());
        professorRepository.save(professorLogado);
        return ResponseEntity.ok(professorLogado.isInscricoesAbertas());
    }

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