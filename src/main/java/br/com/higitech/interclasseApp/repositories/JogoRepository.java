package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Jogo;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {
    
    // A mágica do Spring: Navega de Jogo -> Modalidade -> Lote -> Genero!
    List<Jogo> findByModalidadeLoteGeneroOrderByDataJogoAscHorarioAsc(String genero);
    
    List<Jogo> findByModalidadeId(Long modalidadeId);
}