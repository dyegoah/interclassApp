package br.com.higitech.interclasseApp.config;

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
            admin.setNome("Dyego (Master)");
            admin.setEmail(emailMaster);
            admin.setSenha(passwordEncoder.encode(senhaMaster)); 
            admin.setEscola("Sede Master InterclasseApp");
            admin.setChave2fa(Base32.random()); // 🔥 Gera a chave secreta do Google Auth
            
            admin = professorRepository.save(admin);
            System.out.println("✅ Conta Master e Chave 2FA criadas com sucesso!");
        } else if (admin.getChave2fa() == null || admin.getChave2fa().isEmpty()) {
            admin.setChave2fa(Base32.random()); // 🔥 Adiciona a chave caso a conta já exista
            admin = professorRepository.save(admin);
            System.out.println("✅ Nova Chave 2FA injetada na conta Master!");
        }

        // 📱 IMPRIME O LINK DO QR CODE NO CONSOLE
        System.out.println("\n=================================================================");
        System.out.println("📱 GOOGLE AUTHENTICATOR (2FA) DO MASTER 📱");
        System.out.println("Abra o aplicativo no seu celular, clique em '+' e escaneie o QR Code acessando o link abaixo:");
        System.out.println("👉 https://chart.googleapis.com/chart?chs=250x250&chld=M|0&cht=qr&chl=otpauth://totp/InterclasseApp:Master?secret=" + admin.getChave2fa() + "&issuer=InterclasseApp");
        System.out.println("=================================================================\n");
    }
}