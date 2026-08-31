package br.com.higitech.interclasseApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 🔥 ALTERAÇÃO AQUI: Removemos o "*" e colocamos apenas os SEUS sites
                .allowedOrigins(
                    "http://localhost:8080",      // Para testes locais
                    "http://localhost:5500",      // Caso use Live Server do VSCode
                    "https://seu-site-oficial.com.br", // URL final do seu frontend em produção
                    "https://interclassapp.onrender.com/index.html" // URL do Render
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                .allowedHeaders("*")
                .allowCredentials(true); // Agora está seguro deixar as credenciais passarem!
    }
}