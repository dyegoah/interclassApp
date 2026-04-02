package br.com.higitech.interclasseApp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Lote;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    
    // Busca o lote ativo pelo gênero (Ex: "masculino")
    Optional<Lote> findByGenero(String genero);
}