package br.com.higitech.interclasseApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Torneio;

@Repository
public interface TorneioRepository extends JpaRepository<Torneio, Long> {
    // Você pode adicionar buscas customizadas aqui depois, se precisar.
}