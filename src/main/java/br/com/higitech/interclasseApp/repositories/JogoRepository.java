package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Jogo;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {
    
    // Busca interna para o Professor logado
    @Query("SELECT j FROM Jogo j WHERE j.professor.id = :profId AND j.genero = :genero")
    List<Jogo> findByProfessorIdAndGenero(@Param("profId") Long profId, @Param("genero") String genero);

    // 🛡️ BUSCA BLINDADA PÚBLICA: Usa o Hash do professor e impede IDOR
    @Query("SELECT j FROM Jogo j WHERE j.professor.hashPublico = :hash AND j.genero = :genero")
    List<Jogo> findByProfessorHashAndGenero(@Param("hash") String hash, @Param("genero") String genero);
    
    // 🛡️ CORREÇÃO 1: Usa j.modalidade.nomeEsporte em vez de j.esporte e exige @Modifying
    @Modifying
    @Query("DELETE FROM Jogo j WHERE j.professor.id = :profId AND j.genero = :genero AND j.modalidade.nomeEsporte = :esporte")
    void excluirTorneioEspecificoDoBanco(@Param("profId") Long profId, @Param("genero") String genero, @Param("esporte") String esporte);

    // Usado pelo JogoService ao recriar um lote inteiro
    @Modifying
    @Query("DELETE FROM Jogo j WHERE j.professor.id = :profId AND j.genero = :genero")
    void deleteByProfessorIdAndGenero(@Param("profId") Long profId, @Param("genero") String genero);

    // Necessário para o motor de Súmulas
    List<Jogo> findByProfessorId(Long profId);
}