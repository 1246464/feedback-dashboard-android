package com.example.visualizadorapp.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testes para FiltroVisibilidadeCardapio
 * 
 * Valida que as regras de visibilidade de cardápios funcionam corretamente
 */
public class FiltroVisibilidadeCardapioTest {
    
    // ===== TESTES: TURNO MANHA =====
    
    @Test
    public void testManha_PoDeVerTARDE() {
        // MANHA vê cardápio TARDE de qualquer dia
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "06:00-18:00",                // horarioUsuario
            "TARDE",                      // turnoCardapio
            FiltroVisibilidadeCardapio.getDataHoje(),  // dataCardapio (hoje)
            null                          // diasTrabalho (não aplicável)
        );
        assertTrue("MANHA deve ver TARDE de hoje", resultado);
    }
    
    @Test
    public void testManha_NaoPoDeVerNOITE() {
        // MANHA NÃO vê cardápio NOITE
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "06:00-18:00",
            "NOITE",
            FiltroVisibilidadeCardapio.getDataHoje(),
            null
        );
        assertFalse("MANHA não deve ver NOITE", resultado);
    }
    
    @Test
    public void testManha_PoDeVerTARDE_Futuro() {
        // MANHA vê TARDE de dias futuros
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "06:00-18:00",
            "TARDE",
            FiltroVisibilidadeCardapio.getDataFutura(3),  // 3 dias à frente
            null
        );
        assertTrue("MANHA deve ver TARDE de dia futuro", resultado);
    }
    
    @Test
    public void testManha_NaoPoDeVerTARDE_Passado() {
        // MANHA não vê TARDE do passado
        String dataPassado = FiltroVisibilidadeCardapio.getDataFutura(-1); // ontem
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "06:00-18:00",
            "TARDE",
            dataPassado,
            null
        );
        assertFalse("MANHA não deve ver TARDE do passado", resultado);
    }
    
    // ===== TESTES: TURNO NOITE =====
    
    @Test
    public void testNoite_PoDeVerNOITE() {
        // NOITE vê cardápio NOITE
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "18:00-06:00",
            "NOITE",
            FiltroVisibilidadeCardapio.getDataHoje(),
            null
        );
        assertTrue("NOITE deve ver NOITE", resultado);
    }
    
    @Test
    public void testNoite_NaoPoDeVerTARDE() {
        // NOITE NÃO vê cardápio TARDE
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "18:00-06:00",
            "TARDE",
            FiltroVisibilidadeCardapio.getDataHoje(),
            null
        );
        assertFalse("NOITE não deve ver TARDE", resultado);
    }
    
    @Test
    public void testNoite_PoDeVerNOITE_Futuro() {
        // NOITE vê NOITE de dias futuros
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "18:00-06:00",
            "NOITE",
            FiltroVisibilidadeCardapio.getDataFutura(5),
            null
        );
        assertTrue("NOITE deve ver NOITE de dia futuro", resultado);
    }
    
    @Test
    public void testNoite_NaoPoDeVerNOITE_Passado() {
        // NOITE não vê NOITE do passado
        String dataPassado = FiltroVisibilidadeCardapio.getDataFutura(-2);
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "18:00-06:00",
            "NOITE",
            dataPassado,
            null
        );
        assertFalse("NOITE não deve ver NOITE do passado", resultado);
    }
    
    // ===== TESTES: TURNO 5X2 =====
    
    @Test
    public void testCincoXDois_NaoPoDeVerNOITE() {
        // 5X2 nunca vê NOITE
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "07:00-17:00",
            "NOITE",
            FiltroVisibilidadeCardapio.getDataHoje(),
            FiltroVisibilidadeCardapio.getDias5X2Padrao()
        );
        assertFalse("5X2 não deve ver NOITE", resultado);
    }
    
    @Test
    public void testCincoXDois_PoDeVerTARDE_DiaDeTrabalho() {
        // 5X2 vê TARDE só em dias de trabalho
        // Precisamos pegar um dia que seja segunda a sexta
        
        // Encontrar próxima segunda-feira
        String proximaSegunda = encontrarProximaDiaSemanax(1); // 1 = segunda
        
        int[] diasTrabalho5x2 = {1, 2, 3, 4, 5}; // seg a sex
        
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "5X2",
            "07:00-17:00",
            "TARDE",
            proximaSegunda,
            diasTrabalho5x2
        );
        assertTrue("5X2 deve ver TARDE em dia de trabalho", resultado);
    }
    
    @Test
    public void testCincoXDois_NaoPoDeVerTARDE_DiaQueNaoTrabalha() {
        // 5X2 não vê TARDE em domingo/sábado
        
        // Encontrar próximo domingo
        String proximoDomingo = encontrarProximaDiaSemanax(0); // 0 = domingo
        
        int[] diasTrabalho5x2 = {1, 2, 3, 4, 5}; // seg a sex
        
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "5X2",
            "07:00-17:00",
            "TARDE",
            proximoDomingo,
            diasTrabalho5x2
        );
        assertFalse("5X2 não deve ver TARDE em dia que não trabalha", resultado);
    }
    
    @Test
    public void testCincoXDois_NaoPoDeVerTARDE_MuitoLonge() {
        // 5X2 não vê TARDE muito longe (além de 7 dias)
        String dataLonge = FiltroVisibilidadeCardapio.getDataFutura(8); // 8 dias
        
        int[] diasTrabalho5x2 = {1, 2, 3, 4, 5};
        
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "5X2",
            "07:00-17:00",
            "TARDE",
            dataLonge,
            diasTrabalho5x2
        );
        assertFalse("5X2 não deve ver TARDE muito longe (>7 dias)", resultado);
    }
    
    @Test
    public void testCincoXDois_PoDeVerTARDE_Ate7Dias() {
        // 5X2 vê TARDE em dias de trabalho até 7 dias
        String data7Dias = FiltroVisibilidadeCardapio.getDataFutura(7);
        
        // Calcular qual dia da semana será
        int diaSemana = obterDiaSemana(data7Dias);
        
        // Se for dia de semana (seg-sex)
        if (diaSemana >= 1 && diaSemana <= 5) {
            int[] diasTrabalho5x2 = {1, 2, 3, 4, 5};
            
            boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
                "5X2",
                "07:00-17:00",
                "TARDE",
                data7Dias,
                diasTrabalho5x2
            );
            assertTrue("5X2 deve ver TARDE em dia de trabalho (até 7 dias)", resultado);
        }
    }
    
    // ===== TESTES: MÉTODO getDiasSimDiaNao =====
    
    @Test
    public void testDiasSimDiaNao_TamanhoCorreto() {
        int[] dias = FiltroVisibilidadeCardapio.getDiasSimDiaNao(
            FiltroVisibilidadeCardapio.getDataHoje(),
            7
        );
        
        assertEquals("Deve retornar 7 dias", 7, dias.length);
    }
    
    // ===== TESTES: MÉTODO getDescricaoRegras =====
    
    @Test
    public void testDescricaoRegras_MANHA() {
        String desc = FiltroVisibilidadeCardapio.getDescricaoRegras("MANHA");
        assertTrue("Deve mencionar TARDE", desc.contains("TARDE"));
        assertTrue("Deve mencionar NOITE", desc.contains("NOITE"));
    }
    
    @Test
    public void testDescricaoRegras_NOITE() {
        String desc = FiltroVisibilidadeCardapio.getDescricaoRegras("NOITE");
        assertTrue("Deve mencionar NOITE", desc.contains("NOITE"));
        assertTrue("Deve mencionar TARDE", desc.contains("TARDE"));
    }
    
    @Test
    public void testDescricaoRegras_5X2() {
        String desc = FiltroVisibilidadeCardapio.getDescricaoRegras("5X2");
        assertTrue("Deve mencionar dias de trabalho", desc.contains("dias que trabalha"));
        assertTrue("Deve mencionar 7 dias", desc.contains("7"));
    }
    
    // ===== TESTES: MÉTODO getDias5X2Padrao =====
    
    @Test
    public void testDias5X2Padrao_Quantidade() {
        int[] dias = FiltroVisibilidadeCardapio.getDias5X2Padrao();
        assertEquals("5X2 padrão é 5 dias", 5, dias.length);
    }
    
    @Test
    public void testDias5X2Padrao_Valores() {
        int[] dias = FiltroVisibilidadeCardapio.getDias5X2Padrao();
        int[] esperado = {1, 2, 3, 4, 5}; // seg-sex
        
        assertArrayEquals("Valores devem ser seg-sex", esperado, dias);
    }
    
    // ===== TESTES: VALIDAÇÃO DE NULOS =====
    
    @Test
    public void testValidacao_TurnoNull() {
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            null,
            "06:00-18:00",
            "TARDE",
            FiltroVisibilidadeCardapio.getDataHoje(),
            null
        );
        assertFalse("Turno null deve retornar false", resultado);
    }
    
    @Test
    public void testValidacao_CardapioNull() {
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "MANHA",
            "06:00-18:00",
            null,
            FiltroVisibilidadeCardapio.getDataHoje(),
            null
        );
        assertFalse("Cardápio null deve retornar false", resultado);
    }
    
    @Test
    public void testValidacao_DataNull() {
        boolean resultado = FiltroVisibilidadeCardapio.podeVerCardapio(
            "MANHA",
            "06:00-18:00",
            "TARDE",
            null,
            null
        );
        assertFalse("Data null deve retornar false", resultado);
    }
    
    // ===== MÉTODOS AUXILIARES =====
    
    private String encontrarProximaDiaSemanax(int diaSemanaAlvo) {
        // Encontrar próximo dia da semana específico (0=dom, 6=sab)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        
        int diaAtual = cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;
        int diasAdicionar = (diaSemanaAlvo - diaAtual + 7) % 7;
        
        if (diasAdicionar == 0) {
            diasAdicionar = 7; // Se é hoje, vai para próxima semana
        }
        
        cal.add(java.util.Calendar.DAY_OF_MONTH, diasAdicionar);
        return sdf.format(cal.getTime());
    }
    
    private int obterDiaSemana(String dataStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date data = sdf.parse(dataStr);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(data);
            return cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;
        } catch (Exception e) {
            return -1;
        }
    }
}
