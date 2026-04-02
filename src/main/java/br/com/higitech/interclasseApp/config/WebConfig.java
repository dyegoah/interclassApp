package br.com.higitech.interclasseApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Mapeia as URLs limpas (que você digita no navegador) diretamente para os arquivos físicos estáticos
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/alunos").setViewName("forward:/alunos.html");
        registry.addViewController("/cadastro-aluno").setViewName("forward:/cadastro-aluno.html");
        
        // Mapeamento das pastas de Setup
        registry.addViewController("/setup/modalidades").setViewName("forward:/setup/modalidades.html");
        registry.addViewController("/setup/torneios").setViewName("forward:/setup/torneios.html");
        registry.addViewController("/setup/calendario").setViewName("forward:/setup/calendario.html");
    }
}