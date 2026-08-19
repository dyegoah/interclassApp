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
        // 🔒 BLINDAGEM MULTI-TENANT: O aluno é forçadamente vinculado ao Gestor Atual
        aluno.setProfessor(profLogado); 
        return alunoRepository.save(aluno);
    }

    public List<Aluno> listarPorProfessor(Professor profLogado) {
        // 🔒 Filtra direto pelo ID de segurança do professor atual
        return alunoRepository.findByProfessorId(profLogado.getId());
    }

    @Transactional
    public void excluirPeloHash(String hash, Professor profLogado) {
        Aluno aluno = alunoRepository.findByHashPublico(hash).orElseThrow();
        // 🛡️ Segurança: Garante que um professor não delete o aluno do outro
        if (aluno.getProfessor().getId().equals(profLogado.getId())) {
            alunoRepository.delete(aluno);
        }
    }
}