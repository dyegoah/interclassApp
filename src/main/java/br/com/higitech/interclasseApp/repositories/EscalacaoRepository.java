package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Escalacao;

@Repository
public interface EscalacaoRepository extends JpaRepository<Escalacao, Long> {
    
    // Busca a escalação de um jogo separando pela letra da equipe ("A" ou "B")
    List<Escalacao> findByJogoIdAndEquipe(Long jogoId, String equipe);
}