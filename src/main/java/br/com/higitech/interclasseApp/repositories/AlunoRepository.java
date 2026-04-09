package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.higitech.interclasseApp.model.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
    // 🔒 Filtra os alunos por professor para a página alunos.html
    List<Aluno> findByProfessorId(Long professorId);
    
    // Filtra alunos de um professor específico em uma modalidade específica
    List<Aluno> findByProfessorIdAndEsporte(Long professorId, String esporte);
}