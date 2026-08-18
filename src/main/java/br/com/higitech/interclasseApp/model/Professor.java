package br.com.higitech.interclasseApp.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "professores")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🛡️ CORREÇÃO: Removido o 'updatable = false' para o Java conseguir "curar" as contas antigas
    @Column(name = "hash_publico", unique = true)
    private String hashPublico = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String escola;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100) 
    private String senha;

    @Column(nullable = false)
    private String status = "ativo"; 

    @Column(name = "chave_2fa")
    private String chave2fa;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getHashPublico() { return hashPublico; }
    public void setHashPublico(String hashPublico) { this.hashPublico = hashPublico; }

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

    public String getChave2fa() { return chave2fa; }
    public void setChave2fa(String chave2fa) { this.chave2fa = chave2fa; }
}