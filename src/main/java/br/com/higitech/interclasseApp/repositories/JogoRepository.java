package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.model.Jogo;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {
    
    List<Jogo> findByProfessorId(Long professorId);
    
    List<Jogo> findByProfessorIdAndGenero(Long professorId, String genero);
    
    
    @Modifying
    @Transactional
    void deleteByProfessorIdAndGenero(Long professorId, String genero);

    // 🔥 O COMANDO SEGURO: Cruza a tabela de Jogos com a Modalidade e Deleta
    @Modifying
    @Transactional
    @Query("DELETE FROM Jogo j WHERE j.professor.id = :profId AND j.genero = :genero AND j.modalidade.nomeEsporte = :esporte")
    void excluirTorneioEspecificoDoBanco(@Param("profId") Long profId, @Param("genero") String genero, @Param("esporte") String esporte);
}