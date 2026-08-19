package br.com.higitech.interclasseApp.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.dto.EsporteSetupDTO;
import br.com.higitech.interclasseApp.dto.LoteSetupDTO;
import br.com.higitech.interclasseApp.model.Aluno;
import br.com.higitech.interclasseApp.model.Classificacao;
import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.model.Lote;
import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.model.Torneio;
import br.com.higitech.interclasseApp.repositories.AlunoRepository;
import br.com.higitech.interclasseApp.repositories.ClassificacaoRepository;
import br.com.higitech.interclasseApp.repositories.JogoRepository;
import br.com.higitech.interclasseApp.repositories.LoteRepository;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository;
import br.com.higitech.interclasseApp.repositories.TorneioRepository;

@Service
@Transactional
public class SetupService {

    private final TorneioRepository torneioRepository;
    private final LoteRepository loteRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final ClassificacaoRepository classificacaoRepository;
    private final JogoRepository jogoRepository;
    private final AlunoRepository alunoRepository;
    private final JdbcTemplate jdbcTemplate; // 🚀 A FERRAMENTA DE LIMPEZA BRUTA

    public SetupService(TorneioRepository torneioRepository, LoteRepository loteRepository, 
                        ModalidadeRepository modalidadeRepository, ClassificacaoRepository classificacaoRepository,
                        JogoRepository jogoRepository, AlunoRepository alunoRepository, JdbcTemplate jdbcTemplate) {
        this.torneioRepository = torneioRepository;
        this.loteRepository = loteRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.classificacaoRepository = classificacaoRepository;
        this.jogoRepository = jogoRepository;
        this.alunoRepository = alunoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void processarLote(LoteSetupDTO dto) {
        Professor profLogado = (Professor) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Torneio torneioAtivo = torneioRepository.findAll().stream()
                .findFirst() 
                .orElseGet(() -> {
                    Torneio t = new Torneio();
                    t.setTitulo("Interclasses Oficial");
                    t.setStatus("ATIVO");
                    return torneioRepository.save(t);
                });

        Lote lote = new Lote();
        lote.setGenero(dto.genero());
        lote.setTorneio(torneioAtivo);
        lote.setProfessor(profLogado); 
        lote = loteRepository.save(lote);

        for (EsporteSetupDTO esporteDto : dto.esportes()) {
            Modalidade modalidade = new Modalidade();
            modalidade.setNomeEsporte(obterNomeEsporte(esporteDto.id()));
            modalidade.setLote(lote);
            
            Modalidade modalidadeSalva = modalidadeRepository.save(modalidade);

            if ("liga".equals(esporteDto.formato()) || "grupos".equals(esporteDto.formato()) || "liga_playoffs".equals(esporteDto.formato())) {
                for (int i = 1; i <= esporteDto.qtdTimes(); i++) {
                    Classificacao classif = new Classificacao();
                    classif.setModalidade(modalidadeSalva);
                    classif.setNomeTurma("Equipe " + i + " (A Definir)");
                    classificacaoRepository.save(classif);
                }
            }
        }
    }

    // 🔴 O CÓDIGO DA FAXINA COMPLETA E HIERÁRQUICA (Livre de Erros 500)
    public void resetarTudo(Professor profLogado) {
        Long idProf = profLogado.getId();
        
        // 1. A MARRETA SQL: Fura bloqueios de Foreign Key e limpa as amarras!
        // Ignora erros caso as tabelas estejam vazias ou com outro nome de mapeamento
        try { jdbcTemplate.update("DELETE FROM evento_sumula WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacao WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacoes WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}

        // 2. Apaga os Jogos (agora sem os "móveis" prendendo)
        List<Jogo> jogos = jogoRepository.findByProfessorId(idProf);
        if (!jogos.isEmpty()) jogoRepository.deleteAll(jogos);
        
        // 3. Apaga os Alunos
        List<Aluno> alunos = alunoRepository.findByProfessorId(idProf);
        if (!alunos.isEmpty()) alunoRepository.deleteAll(alunos);
        
        // 4. Encontra os Lotes atrelados EXCLUSIVAMENTE a este professor
        List<Lote> lotes = loteRepository.findByProfessorId(idProf);
        
        if (!lotes.isEmpty()) {
            
            // 5. Apaga de BAIXO PARA CIMA: Primeiro as Classificações (Tabela de Grupos)...
            List<Classificacao> classificacoes = classificacaoRepository.findAll().stream()
                .filter(c -> c.getModalidade() != null && c.getModalidade().getLote() != null && c.getModalidade().getLote().getProfessor().getId().equals(idProf))
                .toList();
            if (!classificacoes.isEmpty()) classificacaoRepository.deleteAll(classificacoes);
            
            // 6. ...depois apaga as Modalidades...
            List<Modalidade> modalidades = modalidadeRepository.findAll().stream()
                .filter(m -> m.getLote() != null && m.getLote().getProfessor().getId().equals(idProf))
                .toList();
            if (!modalidades.isEmpty()) modalidadeRepository.deleteAll(modalidades);
            
            // 7. ...e finalmente implode os Lotes deste professor!
            loteRepository.deleteAll(lotes);
        }
    }

    private String obterNomeEsporte(Integer id) {
        switch (id) {
            case 1: return "Futsal"; case 2: return "Vôlei"; case 3: return "Basquete";
            case 4: return "Handebol"; case 5: return "Queimada"; case 6: return "Vôlei de Areia";
            case 7: return "Natação"; case 8: return "Atletismo"; case 9: return "Tênis de Mesa"; 
            case 10: return "Xadrez"; case 11: return "Dama"; case 12: return "E-Sports"; 
            default: return "Esporte Geral";
        }
    }
}