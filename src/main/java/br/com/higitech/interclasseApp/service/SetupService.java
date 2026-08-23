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
    public void resetarTudo(Professor profLogado) {
        Long idProf = profLogado.getId();

        // 1. Limpa Súmulas e Escalações (Filhos dos Jogos)
        try { jdbcTemplate.update("DELETE FROM evento_sumula WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacao WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacoes WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}

        // 2. Limpa Classificações e Jogos
        try { jdbcTemplate.update("DELETE FROM classificacao WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM classificacoes WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM jogos WHERE professor_id = ?", idProf); } catch (Exception e) {}

        // 3. Limpa Raízes (Alunos, Modalidades e Lotes)
        try { jdbcTemplate.update("DELETE FROM alunos WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM modalidades WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM lotes WHERE professor_id = ?", idProf); } catch (Exception e) {}

        // 4. Limpa Torneios Órfãos para não deixar lixo no banco
        try { jdbcTemplate.update("DELETE FROM torneios WHERE id NOT IN (SELECT torneio_id FROM lotes)"); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM torneios WHERE id NOT IN (SELECT torneio_id FROM tb_lote)"); } catch (Exception e) {}
    }
}