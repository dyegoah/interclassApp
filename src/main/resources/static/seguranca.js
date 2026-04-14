/* ========================================================
   ESCUDO DE SEGURANÇA FRONTEND - HIGHTECH
   Módulo de Prevenção contra XSS (Cross-Site Scripting)
======================================================== */

const HighTechSecurity = {
    /**
     * Higieniza qualquer texto recebido antes de renderizar na tela.
     * Converte tags HTML em texto inofensivo.
     */
    sanitizar: function(textoCru) {
        if (!textoCru) return '';
        
        // Garante que é uma string antes de tentar limpar
        const texto = String(textoCru);
        
        const mapa = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#x27;',
            "/": '&#x2F;',
            "`": '&#x60;',
            "=": '&#x3D;'
        };
        
        const reg = /[&<>"'/`=]/ig;
        return texto.replace(reg, (match) => (mapa[match]));
    }
};