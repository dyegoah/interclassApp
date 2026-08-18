package br.com.higitech.interclasseApp.model;

import jakarta.persistence.*;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore; // 🚀 IMPORTAÇÃO

@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hash_publico", unique = true, updatable = false)
    private String hashPublico = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String nome;

    @Column
    private String turma;

    // 🛡️ CORTA O VAZAMENTO E DEIXA A LISTA MAIS RÁPIDA
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHashPublico() { return hashPublico; }
    public void setHashPublico(String hashPublico) { this.hashPublico = hashPublico; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }
}