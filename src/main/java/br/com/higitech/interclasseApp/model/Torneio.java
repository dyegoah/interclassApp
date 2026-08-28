package br.com.higitech.interclasseApp.model;

import java.time.Year;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_torneio")
public class Torneio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String status;
    
    private Integer ano = Year.now().getValue();
    private Boolean ativo = true;

    // 🔥 CORREÇÃO: A coluna "nome" que o PostgreSQL está exigindo
    private String nome = "Torneio Oficial";

    // ==========================
    // GETTERS E SETTERS
    // ==========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}