package br.com.higitech.interclasseApp.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.dto.SumulaFinalizadaDTO;
import br.com.higitech.interclasseApp.model.Escalacao;
import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.repositories.EscalacaoRepository;
import br.com.higitech.interclasseApp.repositories.JogoRepository;

@Service
@Transactional
public class SumulaService {

    private final JogoRepository jogoRepository;
    private final EscalacaoRepository escalacaoRepository;

    public SumulaService(JogoRepository jogoRepository, EscalacaoRepository escalacaoRepository) {
        this.jogoRepository = jogoRepository;
        this.escalacaoRepository = escalacaoRepository;
    }

    public void processarSumula(Long jogoId, SumulaFinalizadaDTO dto) {
        Jogo jogoAtual = jogoRepository.findById(jogoId).orElseThrow();
        jogoAtual.setPlacarA(dto.placarA());
        jogoAtual.setPlacarB(dto.placarB());
        jogoAtual.setStatus("FINALIZADO");
        jogoRepository.save(jogoAtual);

        String vencedorNome = null;
        String equipeVencedoraLetra = null;

        try {
            String confronto = jogoAtual.getTitulo().split("\\|")[1].trim();
            String[] times = confronto.split(" x ");
            if (dto.placarA() > dto.placarB()) {
                vencedorNome = times[0].trim(); equipeVencedoraLetra = "A";
            } else if (dto.placarB() > dto.placarA()) {
                vencedorNome = times.length > 1 ? times[1].trim() : "Equipe B"; equipeVencedoraLetra = "B";
            } else return; 
        } catch(Exception e) { return; }

        String numJogoAtual = null;
        Matcher matcher = Pattern.compile("\\(Jogo (\\d+)\\)").matcher(jogoAtual.getTitulo());
        if (matcher.find()) numJogoAtual = matcher.group(1);
        if (numJogoAtual == null) return; 

        String tagBusca = "Vencedor do Jogo " + numJogoAtual;
        
        // 🔒 Inteligência Multi-Tenant: Lê o ID do dono do jogo atual
        Long profId = jogoAtual.getProfessor().getId();

        // 3. Procura a partida filtrando SOMENTE pelas partidas DESTE professor!
        List<Jogo> proximos = jogoRepository.findByProfessorId(profId).stream()
            .filter(j -> j.getModalidade() != null && j.getModalidade().getId().equals(jogoAtual.getModalidade().getId()))
            .filter(j -> j.getStatus().equals("PENDENTE") && j.getTitulo().contains(tagBusca))
            .toList();

        for (Jogo proximo : proximos) {
            String novoTitulo = proximo.getTitulo().replace(tagBusca, vencedorNome);
            proximo.setTitulo(novoTitulo);
            
            String novaEquipeLetra = "A";
            try {
                if (!novoTitulo.split("\\|")[1].trim().startsWith(vencedorNome)) novaEquipeLetra = "B";
            } catch(Exception e){}

            jogoRepository.save(proximo);

            if (proximo.getEscalacoes() != null) {
                final String nLetra = novaEquipeLetra;
                List<Escalacao> antigas = proximo.getEscalacoes().stream().filter(e -> e.getEquipe().equals(nLetra)).toList();
                escalacaoRepository.deleteAll(antigas);
            }

            final String letraVencedora = equipeVencedoraLetra;
            final String letraDestino = novaEquipeLetra;
            if (jogoAtual.getEscalacoes() != null) {
                jogoAtual.getEscalacoes().stream()
                    .filter(esc -> esc.getEquipe().equals(letraVencedora))
                    .forEach(esc -> {
                        Escalacao novaEsc = new Escalacao();
                        novaEsc.setAluno(esc.getAluno());
                        novaEsc.setJogo(proximo);
                        novaEsc.setEquipe(letraDestino);
                        novaEsc.setNumeroCamisa(esc.getNumeroCamisa());
                        escalacaoRepository.save(novaEsc);
                    });
            }
        }
    }
}