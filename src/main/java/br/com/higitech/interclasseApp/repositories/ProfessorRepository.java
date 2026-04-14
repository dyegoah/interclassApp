package br.com.higitech.interclasseApp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    
    // O Spring faz a mágica aqui. Se um hacker digitar "' OR 1=1" no email, 
    // o JPA trata isso como texto puro, bloqueando o SQL Injection instantaneamente!
    Optional<Professor> findByEmail(String email);
    
    boolean existsByEmail(String email);
}