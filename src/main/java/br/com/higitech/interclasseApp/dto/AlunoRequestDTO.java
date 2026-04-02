package br.com.higitech.interclasseApp.dto;

import java.time.LocalDate;

// Recebe os dados do frontend quando o aluno se cadastra
public record AlunoRequestDTO(
    String nome, 
    String turma, 
    LocalDate dataNascimento, 
    Long modalidadeId, 
    String fotoUrl // O link que virá do Cloudinary
) {}