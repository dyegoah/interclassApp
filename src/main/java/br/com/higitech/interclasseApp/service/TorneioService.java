package br.com.higitech.interclasseApp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.higitech.interclasseApp.dto.TorneioDTO;
import br.com.higitech.interclasseApp.model.Torneio;
import br.com.higitech.interclasseApp.repositories.TorneioRepository;

@Service
public class TorneioService {

    private final TorneioRepository torneioRepository;

    public TorneioService(TorneioRepository torneioRepository) {
        this.torneioRepository = torneioRepository;
    }

    public Torneio criarTorneio(TorneioDTO dto) {
        Torneio torneio = new Torneio();
        torneio.setNome(dto.nome());
        torneio.setAno(dto.ano());
        torneio.setDataInicio(dto.dataInicio());
        torneio.setDataFim(dto.dataFim());
        torneio.setAtivo(true); // Todo torneio recém-criado já entra como ativo

        return torneioRepository.save(torneio);
    }

    public List<Torneio> listarTodos() {
        return torneioRepository.findAll();
    }
}