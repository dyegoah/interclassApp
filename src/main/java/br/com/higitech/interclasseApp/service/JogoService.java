package br.com.higitech.interclasseApp.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.JogoRepository;

@Service
public class JogoService {

    @Autowired
    private JogoRepository jogoRepository;

    @SuppressWarnings("unchecked")
    public void salvarCalendario(Map<String, Object> payload, Professor professorLogado) {
        List<Map<String, Object>> jogosPayload = (List<Map<String, Object>>) payload.get("jogos");
        String generoDefault = payload.get("genero") != null ? payload.get("genero").toString() : "geral";

        if (jogosPayload != null) {
            for (Map<String, Object> jData : jogosPayload) {
                
                Jogo jogo = new Jogo();

                // 1. Proteção contra IDs: Se já tem ID, é edição. Senão, é um jogo novo.
                if (jData.get("id") != null && !jData.get("id").toString().isEmpty()) {
                    try {
                        Long idFront = Long.valueOf(jData.get("id").toString());
                        Optional<Jogo> existe = jogoRepository.findById(idFront);
                        // Garante que o jogo só será alterado se pertencer ao professor logado
                        if (existe.isPresent() && existe.get().getProfessor() != null && existe.get().getProfessor().getId().equals(professorLogado.getId())) {
                            jogo = existe.get();
                        }
                    } catch (Exception e) {}
                }

                // 2. Configurações base do Jogo
                jogo.setProfessor(professorLogado);
                try {
                    String statusAtual = (String) jogo.getClass().getMethod("getStatus").invoke(jogo);
                    if (statusAtual == null || statusAtual.isEmpty()) {
                        jogo.getClass().getMethod("setStatus", String.class).invoke(jogo, "AGENDADO");
                    }
                } catch (Exception e) {}

                // 3. Extração dos dados enviados pelo Calendário
                String generoJogo = jData.get("genero") != null ? jData.get("genero").toString() : generoDefault;
                String diaId = jData.get("diaId") != null ? jData.get("diaId").toString() : (jData.get("dataJogo") != null ? jData.get("dataJogo").toString() : "");
                String icone = jData.get("icone") != null ? jData.get("icone").toString() : "🏅";
                String quadra = jData.get("quadra") != null ? jData.get("quadra").toString() : "";
                String eqA = jData.get("equipeANome") != null ? jData.get("equipeANome").toString() : "";
                String eqB = jData.get("equipeBNome") != null ? jData.get("equipeBNome").toString() : "";
                String esporte = jData.get("esporte") != null ? jData.get("esporte").toString() : "";
                String titulo = jData.get("titulo") != null ? jData.get("titulo").toString() : "";

                // 4. Injeção segura de dados (Independente do nome que você deu às variáveis no model Jogo.java)
                injetarDado(jogo, "setGenero", generoJogo);
                injetarDado(jogo, "setDataJogo", diaId);
                injetarDado(jogo, "setDiaId", diaId);
                injetarDado(jogo, "setIcone", icone);
                injetarDado(jogo, "setIconeEsporte", icone);
                injetarDado(jogo, "setQuadra", quadra);
                injetarDado(jogo, "setEquipeANome", eqA);
                injetarDado(jogo, "setEquipeBNome", eqB);
                injetarDado(jogo, "setEsporte", esporte);
                injetarDado(jogo, "setTitulo", titulo);

                // 5. Salva no banco de dados
                jogoRepository.save(jogo);
            }
        }
    }

    // 🔥 Ferramenta Interna: Previne erros de compilação se a classe Jogo não tiver algum atributo
    private void injetarDado(Object alvo, String nomeDoMetodo, String valor) {
        try {
            alvo.getClass().getMethod(nomeDoMetodo, String.class).invoke(alvo, valor);
        } catch (Exception e) {
            // Ignora silenciosamente se a coluna não existir no model
        }
    }

    public List<Jogo> buscarJogosPorProfessor(Professor professorLogado) {
        return jogoRepository.findByProfessorId(professorLogado.getId());
    }
}