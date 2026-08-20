package br.com.higitech.interclasseApp.service;

import java.util.List;
import java.util.stream.Collectors;

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
    private final JdbcTemplate jdbcTemplate;

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
            modalidade.setIcone(obterIconeEsporte(esporteDto.id())); // 🚀 FIX: Agora o ícone é salvo no banco!
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

    public void resetarTudo(Professor profLogado) {
        Long idProf = profLogado.getId();

        try { jdbcTemplate.update("DELETE FROM evento_sumula WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacao WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM escalacoes WHERE jogo_id IN (SELECT id FROM jogos WHERE professor_id = ?)", idProf); } catch (Exception e) {}

        List<Classificacao> classificacoes = classificacaoRepository.findAll().stream()
            .filter(c -> c.getModalidade() != null && c.getModalidade().getLote() != null && c.getModalidade().getLote().getProfessor() != null && c.getModalidade().getLote().getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        List<Modalidade> modalidades = modalidadeRepository.findAll().stream()
            .filter(m -> m.getLote() != null && m.getLote().getProfessor() != null && m.getLote().getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        List<Lote> lotes = loteRepository.findAll().stream()
            .filter(l -> l.getProfessor() != null && l.getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        List<Jogo> jogos = jogoRepository.findAll().stream()
            .filter(j -> j.getProfessor() != null && j.getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        List<Aluno> alunos = alunoRepository.findAll().stream()
            .filter(a -> a.getProfessor() != null && a.getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        if (!classificacoes.isEmpty()) classificacaoRepository.deleteAll(classificacoes);
        if (!modalidades.isEmpty()) modalidadeRepository.deleteAll(modalidades);
        if (!lotes.isEmpty()) loteRepository.deleteAll(lotes);
        
        if (!jogos.isEmpty()) jogoRepository.deleteAll(jogos); 
        if (!alunos.isEmpty()) alunoRepository.deleteAll(alunos); 
        
        try { jdbcTemplate.update("DELETE FROM torneios WHERE id NOT IN (SELECT torneio_id FROM tb_lote)"); } catch (Exception e) {}
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

    private String obterIconeEsporte(Integer id) {
        switch (id) {
            case 1: return "⚽"; case 2: return "🏐"; case 3: return "🏀";
            case 4: return "🤾"; case 5: return "☄️"; case 6: return "🏖️";
            case 7: return "🏊"; case 8: return "🏃"; case 9: return "🏓";
            case 10: return "♟️"; case 11: return "🏁"; case 12: return "🎮";
            default: return "🏅";
        }
    }
}