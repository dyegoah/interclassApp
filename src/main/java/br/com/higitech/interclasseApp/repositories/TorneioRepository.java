package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.model.Torneio;

@Repository
public interface TorneioRepository extends JpaRepository<Torneio, Long> {
    
    // 🔥 Padrão seguro do Spring Data: Busca diretamente pelo objeto Professor
    List<Torneio> findByProfessor(Professor professor);
    
}