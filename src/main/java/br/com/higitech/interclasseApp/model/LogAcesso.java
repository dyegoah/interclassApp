package br.com.higitech.interclasseApp.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "logs_seguranca")
public class LogAcesso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Configurado para pegar a hora oficial do Brasil (BRT) automaticamente
    private LocalDateTime dataHora = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    
    private String emailTentado;
    private String ipOrigem;
    private String localizacaoIsp; 
    private String status;

    // 🔥 CORREÇÃO 2: Getters e Setters explicitamente declarados 🔥

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getEmailTentado() {
        return emailTentado;
    }

    public void setEmailTentado(String emailTentado) {
        this.emailTentado = emailTentado;
    }

    public String getIpOrigem() {
        return ipOrigem;
    }

    public void setIpOrigem(String ipOrigem) {
        this.ipOrigem = ipOrigem;
    }

    public String getLocalizacaoIsp() {
        return localizacaoIsp;
    }

    public void setLocalizacaoIsp(String localizacaoIsp) {
        this.localizacaoIsp = localizacaoIsp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}