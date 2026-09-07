package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Jogo;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {
    
    // Busca apenas os jogos do professor logado (Segurança Multi-Tenant)
    List<Jogo> findByProfessorId(Long professorId);

    // 🔥 NOVA ROTA PÚBLICA: Busca os jogos pelo código de segurança do link
    List<Jogo> findByProfessorHashPublico(String hashPublico);
    
 // 🔥 ADICIONE ESTA LINHA: Comando para deletar todos os jogos de um esporte e gênero específico
    @org.springframework.transaction.annotation.Transactional
    void deleteByGeneroIgnoreCaseAndEsporteIgnoreCase(String genero, String esporte);
}