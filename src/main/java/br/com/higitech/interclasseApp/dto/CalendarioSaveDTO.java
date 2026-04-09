package br.com.higitech.interclasseApp.dto;

import java.util.List;

public record CalendarioSaveDTO(
    String genero, // <-- A CORREÇÃO ESTÁ AQUI
    List<Object> dias,
    List<JogoSaveDTO> jogos
) {}