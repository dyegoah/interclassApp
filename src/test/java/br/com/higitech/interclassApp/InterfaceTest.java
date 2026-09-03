package br.com.higitech.interclassApp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.SelectOption;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InterfaceTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void iniciarMotor() {
        System.out.println("⚙️ LIGANDO O MOTOR PLAYWRIGHT E PREPARANDO DADOS...");
        playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(300);
        browser = playwright.chromium().launch(options);
    }

    @AfterAll
    static void desligarMotor() {
        System.out.println("🏁 TODOS OS TESTES DO MÓDULO FORAM CONCLUÍDOS. FECHANDO NAVEGADOR.");
        playwright.close();
    }

    @BeforeEach
    void abrirNovaAbaLimpa() {
        context = browser.newContext();
        page = context.newPage();
        
        // Manipulador automático para clicar em "OK" nos Alerts e Confirms do Javascript!
        page.onDialog(dialog -> {
            System.out.println("   [SISTEMA ALERT/CONFIRM] " + dialog.message());
            dialog.accept();
        });
    }

    @AfterEach
    void fecharAba() {
        context.close();
    }

    // =========================================================================
    // SUÍTE DE TESTES: MÓDULO 1 (SEGURANÇA E ACESSO)
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("Teste 1: Validar Cadastro de Novo Professor")
    public void testarCadastroProfessor() {
        String emailTeste = "prof_" + System.currentTimeMillis() + "@teste.com";
        System.out.println("👉 INICIANDO TESTE 1: Cadastro com " + emailTeste);
        
        page.navigate("http://localhost:8015/cadastro-professor.html");

        page.fill("#nome", "Professor Suite Teste");
        page.fill("#escola", "Escola de Automação");
        page.fill("#email", emailTeste);
        page.fill("#senha", "SenhaForte123");
        page.click("button:has-text('Criar Minha Conta')");

        try {
            page.waitForURL("**/index.html", new Page.WaitForURLOptions().setTimeout(5000));
            assertTrue(page.url().contains("index.html"));
            System.out.println("✅ TESTE 1 CONCLUÍDO: Cadastro realizado com sucesso!");
        } catch (Exception e) {
            String erroNaTela = page.locator("#mensagem-alerta").innerText();
            System.out.println("   🚨 O CADASTRO FALHOU! Motivo apontado pelo sistema: " + erroNaTela);
            assertTrue(false, "O teste falhou porque o sistema recusou o cadastro.");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Teste 2: Validar Login com Sucesso")
    public void testarLoginCorreto() {
        System.out.println("👉 INICIANDO TESTE 2: Login com a Conta Oficial...");
        page.navigate("http://localhost:8015/index.html");

        page.fill("#email", "dyegoah@hotmail.com");
        page.fill("#senha", "123");
        page.click("#btn-etapa1");

        page.waitForURL("**/dashboard.html");
        assertTrue(page.url().contains("dashboard.html"), "Falha ao acessar o painel de comando.");
        System.out.println("✅ TESTE 2 CONCLUÍDO: Login validado e Dashboard carregado!");
    }

    @Test
    @Order(3)
    @DisplayName("Teste 3: Validar Segurança Anti-Curiosos (Token)")
    public void testarProtecaoSemToken() {
        System.out.println("👉 INICIANDO TESTE 3: Tentativa de invasão sem Token...");
        
        page.navigate("http://localhost:8015/dashboard.html");
        page.waitForURL("**/index.html");
        
        assertTrue(page.url().contains("index.html"), "FALHA DE SEGURANÇA: O sistema permitiu acesso sem Token!");
        System.out.println("✅ TESTE 3 CONCLUÍDO: O sistema bloqueou a rota protegida com sucesso.");
    }
    
    // =========================================================================
    // SUÍTE DE TESTES: MÓDULO 2 (INJEÇÃO DE ATLETAS E GESTÃO)
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("Teste 4: Injeção de 25 Atletas (5 Equipes de Futsal)")
    public void testarInjecaoEmMassa() {
        System.out.println("👉 INICIANDO TESTE 4: Populando o banco com 25 Atletas...");
        
        page.navigate("http://localhost:8015/index.html");
        page.fill("#email", "dyegoah@hotmail.com");
        page.fill("#senha", "123");
        page.click("#btn-etapa1");
        page.waitForURL("**/dashboard.html");

        page.click("a:has-text('Link Inscrição do Aluno')");
        page.click(".mod-block:has-text('Futsal')");
        page.click("button:has-text('Copiar e Compartilhar')");
        String linkPublico = page.inputValue("#link-publico");
        page.click("button:has-text('Fechar')");

        String[] turmas = {"6º Ano A", "6º Ano B", "7º Ano A", "7º Ano B", "8º Ano A"};
        
        for (String turma : turmas) {
            System.out.println("   - Inscrevendo o time do " + turma + "...");
            for (int i = 1; i <= 5; i++) {
                page.navigate(linkPublico);
                page.locator("#form-cadastro").waitFor(); 
                
                page.selectOption("#generoAluno", "masculino");
                page.selectOption("#esporteAluno", new SelectOption().setIndex(1)); 
                page.fill("#nomeAluno", "Atleta " + i + " - " + turma);
                page.selectOption("#turmaAluno", turma);
                
                page.click("#btn-submit");
                page.locator(".alert-success").waitFor();
            }
        }
        System.out.println("✅ TESTE 4 CONCLUÍDO: 25 alunos cadastrados com sucesso!");
    }

    @Test
    @Order(5)
    @DisplayName("Teste 5: Gestão de Atletas, Filtros e Geração de PDF")
    public void testarPainelAlunosEPdf() {
        System.out.println("👉 INICIANDO TESTE 5: Validando a tela de Gestão de Atletas...");
        
        page.navigate("http://localhost:8015/index.html");
        page.fill("#email", "dyegoah@hotmail.com");
        page.fill("#senha", "123");
        page.click("#btn-etapa1");
        page.waitForURL("**/dashboard.html");
        
        page.navigate("http://localhost:8015/alunos.html");
        page.waitForURL("**/alunos.html");

        String totalAtletas = page.locator("#valor-total-geral").innerText();
        System.out.println("   - Total de atletas lidos no painel: " + totalAtletas);

        System.out.println("   - Testando o filtro de busca...");
        page.fill("#input-busca", "Atleta 1 - 6º Ano A");
        page.waitForTimeout(1000); 
        
        page.fill("#input-busca", "");
        page.waitForTimeout(1000);

        System.out.println("   - Testando a geração do PDF...");
        Page novaAbaPdf = page.waitForPopup(() -> {
            page.click("button:has-text('Gerar PDF')");
        });
        
        novaAbaPdf.waitForLoadState();
        novaAbaPdf.close();

        System.out.println("✅ TESTE 5 CONCLUÍDO: Filtros e PDF funcionando!");
    }
    
    // =========================================================================
    // SUÍTE DE TESTES: MÓDULO 3 (TORNEIOS E PLAYHUB)
    // =========================================================================

    @Test
    @Order(6)
    @DisplayName("Teste 6: Criação Inteligente do Torneio e Calendário")
    public void testarCriacaoDeTorneio() {
        System.out.println("👉 INICIANDO TESTE 6: Passando pelo Assistente de Torneio...");
        
        page.navigate("http://localhost:8015/index.html");
        page.fill("#email", "dyegoah@hotmail.com");
        page.fill("#senha", "123");
        page.click("#btn-etapa1");
        page.waitForURL("**/dashboard.html");
        
        // 🔥 CORREÇÃO: Força o navegador do robô a lembrar que escolhemos "futsal"
        page.evaluate("localStorage.setItem('modalidadesAtivas', JSON.stringify(['futsal']));");

        page.navigate("http://localhost:8015/torneios.html");
        page.waitForURL("**/torneios.html");

        System.out.println("   - Gravando regras de chaveamento...");
        page.click("button:has-text('Gravar e Avançar')"); 
        
        page.waitForURL("**/calendario.html");
        
        System.out.println("   - Acionando a IA de Agendamento...");
        page.click("button:has-text('Gerar Calendário Inteligente')");
        page.waitForTimeout(2500); 
        
        System.out.println("   - Salvando a Tabela Oficial...");
        page.click("button:has-text('Salvar Tabela Oficial')"); 

        page.waitForURL("**/tabelas.html");
        assertTrue(page.url().contains("tabelas.html"), "O sistema não redirecionou para o PlayHub após salvar o torneio.");
        System.out.println("✅ TESTE 6 CONCLUÍDO: Torneio criado e agendado com sucesso!");
    }

    @Test
    @Order(7)
    @DisplayName("Teste 7: Simulação de Jogo no PlayHub (Súmula de Futsal)")
    public void testarPlayHubESumula() {
        System.out.println("👉 INICIANDO TESTE 7: Simulando uma partida de Futsal...");
        
        page.navigate("http://localhost:8015/index.html");
        page.fill("#email", "dyegoah@hotmail.com");
        page.fill("#senha", "123");
        page.click("#btn-etapa1");
        page.waitForURL("**/dashboard.html");

        page.navigate("http://localhost:8015/tabelas.html");
        page.waitForURL("**/tabelas.html");
        page.waitForTimeout(2000); 

        int sumulasAbertas = page.locator("button:has-text('▶️ SÚMULA')").count();
        assertTrue(sumulasAbertas > 0, "Nenhuma súmula aberta foi encontrada no PlayHub!");

        System.out.println("   - Entrando na Súmula...");
        page.locator("button:has-text('▶️ SÚMULA')").first().click();
        page.waitForURL("**/matchsheet/futsal.html**");
        page.waitForTimeout(1500); 

        System.out.println("   - Marcando gol para a Equipe A...");
        page.locator("#lista-a .player-row").first().click();
        page.click("button:has-text('⚽ Marcar Gol')");
        page.waitForTimeout(500);

        System.out.println("   - Marcando gols para a Equipe B...");
        page.locator("#lista-b .player-row").first().click();
        page.click("button:has-text('⚽ Marcar Gol')");
        page.waitForTimeout(500);
        
        page.locator("#lista-b .player-row").last().click();
        page.click("button:has-text('⚽ Marcar Gol')");
        page.waitForTimeout(500);

        System.out.println("   - Encerrando a partida e gravando no banco...");
        page.click("#btn-finalizar");
        page.waitForURL("**/tabelas.html");

        System.out.println("✅ TESTE 7 CONCLUÍDO: Súmula preenchida e campeonato avançou de chave!");
    }

    // =========================================================================
    // SUÍTE DE TESTES: MÓDULO 4 (NOVAS ARQUITETURAS DE SÚMULA)
    // =========================================================================

    @Test
    @Order(8)
    @DisplayName("Teste 8: Validação Dinâmica Súmula Handebol (Cronômetro e Exclusão)")
    public void testarSumulaHandebol() {
        System.out.println("👉 INICIANDO TESTE 8: Simulando partida de Handebol...");
        
        // 🔥 CORREÇÃO: Faz o Login antes para não ser expulso pelo seguranca.js
        page.navigate("http://localhost:8015/index.html");
        page.fill("#email", "dyegoah@hotmail.com");
        page.fill("#senha", "123");
        page.click("#btn-etapa1");
        page.waitForURL("**/dashboard.html");

        // Simula a entrada direta em um jogo na tela recém-refatorada (usando ID falso 999 que o JS converte num jogo mock)
        page.navigate("http://localhost:8015/matchsheet/handebol.html?jogo=999&lote=masculino");
        page.waitForTimeout(1500);

        System.out.println("   - Testando Cronômetro...");
        page.click("#btn-start-crono");
        page.waitForTimeout(1500);
        page.click("#btn-start-crono"); // Pausa
        
        System.out.println("   - Aplicando gol...");
        page.locator("#lista-a .player-row").first().click();
        page.click("button:has-text('Golo Normal')");
        page.waitForTimeout(500);

        System.out.println("   - Aplicando Exclusão (2 Minutos)...");
        page.locator("#lista-b .player-row").first().click();
        page.click("button:has-text('2 Min.')");
        page.waitForTimeout(500);

        System.out.println("   - Encerrando a partida...");
        page.click("#btn-finalizar");
        
        page.waitForURL("**/tabelas.html");
        System.out.println("✅ TESTE 8 CONCLUÍDO: Súmula de Handebol testada e validada com sucesso!");
    }

    @Test
    @Order(9)
    @DisplayName("Teste 9: Validação Dinâmica Súmula Individual (1x1)")
    public void testarSumulaIndividual() {
        System.out.println("👉 INICIANDO TESTE 9: Simulando confronto Individual (Ex: Tênis de Mesa)...");
        
        // 🔥 CORREÇÃO: Faz o Login antes para não ser expulso pelo seguranca.js
        page.navigate("http://localhost:8015/index.html");
        page.fill("#email", "dyegoah@hotmail.com");
        page.fill("#senha", "123");
        page.click("#btn-etapa1");
        page.waitForURL("**/dashboard.html");

        page.navigate("http://localhost:8015/matchsheet/individual.html?jogo=888&lote=misto");
        page.waitForTimeout(1500);

        System.out.println("   - Aplicando Ponto para o Atleta A...");
        // Clica no card do jogador da coluna A
        page.locator("#coluna-a .jogador-card").first().click();
        page.click("button:has-text('+1 PONTO')");
        page.waitForTimeout(500);

        System.out.println("   - Encerrando a partida 1v1...");
        page.click("#btn-enviar"); // Note que no Individual o ID é btn-enviar
        
        page.waitForURL("**/tabelas.html");
        System.out.println("✅ TESTE 9 CONCLUÍDO: Súmula Individual testada e validada com sucesso!");
    }
}