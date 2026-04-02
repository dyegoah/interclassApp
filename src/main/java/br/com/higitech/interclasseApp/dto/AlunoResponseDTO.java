package br.com.higitech.interclasseApp.dto;

// Envia os dados do backend para a grelha de monitoramento do professor
public record AlunoResponseDTO(
    Long id, 
    String nome, 
    String turma, 
    String esporte, 
    String icone, 
    String cor, 
    String fotoUrl
) {}