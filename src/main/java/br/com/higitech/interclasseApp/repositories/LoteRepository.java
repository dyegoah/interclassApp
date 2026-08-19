package br.com.higitech.interclasseApp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Lote;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    
    // 🛡️ BLINDAGEM MULTI-TENANT: Isola os lotes por Professor
    List<Lote> findByProfessorId(Long professorId);
    Optional<Lote> findByProfessorIdAndGenero(Long professorId, String genero);
}