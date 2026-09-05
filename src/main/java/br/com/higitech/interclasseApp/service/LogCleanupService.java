package br.com.higitech.interclasseApp.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.higitech.interclasseApp.repositories.LogAcessoRepository;

@Service
public class LogCleanupService {

    @Autowired
    private LogAcessoRepository logAcessoRepository;

    // 🔥 CRON JOB: Roda todos os dias, pontualmente às 03:00 da manhã
    @Scheduled(cron = "0 0 3 * * ?")
    public void limparLogsAntigos() {
        
        // 1. Calcula qual era a data exata de 6 meses atrás (Regra do Marco Civil)
        LocalDateTime limite = LocalDateTime.now().minusMonths(6);
        
        // 2. Aciona o repositório para jogar o lixo fora
        logAcessoRepository.deleteByDataHoraBefore(limite);
        
        System.out.println("🧹 [SEGURANÇA] Limpeza automática de logs antigos (mais de 6 meses) concluída com sucesso!");
    }
}