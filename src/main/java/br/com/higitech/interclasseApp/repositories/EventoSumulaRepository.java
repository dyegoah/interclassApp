package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.EventoSumula;

@Repository
public interface EventoSumulaRepository extends JpaRepository<EventoSumula, Long> {
    
    // Traz a linha do tempo (Timeline) inteira de uma partida finalizada
    List<EventoSumula> findByJogoIdOrderByIdAsc(Long jogoId);
    
    // QUERY INTELIGENTE: Pede ao PostgreSQL para somar todos os pontos de um aluno no torneio inteiro
    @Query("SELECT SUM(e.pontosGerados) FROM EventoSumula e WHERE e.aluno.id = :alunoId")
    Integer somarPontosTotaisDoAluno(@Param("alunoId") Long alunoId);
    
    // Conta quantos cartões/faltas um aluno teve em todo o torneio
    @Query("SELECT COUNT(e) FROM EventoSumula e WHERE e.aluno.id = :alunoId AND e.pontosGerados = 0")
    Integer contarFaltasTotaisDoAluno(@Param("alunoId") Long alunoId);
}