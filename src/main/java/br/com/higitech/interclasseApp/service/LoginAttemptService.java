package br.com.higitech.interclasseApp.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.higitech.interclasseApp.model.LogAcesso;
import br.com.higitech.interclasseApp.repositories.LogAcessoRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginAttemptService {
    
    private final int MAXIMO_TENTATIVAS = 5;
    private ConcurrentHashMap<String, Integer> tentativas = new ConcurrentHashMap<>();

    // 🔥 CORREÇÃO 1: Injeção do repositório para o Java saber onde salvar 🔥
    @Autowired
    private LogAcessoRepository logAcessoRepository;

    public void loginComSucesso(String email) {
        tentativas.remove(email);
    }

    public void loginFalhou(String email) {
        int erros = tentativas.getOrDefault(email, 0);
        erros++;
        tentativas.put(email, erros);
    }

    public boolean estaBloqueado(String email) {
        return tentativas.getOrDefault(email, 0) >= MAXIMO_TENTATIVAS;
    }
    
    public void registrarLog(String email, String status, HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();

        // Chamada na API gratuita para pegar a cidade do IP
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://ip-api.com/json/" + ip + "?fields=city,regionName,country,isp";
        
        String local = "Local Desconhecido";
        try {
            Map<String, String> geo = restTemplate.getForObject(url, Map.class);
            if (geo != null && geo.get("city") != null) {
                local = geo.get("city") + ", " + geo.get("regionName") + " (" + geo.get("isp") + ")";
            }
        } catch (Exception e) {
            System.out.println("Aviso: Falha ao buscar geolocalização do IP " + ip);
        }

        // Criando e salvando o log
        LogAcesso log = new LogAcesso();
        log.setEmailTentado(email);
        log.setIpOrigem(ip);
        log.setLocalizacaoIsp(local);
        log.setStatus(status);
        
        logAcessoRepository.save(log);
    }
}