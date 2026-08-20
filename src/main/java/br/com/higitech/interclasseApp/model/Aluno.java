package br.com.higitech.interclasseApp.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Column(name = "hash_publico", unique = true, updatable = false)
    private String hashPublico = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String nome;

    @Column
    private String turma;

    // 🚀 AJUSTE: Permite textos gigantes para armazenar o Upload da imagem em Base64
    @Column(columnDefinition = "TEXT")
    private String fotoUrl;

    @Column
    private String esporte;

    @Column
    private String iconeEsporte;

    @Column
    private String genero;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getHashPublico() { return hashPublico; }
    public void setHashPublico(String hashPublico) { this.hashPublico = hashPublico; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public String getEsporte() { return esporte; }
    public void setEsporte(String esporte) { this.esporte = esporte; }
    public String getIconeEsporte() { return iconeEsporte; }
    public void setIconeEsporte(String iconeEsporte) { this.iconeEsporte = iconeEsporte; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }
}