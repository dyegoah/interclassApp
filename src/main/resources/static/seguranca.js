/* ========================================================
   MÓDULO DE SEGURANÇA E UTILIDADES GLOBAIS - HIGHTECH
======================================================== */

// 1. ANTI-FLICKER E PROTEÇÃO DE ROTA AUTOMÁTICA
// Este bloco roda imediatamente e bloqueia rotas para não logados.
(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const path = window.location.pathname;
    
    // Define rotas que podem ser acessadas sem login
    const isPublicRoute = path === '/' || 
                          path.includes('index.html') || 
                          path.includes('cadastro') || 
                          urlParams.has('ref'); // O "ref" é o acesso do atleta
    
    const token = localStorage.getItem('interclassToken');
    
    if (!token && !isPublicRoute) {
        window.location.replace('/index.html');
    }
	
	
	
})();

// 2. PROTEÇÃO CONTRA XSS (Higienização de Texto da API)
const HighTechSecurity = {
    sanitizar: function(textoCru) {
        if (!textoCru) return '';
        const texto = String(textoCru);
        const mapa = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#x27;', "/": '&#x2F;', "`": '&#x60;', "=": '&#x3D;' };
        return texto.replace(/[&<>"'/`=]/ig, (match) => (mapa[match]));
    }
};

// 3. UTILITÁRIOS DA INTERFACE (Elimina funções duplicadas nos HTMLs)
const Utils = {
    mostrarAlerta: function(mensagem, tipo, elementoId = 'mensagem-alerta') {
        const alerta = document.getElementById(elementoId);
        if(alerta) {
            alerta.textContent = mensagem;
            alerta.className = `alert alert-${tipo} d-block`;
            alerta.scrollIntoView({ behavior: 'smooth', block: 'end' });
        } else {
            alert(mensagem); // Fallback se o HTML não tiver a div de alerta
        }
    },
    validarEmail: function(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }
};

/* ========================================================
   4. INICIALIZAÇÃO DE COMPONENTES GLOBAIS (TOPBAR/FOOTBAR)
======================================================== */
window.initTopbar = function() {
    try {
        var p = localStorage.getItem('profNome');
        var e = localStorage.getItem('profEscola');
        var escolaEl = document.getElementById('escola-nome-top');
        var profEl = document.getElementById('nome-professor-top');

        if(p && escolaEl && profEl) {
            escolaEl.textContent = e.replace(/&quot;/g, '');
            profEl.textContent = 'Prof. ' + p.replace(/&quot;/g, '').split(' ')[0];
        } else if (escolaEl && profEl) {
            escolaEl.textContent = 'Colégio Interclass';
            profEl.textContent = 'Sessão Expirada';
        }
    } catch(err) { console.error('Falha no injetor do Topbar:', err); }
};

window.zerarTemporada = async function() {
    var conf = prompt('Tem certeza absoluta? Digite ZERAR na caixa abaixo para confirmar a destruição de todos os dados do torneio:');
    if(conf !== 'ZERAR' && conf !== 'zerar') { 
        alert('Ação cancelada de forma segura.'); 
        return; 
    }
    
    var btn = document.getElementById('btn-executar-reset');
    if(btn) { btn.innerHTML = '⏳ Varrendo Banco...'; btn.disabled = true; }
    
    try {
        var res = await fetch('/api/setup/reset', { 
            method: 'DELETE', 
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('interclassToken') } 
        });
        
        if(res.ok) {
            var t = localStorage.getItem('interclassToken');
            var pid = localStorage.getItem('profId');
            var pNome = localStorage.getItem('profNome');
            var pEscola = localStorage.getItem('profEscola');
            var isM = localStorage.getItem('isMasterAdmin');
            
            localStorage.clear();
            
            localStorage.setItem('interclassToken', t);
            localStorage.setItem('profId', pid);
            localStorage.setItem('profNome', pNome);
            localStorage.setItem('profEscola', pEscola);
            if(isM) localStorage.setItem('isMasterAdmin', isM);
            
            alert('♻️ RECOMEÇO PRONTO! O seu aplicativo foi completamente zerado. Bem-vindo à nova Temporada Interclass!');
            window.location.href = '/dashboard.html';
        } else {
            alert('Erro ao tentar limpar a base de dados. Tente novamente mais tarde.');
            if(btn) { btn.innerHTML = 'Sim, Apagar Tudo'; btn.disabled = false; }
        }
    } catch(e) {
        alert('Erro de conexão. Verifique o servidor.');
        if(btn) { btn.innerHTML = 'Sim, Apagar Tudo'; btn.disabled = false; }
    }
};

window.initFootbar = function() {
    const path = window.location.pathname;
    if(path.includes('index') || path === '/') document.getElementById('nav-arena')?.classList.add('active');
    else if(path.includes('tabelas') || path.includes('matchsheet')) document.getElementById('nav-tabelas')?.classList.add('active');
    else if(path.includes('resumo')) document.getElementById('nav-resumo')?.classList.add('active');
    else if(path.includes('alunos')) document.getElementById('nav-alunos')?.classList.add('active');
};

/* ========================================================
   5. ESCUDO ANTI-CURIOSOS (Bloqueio de F12, Ctrl+U, etc.)
======================================================== */
(function() {
    // Bloqueia o botão direito do mouse
    document.addEventListener('contextmenu', function(e) {
        e.preventDefault();
    });

    // Bloqueia atalhos de teclado comuns para desenvolvedores
    document.addEventListener('keydown', function(e) {
        // F12
        if (e.key === 'F12' || e.keyCode === 123) {
            e.preventDefault();
        }
        // Ctrl+Shift+I (Inspecionar) / Ctrl+Shift+J (Console) / Ctrl+Shift+C (Elementos)
        if (e.ctrlKey && e.shiftKey && (e.key === 'I' || e.key === 'i' || e.key === 'J' || e.key === 'j' || e.key === 'C' || e.key === 'c')) {
            e.preventDefault();
        }
        // Ctrl+U (Exibir código fonte) / Ctrl+S (Salvar página) / Ctrl+P (Imprimir)
        if (e.ctrlKey && (e.key === 'U' || e.key === 'u' || e.key === 'S' || e.key === 's' || e.key === 'P' || e.key === 'p')) {
            e.preventDefault();
        }
    });
})();



// =========================================================================
// 🔥 PROTEÇÃO CONTRA INATIVIDADE (SESSÃO DE 20 MINUTOS) 🔥
// =========================================================================

let timerInatividade;
// 20 minutos = 20 * 60 segundos * 1000 milissegundos = 1.200.000 ms
const TEMPO_MAXIMO_INATIVIDADE = 1200000; 

function resetarTimerInatividade() {
    // Se o timer já estava rodando, cancela ele
    clearTimeout(timerInatividade);
    
    // Inicia a contagem de 20 minutos novamente
    timerInatividade = setTimeout(executarLogoutPorInatividade, TEMPO_MAXIMO_INATIVIDADE); 
}

function executarLogoutPorInatividade() {
    // Só executa a expulsão se a pessoa tiver um token de login
    const token = localStorage.getItem('interclassToken');
    
    // Ignora a regra se for uma página pública (Login ou Portal do Aluno/Cadastro)
    const urlAtual = window.location.pathname.toLowerCase();
    const ehPaginaPublica = urlAtual.includes('index.html') || 
                            urlAtual.includes('cadastro-aluno') || 
                            window.location.search.includes('ref=');
    
    if (token && !ehPaginaPublica) {
        // Remove a memória de login
        localStorage.removeItem('interclassToken');
        // Você pode usar localStorage.clear() se quiser apagar TUDO, mas remover o token já bloqueia o acesso.
        
        alert("⏳ Sessão expirada por inatividade de 20 minutos. Por segurança, faça login novamente.");
        window.location.href = '/index.html';
    }
}

// Liga os "radares" de movimento assim que a página carregar
window.addEventListener('load', () => {
    const urlAtual = window.location.pathname.toLowerCase();
    const ehPaginaPublica = urlAtual.includes('index.html') || 
                            urlAtual.includes('cadastro-aluno') || 
                            window.location.search.includes('ref=');
    
    // Se NÃO for a tela de login, liga o monitoramento
    if (!ehPaginaPublica) {
        // Radares de ação (qualquer uma dessas ações zera o relógio)
        document.addEventListener('mousemove', resetarTimerInatividade);   // Mexeu o mouse
        document.addEventListener('keypress', resetarTimerInatividade);    // Digitou no teclado
        document.addEventListener('click', resetarTimerInatividade);       // Clicou
        document.addEventListener('scroll', resetarTimerInatividade);      // Rolou a página
        document.addEventListener('touchstart', resetarTimerInatividade);  // Tocou na tela (Celular)
        
        // Dá o start inicial no relógio assim que entra
        resetarTimerInatividade();
    }
});