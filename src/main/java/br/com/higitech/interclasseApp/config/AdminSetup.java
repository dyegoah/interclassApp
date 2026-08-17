package br.com.higitech.interclasseApp.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;

@Component
public class AdminSetup implements CommandLineRunner {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String emailMaster = "dyego@master.com"; 
        String senhaMaster = "Dyego@Admin2026"; 
        
        System.out.println("🛡️ Verificando conta Master Admin e 2FA...");
        
        List<Professor> todos = professorRepository.findAll();
        Professor admin = todos.stream()
            .filter(p -> p.getEmail() != null && p.getEmail().equalsIgnoreCase(emailMaster))
            .findFirst()
            .orElse(null);

        if (admin == null) {
            admin = new Professor();
            admin.setEmail(emailMaster);
            admin.setEscola("Sede Master InterclasseApp");
            System.out.println("✅ Criando nova conta Master no banco de dados...");
        } else {
            System.out.println("✅ Conta Master encontrada! Forçando atualização da senha para garantir o acesso...");
        }

        // 🔥 A MÁGICA AQUI: Força a atualização da Senha e do Nome TODA VEZ que o servidor liga!
        admin.setNome("Dyego (Master)");
        admin.setSenha(passwordEncoder.encode(senhaMaster)); 
        
        if (admin.getChave2fa() == null || admin.getChave2fa().isEmpty()) {
            admin.setChave2fa(Base32.random()); 
        }
        
        admin = professorRepository.save(admin);

        // Gera o link do QR Code
        String urlAutenticador = "otpauth://totp/InterclasseApp:Master?secret=" + admin.getChave2fa() + "&issuer=InterclasseApp";
        String urlCodificada = URLEncoder.encode(urlAutenticador, StandardCharsets.UTF_8.toString());
        String linkQrCode = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + urlCodificada;

        System.out.println("\n=================================================================");
        System.out.println("📱 GOOGLE AUTHENTICATOR (2FA) DO MASTER 📱");
        System.out.println("Opção 1: Escaneie o QR Code acessando o link seguro abaixo:");
        System.out.println("👉 " + linkQrCode);
        System.out.println("\nOpção 2: Se preferir, digite a chave manualmente no aplicativo:");
        System.out.println("Nome da Conta: InterclasseApp");
        System.out.println("Chave Secreta: " + admin.getChave2fa());
        System.out.println("=================================================================\n");
    }
}