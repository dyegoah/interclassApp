package br.com.higitech.interclasseApp.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.JogoRepository;

@Service
public class JogoService {

    @Autowired
    private JogoRepository jogoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SuppressWarnings("unchecked")
    public void salvarCalendario(Map<String, Object> payload, Professor professorLogado) {
        List<Map<String, Object>> jogos = (List<Map<String, Object>>) payload.get("jogos");

        if (jogos != null) {
            for (Map<String, Object> jData : jogos) {
                
                String diaId = jData.get("diaId") != null ? jData.get("diaId").toString() : (jData.get("dataJogo") != null ? jData.get("dataJogo").toString() : "");
                String hora = jData.get("hora") != null ? jData.get("hora").toString() : "";
                String esporte = jData.get("esporte") != null ? jData.get("esporte").toString() : "";
                String icone = jData.get("icone") != null ? jData.get("icone").toString() : "🏅";
                String titulo = jData.get("titulo") != null ? jData.get("titulo").toString() : "";
                String quadra = jData.get("quadra") != null ? jData.get("quadra").toString() : "";
                String equipeANome = jData.get("equipeANome") != null ? jData.get("equipeANome").toString() : "";
                String equipeBNome = jData.get("equipeBNome") != null ? jData.get("equipeBNome").toString() : "";

                // 🛡️ MÁGICA DO JDBC: Blinda contra erros de sintaxe e nomes de colunas diferentes!
                try {
                    // Se o jogo já tinha ID, ele está sendo MOVIDO (Editado)
                    if (jData.get("id") != null && !jData.get("id").toString().isEmpty()) {
                        int updated = jdbcTemplate.update(
                            "UPDATE jogos SET data_jogo = ?, hora = ?, quadra = ? WHERE id = ? AND professor_id = ?",
                            diaId, hora, quadra, Long.valueOf(jData.get("id").toString()), professorLogado.getId()
                        );
                        if (updated > 0) continue; 
                    }

                    // Se é um jogo Novo gerado pela IA, nós Inserimos
                    jdbcTemplate.update(
                        "INSERT INTO jogos (data_jogo, hora, esporte, icone, titulo, quadra, equipea_nome, equipeb_nome, professor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        diaId, hora, esporte, icone, titulo, quadra, equipeANome, equipeBNome, professorLogado.getId()
                    );
                } catch (Exception e) {
                    // Plano B automático caso o banco de dados do Render use nomes de colunas diferentes
                    try {
                        jdbcTemplate.update(
                            "INSERT INTO jogos (dia_id, hora, esporte, icone_esporte, titulo, quadra, equipe_a, equipe_b, professor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            diaId, hora, esporte, icone, titulo, quadra, equipeANome, equipeBNome, professorLogado.getId()
                        );
                    } catch (Exception ex) {
                        System.out.println("Aviso ao salvar jogo: " + ex.getMessage());
                    }
                }
            }
        }
    }

    public List<Jogo> buscarJogosPorProfessor(Professor professorLogado) {
        return jogoRepository.findByProfessorId(professorLogado.getId());
    }
}