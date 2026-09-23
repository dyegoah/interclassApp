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
    private final long TEMPO_BLOQUEIO_MS = 15 * 60 * 1000; // 15 minutos

    // Mapas em memória para controlo por IP
    private ConcurrentHashMap<String, Integer> tentativas = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> bloqueioTemporario = new ConcurrentHashMap<>();

    @Autowired
    private LogAcessoRepository logAcessoRepository;

    // Extrai o IP real, contornando balanceadores de carga como o Render
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    public void loginComSucesso(HttpServletRequest request) {
        String ip = getClientIp(request);
        tentativas.remove(ip);
        bloqueioTemporario.remove(ip);
    }

    public void loginFalhou(HttpServletRequest request) {
        String ip = getClientIp(request);
        int erros = tentativas.getOrDefault(ip, 0) + 1;
        
        if (erros >= MAXIMO_TENTATIVAS) {
            bloqueioTemporario.put(ip, System.currentTimeMillis() + TEMPO_BLOQUEIO_MS);
        } else {
            tentativas.put(ip, erros);
        }
    }

    public boolean estaBloqueado(HttpServletRequest request) {
        String ip = getClientIp(request);
        if (bloqueioTemporario.containsKey(ip)) {
            if (System.currentTimeMillis() < bloqueioTemporario.get(ip)) {
                return true; // Ainda bloqueado
            } else {
                // Tempo de bloqueio expirou
                bloqueioTemporario.remove(ip);
                tentativas.remove(ip);
                return false;
            }
        }
        return false;
    }
    
    public void registrarLog(String email, String status, HttpServletRequest request) {
        String ip = getClientIp(request);

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

        LogAcesso log = new LogAcesso();
        log.setEmailTentado(email);
        log.setIpOrigem(ip);
        log.setLocalizacaoIsp(local);
        log.setStatus(status);
        
        logAcessoRepository.save(log);
    }
}