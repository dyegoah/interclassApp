package br.com.higitech.interclasseApp.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_aluno")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
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
    private String fotoUrl; // URL retornada pelo Cloudinary após upload

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modalidade_id", nullable = false)
    private Modalidade modalidade;

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getTurma() {
		return turma;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public String getFotoUrl() {
		return fotoUrl;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setTurma(String turma) {
		this.turma = turma;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public void setFotoUrl(String fotoUrl) {
		this.fotoUrl = fotoUrl;
	}

	public void setModalidade(Modalidade modalidade) {
		this.modalidade = modalidade;
	}
    
    
}