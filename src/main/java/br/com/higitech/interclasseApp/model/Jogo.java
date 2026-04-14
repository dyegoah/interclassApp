package br.com.higitech.interclasseApp.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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

    private LocalDate dataJogo;
    private LocalTime horario;
    private String quadra;
    private String genero;
    private String titulo;

    private Long equipeAId;
    private String equipeANome;
    private Long equipeBId;
    private String equipeBNome;

    private Integer placarA = 0;
    private Integer placarB = 0;
    private String status = "PENDENTE";

    // 📋 A sua ligação original com a Modalidade
    @ManyToOne
    @JoinColumn(name = "modalidade_id")
    private Modalidade modalidade;

    // 📋 A sua ligação original com a Escalação
    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Escalacao> escalacoes;

    // 🔒 SEGURANÇA: A única coisa nova (Garante o Isolamento de Dados)
    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    // ================= GETTERS E SETTERS =================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getDataJogo() { return dataJogo; }
    public void setDataJogo(LocalDate dataJogo) { this.dataJogo = dataJogo; }
    
    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }
    
    public String getQuadra() { return quadra; }
    public void setQuadra(String quadra) { this.quadra = quadra; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
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
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Modalidade getModalidade() { return modalidade; }
    public void setModalidade(Modalidade modalidade) { this.modalidade = modalidade; }
    
    public List<Escalacao> getEscalacoes() { return escalacoes; }
    public void setEscalacoes(List<Escalacao> escalacoes) { this.escalacoes = escalacoes; }
    
    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }
}