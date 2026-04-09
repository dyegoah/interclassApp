package br.com.higitech.interclasseApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Classificacao;

@Repository
public interface ClassificacaoRepository extends JpaRepository<Classificacao, Long> {
    
    // Esse método vai ser útil no futuro para o PlayHub buscar a tabela de uma modalidade específica
    java.util.List<Classificacao> findByModalidadeId(Long modalidadeId);
}