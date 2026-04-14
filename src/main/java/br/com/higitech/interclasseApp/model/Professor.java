package br.com.higitech.interclasseApp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "professores")
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

    // A senha será salva com um Hash de 60 caracteres do BCrypt
    @Column(nullable = false, length = 100) 
    private String senha;

    @Column(nullable = false)
    private String status = "ativo"; // ativo, bloqueado, pendente

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getEscola() {
		return escola;
	}

	public String getEmail() {
		return email;
	}

	public String getSenha() {
		return senha;
	}

	public String getStatus() {
		return status;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setEscola(String escola) {
		this.escola = escola;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
    
}