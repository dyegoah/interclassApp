package br.com.higitech.interclasseApp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.model.Aluno;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.AlunoRepository;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public AlunoService(AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional
    public Aluno salvarComTenant(Aluno aluno) {
        // 🌟 MOCK: Captura o Professor Fantasma (ID 1)
        Professor profLogado = professorRepository.findById(1L).orElseThrow();
        
        // 🔒 Vincula o aluno ao professor logado antes de salvar
        aluno.setProfessor(profLogado); 
        return alunoRepository.save(aluno);
    }

    public List<Aluno> listarPorProfessor() {
        // 🔒 Só devolve os alunos do professor logado
        return alunoRepository.findByProfessorId(1L);
    }

    @Transactional
    public void excluirSePertencerAoProfessor(Long id) {
        Aluno aluno = alunoRepository.findById(id).orElseThrow();
        // Verificação de segurança: O aluno realmente pertence ao prof 1?
        if (aluno.getProfessor().getId().equals(1L)) {
            alunoRepository.delete(aluno);
        }
    }
}