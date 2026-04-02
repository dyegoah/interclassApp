package br.com.higitech.interclasseApp.dto;

import java.util.List;

// Mapeia o JSON exato que o nosso JS "calendarioOficial" gera
public record CalendarioSaveDTO(
    List<DiaDTO> dias,
    List<JogoDTO> jogos
) {}