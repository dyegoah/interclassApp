package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Lote;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    
    // 🔒 Filtra os Lotes criados por aquele professor específico
    List<Lote> findByProfessorId(Long professorId);
    
}