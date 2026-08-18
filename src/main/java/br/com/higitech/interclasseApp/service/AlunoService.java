package br.com.higitech.interclasseApp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.model.Aluno;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.AlunoRepository;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public Aluno salvarComTenant(Aluno aluno, Professor profLogado) {
        // 🔒 Vincula o aluno EXATAMENTE ao professor que está logado (Fim do vazamento)
        aluno.setProfessor(profLogado); 
        return alunoRepository.save(aluno);
    }

    public List<Aluno> listarPorProfessor(Professor profLogado) {
        // 🔒 Só devolve os alunos do professor correto
        return alunoRepository.findByProfessorId(profLogado.getId());
    }

    @Transactional
    public void excluirPeloHash(String hash, Professor profLogado) {
        Aluno aluno = alunoRepository.findByHashPublico(hash).orElseThrow();
        // 🛡️ Segurança extra: verifica se o aluno pertence a este professor
        if (aluno.getProfessor().getId().equals(profLogado.getId())) {
            alunoRepository.delete(aluno);
        }
    }
}