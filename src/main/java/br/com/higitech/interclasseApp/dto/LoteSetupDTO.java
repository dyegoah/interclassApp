package br.com.higitech.interclasseApp.dto;

import java.util.List;

public record LoteSetupDTO(
    String genero, 
    List<EsporteSetupDTO> esportes // <-- Aqui estava ModalidadeSetupDTO no seu código
) {}