package br.com.higitech.interclasseApp.service;

import java.util.List;
import java.util.stream.Collectors;

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

    public SetupService(TorneioRepository torneioRepository, LoteRepository loteRepository, 
                        ModalidadeRepository modalidadeRepository, ClassificacaoRepository classificacaoRepository,
                        JogoRepository jogoRepository, AlunoRepository alunoRepository) {
        this.torneioRepository = torneioRepository;
        this.loteRepository = loteRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.classificacaoRepository = classificacaoRepository;
        this.jogoRepository = jogoRepository;
        this.alunoRepository = alunoRepository;
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

    // 🔴 O CÓDIGO DA FAXINA PERFEITA E ANTI-ERRO 500
    public void resetarTudo(Professor profLogado) {
        Long idProf = profLogado.getId();

        // 1. Busca todos os dados no banco e filtra APENAS os do professor que apertou o botão
        List<Classificacao> classificacoes = classificacaoRepository.findAll().stream()
            .filter(c -> c.getModalidade() != null && c.getModalidade().getLote() != null && c.getModalidade().getLote().getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        List<Jogo> jogos = jogoRepository.findAll().stream()
            .filter(j -> j.getProfessor() != null && j.getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        List<Aluno> alunos = alunoRepository.findAll().stream()
            .filter(a -> a.getProfessor() != null && a.getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        List<Modalidade> modalidades = modalidadeRepository.findAll().stream()
            .filter(m -> m.getLote() != null && m.getLote().getProfessor() != null && m.getLote().getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        List<Lote> lotes = loteRepository.findAll().stream()
            .filter(l -> l.getProfessor() != null && l.getProfessor().getId().equals(idProf))
            .collect(Collectors.toList());

        // 2. ORDEM DE DELEÇÃO ESTRITA: De baixo para cima para satisfazer o PostgreSQL!
        if (!classificacoes.isEmpty()) classificacaoRepository.deleteAll(classificacoes);
        
        // Ao deletar os jogos, o Java deleta as Escalações automaticamente em cascata
        if (!jogos.isEmpty()) jogoRepository.deleteAll(jogos); 
        
        // Agora que as Escalações sumiram, os alunos estão desamarrados e podem ser deletados
        if (!alunos.isEmpty()) alunoRepository.deleteAll(alunos); 
        
        if (!modalidades.isEmpty()) modalidadeRepository.deleteAll(modalidades);
        if (!lotes.isEmpty()) loteRepository.deleteAll(lotes);
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