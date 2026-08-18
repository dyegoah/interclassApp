package br.com.higitech.interclasseApp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByEmail(String email);
    
    // 🛡️ Busca pelo Link Criptografado
    Optional<Professor> findByHashPublico(String hashPublico);
}