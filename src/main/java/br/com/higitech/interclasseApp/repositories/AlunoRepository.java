package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    // 🛡️ Mágica do Spring: Busca apenas os alunos de um professor específico!
    List<Aluno> findByProfessorId(Long professorId);
}