package br.com.higitech.interclasseApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.com.higitech.interclasseApp.model.Professor;

@Service
public class SetupService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Removemos o @Transactional para o Spring não dar Rollback se uma das tabelas estiver vazia
    public void resetarTudo(Professor professorLogado) {
        Long idProf = professorLogado.getId();

        // 🔥 O "ROLO COMPRESSOR" SQL: Apaga na ordem de "Filho para Pai" (Sem violar chaves)
        
        // 1. Limpar Eventos de Súmula e Escalações (Filhos dos Jogos)
        try { jdbcTemplate.update("DELETE FROM evento_sumula WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacao WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacoes WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}

        // 2. Limpar Classificações (Filhos de Lotes/Professores)
        try { jdbcTemplate.update("DELETE FROM classificacao WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM classificacoes WHERE professor_id = ?", idProf); } catch (Exception e) {}

        // 3. Limpar Jogos
        try { jdbcTemplate.update("DELETE FROM jogos WHERE professor_id = ?", idProf); } catch (Exception e) {}

        // 4. Limpar Torneios (Testa todas as chaves possíveis de ligação)
        try { jdbcTemplate.update("DELETE FROM torneios WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM torneios WHERE lote_id IN (SELECT id FROM lotes WHERE professor_id = ?)", idProf); } catch (Exception e) {}

        // 5. Limpar Raízes (Alunos inscritos, Modalidades e Lotes)
        try { jdbcTemplate.update("DELETE FROM alunos WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM modalidades WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM lotes WHERE professor_id = ?", idProf); } catch (Exception e) {}
    }
}