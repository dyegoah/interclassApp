package br.com.higitech.interclasseApp.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.com.higitech.interclasseApp.model.Lote;
import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.LoteRepository;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository;

@Service
public class SetupService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ModalidadeRepository modalidadeRepository;

    @SuppressWarnings("unchecked")
    public void processarLote(Map<String, Object> payload, Professor professorLogado) {
        String genero = payload.get("genero") != null ? payload.get("genero").toString() : "geral";
        
        if ("geral".equalsIgnoreCase(genero)) {
            try {
                List<Modalidade> antigas = modalidadeRepository.findByLoteProfessorHashPublico(professorLogado.getHashPublico())
                        .stream()
                        .filter(m -> m.getLote() != null && "geral".equalsIgnoreCase(m.getLote().getGenero()))
                        .collect(Collectors.toList());
                modalidadeRepository.deleteAll(antigas);
            } catch (Exception e) {
                System.out.println("Aviso ao limpar modalidades: " + e.getMessage());
            }
        }

        Lote lote = new Lote();
        lote.setGenero(genero);
        lote.setProfessor(professorLogado);
        lote = loteRepository.save(lote);

        List<Map<String, Object>> esportes = (List<Map<String, Object>>) payload.get("esportes");
        if (esportes != null) {
            for (Map<String, Object> esp : esportes) {
                Long id = Long.valueOf(esp.get("id").toString());
                Modalidade mod = new Modalidade();
                mod.setLote(lote);
                mod.setNomeEsporte(getNomeEsportePorId(id));
                mod.setIcone(getIconePorId(id));
                modalidadeRepository.save(mod);
            }
        }
    }

    private String getNomeEsportePorId(Long id) {
        if (id == null) return "Desconhecido";
        switch (id.intValue()) {
            case 1: return "Futsal"; case 2: return "Vôlei"; case 3: return "Basquete";
            case 4: return "Handebol"; case 5: return "Queimada"; case 6: return "Vôlei de Areia";
            case 7: return "Natação"; case 8: return "Atletismo"; case 9: return "Tênis de Mesa";
            case 10: return "Xadrez"; case 11: return "Dama"; case 12: return "E-Sports";
            default: return "Outro";
        }
    }

    private String getIconePorId(Long id) {
        if (id == null) return "🏅";
        switch (id.intValue()) {
            case 1: return "⚽"; case 2: return "🏐"; case 3: return "🏀";
            case 4: return "🤾"; case 5: return "☄️"; case 6: return "🏖️";
            case 7: return "🏊"; case 8: return "🏃"; case 9: return "🏓";
            case 10: return "♟️"; case 11: return "🏁"; case 12: return "🎮";
            default: return "🏅";
        }
    }

    // 🔥 CORREÇÃO: Função blindada para rodar em Localhost (Plural) e Render (Singular) 🔥
    public void resetarTudo(Professor profLogado) {
        Long idProf = profLogado.getId();

        // 1. Apaga Súmulas e Escalações
        try { jdbcTemplate.update("DELETE FROM evento_sumula WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM evento_sumula WHERE jogo_id IN (SELECT id FROM jogo WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacao WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacao WHERE jogo_id IN (SELECT id FROM jogo WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacoes WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacoes WHERE jogo_id IN (SELECT id FROM jogo WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        
        // 2. Apaga Classificações
        try { jdbcTemplate.update("DELETE FROM classificacao WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM classificacoes WHERE professor_id = ?", idProf); } catch (Exception e) {}
        
        // 3. Apaga Jogos e Alunos
        try { jdbcTemplate.update("DELETE FROM jogos WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM jogo WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM alunos WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM aluno WHERE professor_id = ?", idProf); } catch (Exception e) {}
        
        // 4. Apaga Modalidades e Lotes
        try { jdbcTemplate.update("DELETE FROM modalidades WHERE lote_id IN (SELECT id FROM lotes WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM modalidade WHERE lote_id IN (SELECT id FROM lote WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM lotes WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM lote WHERE professor_id = ?", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM tb_lote WHERE professor_id = ?", idProf); } catch (Exception e) {}
        
        // 5. Apaga Torneios (A tabela oficial do Render ficou como tb_torneio)
        try { 
            jdbcTemplate.update("DELETE FROM torneios WHERE professor_id = ?", idProf); 
        } catch (Exception e) {}

        try { 
            jdbcTemplate.update("DELETE FROM tb_torneio WHERE professor_id = ?", idProf); 
        } catch (Exception e) {}
    }
}