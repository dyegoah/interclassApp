package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
    // Traz todos os alunos inscritos numa modalidade específica (Ex: Futsal)
    List<Aluno> findByModalidadeId(Long modalidadeId);
    
    // Filtro para a barra de pesquisa do alunos.html
    List<Aluno> findByNomeContainingIgnoreCaseOrTurmaContainingIgnoreCase(String nome, String turma);
}