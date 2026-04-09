package br.com.higitech.interclasseApp.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_aluno")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String turma;

    private LocalDate dataNascimento;

    @Column(columnDefinition = "TEXT")
    private String fotoUrl;

    @Column(nullable = true)
    private String genero;
    
    @Column(nullable = false)
    private String esporte;

    @Column(nullable = false)
    private String iconeEsporte;

    // 🌟 A MÁGICA MULTI-TENANT AQUI: Quem é o dono deste aluno?
    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    // --- GETTERS E SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getEsporte() { return esporte; }
    public void setEsporte(String esporte) { this.esporte = esporte; }

    public String getIconeEsporte() { return iconeEsporte; }
    public void setIconeEsporte(String iconeEsporte) { this.iconeEsporte = iconeEsporte; }

    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }
}