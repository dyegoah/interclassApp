package br.com.higitech.interclasseApp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.higitech.interclasseApp.model.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    
    // Método essencial para a tela de Login: Buscar o professor pelo e-mail
    Optional<Professor> findByEmail(String email);
    
}