package br.com.higitech.interclasseApp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    List<Aluno> findByProfessorId(Long professorId);
    
    // 🛡️ Buscas Blindadas
    Optional<Aluno> findByHashPublico(String hashPublico);
    List<Aluno> findByProfessorHashPublico(String hashPublico);
}