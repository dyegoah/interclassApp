package br.com.higitech.interclasseApp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_professor")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String escola;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    // ==========================================
    // CAMPOS DE GESTÃO SAAS (SUPER ADMIN)
    // ==========================================
    
    // Status pode ser: "ativo", "atrasado" ou "bloqueado"
    private String status = "ativo"; 
    
    @Column(updatable = false)
    private LocalDate dataCadastro;

    // Esta anotação diz ao Java para preencher a data de hoje automaticamente 
    // na exata fração de segundo em que o professor criar a conta!
    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDate.now();
    }

    // ==========================================
    // GETTERS E SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEscola() { return escola; }
    public void setEscola(String escola) { this.escola = escola; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
}