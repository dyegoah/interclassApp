package br.com.higitech.interclasseApp.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore; // 🚀 IMPORTAÇÃO

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "jogos")
public class Jogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "modalidade_id")
    private Modalidade modalidade;

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private String quadra;

    @Column(name = "dia_id")
    private LocalDate dataJogo; 

    @Column(name = "hora")
    private LocalTime horario; 

    @Column(nullable = false)
    private String status;

    @Column(name = "equipe_a_id")
    private Long equipeAId;

    @Column(name = "equipe_a_nome")
    private String equipeANome;

    @Column(name = "equipe_b_id")
    private Long equipeBId;

    @Column(name = "equipe_b_nome")
    private String equipeBNome;

    @Column(name = "placar_a")
    private Integer placarA = 0;

    @Column(name = "placar_b")
    private Integer placarB = 0;
    
    @Column
    private String esporte;

    @Column
    private String icone;

    // 🛡️ CORTA O VAZAMENTO: O Jogo não vai mais "puxar" o Professor inteiro pro Frontend
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @JsonIgnore
    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Escalacao> escalacoes;

    // Getters e Setters Omitidos para poupar espaço, mas mantenha todos os seus atuais!
    // Você não precisa mudar os Getters e Setters, APENAS adicionar o @JsonIgnore no professor e escalacoes
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getTitulo() { return titulo; }
	public void setTitulo(String titulo) { this.titulo = titulo; }
	public Modalidade getModalidade() { return modalidade; }
	public void setModalidade(Modalidade modalidade) { this.modalidade = modalidade; }
	public String getGenero() { return genero; }
	public void setGenero(String genero) { this.genero = genero; }
	public String getQuadra() { return quadra; }
	public void setQuadra(String quadra) { this.quadra = quadra; }
	public LocalDate getDataJogo() { return dataJogo; }
	public void setDataJogo(LocalDate dataJogo) { this.dataJogo = dataJogo; }
	public LocalTime getHorario() { return horario; }
	public void setHorario(LocalTime horario) { this.horario = horario; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public Long getEquipeAId() { return equipeAId; }
	public void setEquipeAId(Long equipeAId) { this.equipeAId = equipeAId; }
	public String getEquipeANome() { return equipeANome; }
	public void setEquipeANome(String equipeANome) { this.equipeANome = equipeANome; }
	public Long getEquipeBId() { return equipeBId; }
	public void setEquipeBId(Long equipeBId) { this.equipeBId = equipeBId; }
	public String getEquipeBNome() { return equipeBNome; }
	public void setEquipeBNome(String equipeBNome) { this.equipeBNome = equipeBNome; }
	public Integer getPlacarA() { return placarA; }
	public void setPlacarA(Integer placarA) { this.placarA = placarA; }
	public Integer getPlacarB() { return placarB; }
	public void setPlacarB(Integer placarB) { this.placarB = placarB; }
	public Professor getProfessor() { return professor; }
	public void setProfessor(Professor professor) { this.professor = professor; }
	public List<Escalacao> getEscalacoes() { return escalacoes; }
	public void setEscalacoes(List<Escalacao> escalacoes) { this.escalacoes = escalacoes; }
}