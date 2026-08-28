package br.com.higitech.interclasseApp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
    // Busca os alunos de um professor (Painel Administrativo)
    List<Aluno> findByProfessorId(Long professorId);
    
    // Usado para EXCLUIR um aluno na lixeira do Painel
    Optional<Aluno> findByHashPublico(String hashPublico);
    
    // 🔥 NOVA ROTA PÚBLICA: Usado na Arena Ao Vivo para baixar os alunos do torneio sem login
    List<Aluno> findByProfessorHashPublico(String hashPublico);
}