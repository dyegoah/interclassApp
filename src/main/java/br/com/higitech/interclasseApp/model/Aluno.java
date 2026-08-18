package br.com.higitech.interclasseApp.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🛡️ Hash Público do Aluno
    @Column(name = "hash_publico", unique = true, updatable = false)
    private String hashPublico = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String nome;

    @Column
    private String turma;

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