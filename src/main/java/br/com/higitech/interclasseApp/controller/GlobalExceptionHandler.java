package br.com.higitech.interclasseApp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura qualquer exceção genérica não tratada na aplicação
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        // O erro real continua sendo exibido no terminal do servidor para você debugar
        ex.printStackTrace();
        
        // O cliente recebe apenas uma resposta genérica e segura, ocultando o código e o SQL
        Map<String, String> response = new HashMap<>();
        response.put("erro", "Ocorreu um erro interno no servidor. Tente novamente mais tarde.");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Trata envios de dados com formato incorreto ocultando detalhes técnicos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("erro", "Os dados fornecidos são inválidos ou estão em um formato incorreto.");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}