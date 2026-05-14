package com.example.visualizadorapp.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testes Unitários para ValidadorHorario
 * 
 * Testa todos os métodos da classe ValidadorHorario
 * para garantir que validação funciona corretamente
 */
public class ValidadorHorarioTest {
    
    // ===== TESTES: isValido() =====
    
    @Test
    public void testIsValido_Regime5X2() {
        assertTrue("5X2 deve ser válido", 
            ValidadorHorario.isValido("07:00-17:00"));
    }
    
    @Test
    public void testIsValido_TurnoManha() {
        assertTrue("MANHA deve ser válido", 
            ValidadorHorario.isValido("06:00-18:00"));
    }
    
    @Test
    public void testIsValido_TurnoNoite() {
        assertTrue("NOITE deve ser válido", 
            ValidadorHorario.isValido("18:00-06:00"));
    }
    
    @Test
    public void testIsValido_FormatoErrado() {
        assertFalse("Formato sem ':' não deve ser válido", 
            ValidadorHorario.isValido("0700-1700"));
    }
    
    @Test
    public void testIsValido_HorasQuebradas() {
        assertFalse("Horas quebradas não devem ser válidas", 
            ValidadorHorario.isValido("06:30-18:30"));
    }
    
    @Test
    public void testIsValido_HorarioSemFormatacao() {
        assertFalse("Horário sem formatação não deve ser válido", 
            ValidadorHorario.isValido("6-18"));
    }
    
    @Test
    public void testIsValido_String_Null() {
        assertFalse("null não deve ser válido", 
            ValidadorHorario.isValido(null));
    }
    
    @Test
    public void testIsValido_String_Vazia() {
        assertFalse("String vazia não deve ser válida", 
            ValidadorHorario.isValido(""));
    }
    
    @Test
    public void testIsValido_Com_Espacos() {
        assertTrue("Deve remover espaços antes de validar", 
            ValidadorHorario.isValido("  07:00-17:00  "));
    }
    
    @Test
    public void testIsValido_HorarioComSeparadorErrado() {
        assertFalse("Separador '/' não deve ser válido", 
            ValidadorHorario.isValido("07:00/17:00"));
    }
    
    // ===== TESTES: getTurnoParaHorario() =====
    
    @Test
    public void testGetTurnoParaHorario_5X2() {
        assertEquals("5X2", 
            ValidadorHorario.getTurnoParaHorario("07:00-17:00"));
    }
    
    @Test
    public void testGetTurnoParaHorario_MANHA() {
        assertEquals("MANHA", 
            ValidadorHorario.getTurnoParaHorario("06:00-18:00"));
    }
    
    @Test
    public void testGetTurnoParaHorario_NOITE() {
        assertEquals("NOITE", 
            ValidadorHorario.getTurnoParaHorario("18:00-06:00"));
    }
    
    @Test
    public void testGetTurnoParaHorario_Invalido() {
        assertNull("Horário inválido deve retornar null", 
            ValidadorHorario.getTurnoParaHorario("10:00-18:00"));
    }
    
    @Test
    public void testGetTurnoParaHorario_Null() {
        assertNull("null deve retornar null", 
            ValidadorHorario.getTurnoParaHorario(null));
    }
    
    // ===== TESTES: getHorarioParaTurno() =====
    
    @Test
    public void testGetHorarioParaTurno_5X2() {
        assertEquals("07:00-17:00", 
            ValidadorHorario.getHorarioParaTurno("5X2"));
    }
    
    @Test
    public void testGetHorarioParaTurno_MANHA() {
        assertEquals("06:00-18:00", 
            ValidadorHorario.getHorarioParaTurno("MANHA"));
    }
    
    @Test
    public void testGetHorarioParaTurno_NOITE() {
        assertEquals("18:00-06:00", 
            ValidadorHorario.getHorarioParaTurno("NOITE"));
    }
    
    @Test
    public void testGetHorarioParaTurno_MinusculaManha() {
        assertEquals("06:00-18:00", 
            ValidadorHorario.getHorarioParaTurno("manha"));
    }
    
    @Test
    public void testGetHorarioParaTurno_Invalido() {
        assertNull("Turno inválido deve retornar null", 
            ValidadorHorario.getHorarioParaTurno("TARDE"));
    }
    
    @Test
    public void testGetHorarioParaTurno_Null() {
        assertNull("null deve retornar null", 
            ValidadorHorario.getHorarioParaTurno(null));
    }
    
    // ===== TESTES: getDuracao() =====
    
    @Test
    public void testGetDuracao_5X2() {
        assertEquals("5X2 tem 10 horas", 10, 
            ValidadorHorario.getDuracao("07:00-17:00"));
    }
    
    @Test
    public void testGetDuracao_MANHA() {
        assertEquals("MANHA tem 12 horas", 12, 
            ValidadorHorario.getDuracao("06:00-18:00"));
    }
    
    @Test
    public void testGetDuracao_NOITE() {
        assertEquals("NOITE tem 12 horas", 12, 
            ValidadorHorario.getDuracao("18:00-06:00"));
    }
    
    @Test
    public void testGetDuracao_Invalido() {
        assertEquals("Horário inválido retorna -1", -1, 
            ValidadorHorario.getDuracao("10:00-18:00"));
    }
    
    @Test
    public void testGetDuracao_Null() {
        assertEquals("null retorna -1", -1, 
            ValidadorHorario.getDuracao(null));
    }
    
    // ===== TESTES: getDescricao() =====
    
    @Test
    public void testGetDescricao_5X2() {
        assertEquals("Regime 5x2 (7h às 17h)", 
            ValidadorHorario.getDescricao("07:00-17:00"));
    }
    
    @Test
    public void testGetDescricao_MANHA() {
        assertEquals("Turno Manhã (6h às 18h)", 
            ValidadorHorario.getDescricao("06:00-18:00"));
    }
    
    @Test
    public void testGetDescricao_NOITE() {
        assertEquals("Turno Noite (18h às 6h)", 
            ValidadorHorario.getDescricao("18:00-06:00"));
    }
    
    @Test
    public void testGetDescricao_Invalido() {
        assertNull("Horário inválido retorna null", 
            ValidadorHorario.getDescricao("10:00-18:00"));
    }
    
    // ===== TESTES: isParValido() =====
    
    @Test
    public void testIsParValido_5X2_Correto() {
        assertTrue("5X2 com 07:00-17:00 é válido", 
            ValidadorHorario.isParValido("5X2", "07:00-17:00"));
    }
    
    @Test
    public void testIsParValido_MANHA_Correto() {
        assertTrue("MANHA com 06:00-18:00 é válido", 
            ValidadorHorario.isParValido("MANHA", "06:00-18:00"));
    }
    
    @Test
    public void testIsParValido_NOITE_Correto() {
        assertTrue("NOITE com 18:00-06:00 é válido", 
            ValidadorHorario.isParValido("NOITE", "18:00-06:00"));
    }
    
    @Test
    public void testIsParValido_5X2_Errado() {
        assertFalse("5X2 com 06:00-18:00 é inválido", 
            ValidadorHorario.isParValido("5X2", "06:00-18:00"));
    }
    
    @Test
    public void testIsParValido_MANHA_Errado() {
        assertFalse("MANHA com 07:00-17:00 é inválido", 
            ValidadorHorario.isParValido("MANHA", "07:00-17:00"));
    }
    
    @Test
    public void testIsParValido_NOITE_Errado() {
        assertFalse("NOITE com 07:00-17:00 é inválido", 
            ValidadorHorario.isParValido("NOITE", "07:00-17:00"));
    }
    
    @Test
    public void testIsParValido_HorarioInvalido() {
        assertFalse("Horário inválido torna par inválido", 
            ValidadorHorario.isParValido("5X2", "10:00-18:00"));
    }
    
    // ===== TESTES: getHorariosValidos() =====
    
    @Test
    public void testGetHorariosValidos_Quantidade() {
        String[] horarios = ValidadorHorario.getHorariosValidos();
        assertEquals("Deve haver exatamente 3 horários válidos", 3, horarios.length);
    }
    
    @Test
    public void testGetHorariosValidos_Contem5X2() {
        String[] horarios = ValidadorHorario.getHorariosValidos();
        boolean encontrou = false;
        for (String h : horarios) {
            if ("07:00-17:00".equals(h)) {
                encontrou = true;
                break;
            }
        }
        assertTrue("Deve conter 07:00-17:00", encontrou);
    }
    
    @Test
    public void testGetHorariosValidos_ContemMANHA() {
        String[] horarios = ValidadorHorario.getHorariosValidos();
        boolean encontrou = false;
        for (String h : horarios) {
            if ("06:00-18:00".equals(h)) {
                encontrou = true;
                break;
            }
        }
        assertTrue("Deve conter 06:00-18:00", encontrou);
    }
    
    @Test
    public void testGetHorariosValidos_ContemNOITE() {
        String[] horarios = ValidadorHorario.getHorariosValidos();
        boolean encontrou = false;
        for (String h : horarios) {
            if ("18:00-06:00".equals(h)) {
                encontrou = true;
                break;
            }
        }
        assertTrue("Deve conter 18:00-06:00", encontrou);
    }
    
    // ===== TESTES: formatar() =====
    
    @Test
    public void testFormatar_JaFormatado() {
        assertEquals("07:00-17:00", 
            ValidadorHorario.formatar("07:00-17:00"));
    }
    
    @Test
    public void testFormatar_ComEspacos() {
        assertEquals("07:00-17:00", 
            ValidadorHorario.formatar("  07:00-17:00  "));
    }
    
    @Test
    public void testFormatar_HoraSemZero() {
        assertEquals("07:00-17:00", 
            ValidadorHorario.formatar("7:00-17:00"));
    }
    
    @Test
    public void testFormatar_HorasQuebradas() {
        assertNull("Horas quebradas não podem ser formatadas", 
            ValidadorHorario.formatar("06:30-18:30"));
    }
    
    @Test
    public void testFormatar_FormatoInvalido() {
        assertNull("Formato completamente inválido retorna null", 
            ValidadorHorario.formatar("quebrado"));
    }
    
    @Test
    public void testFormatar_SemDoisPontos() {
        assertNull("Formato sem ':' retorna null", 
            ValidadorHorario.formatar("0700-1800"));
    }
    
    // ===== TESTES: Constantes =====
    
    @Test
    public void testConstantes_5X2() {
        assertEquals("07:00-17:00", ValidadorHorario.HORARIO_5X2);
    }
    
    @Test
    public void testConstantes_MANHA() {
        assertEquals("06:00-18:00", ValidadorHorario.HORARIO_MANHA);
    }
    
    @Test
    public void testConstantes_NOITE() {
        assertEquals("18:00-06:00", ValidadorHorario.HORARIO_NOITE);
    }
    
    // ===== TESTES DE INTEGRAÇÃO =====
    
    @Test
    public void testIntegracao_TodosHorariosValidos() {
        // Garantir que todos os 3 horários são válidos
        assertTrue(ValidadorHorario.isValido(ValidadorHorario.HORARIO_5X2));
        assertTrue(ValidadorHorario.isValido(ValidadorHorario.HORARIO_MANHA));
        assertTrue(ValidadorHorario.isValido(ValidadorHorario.HORARIO_NOITE));
    }
    
    @Test
    public void testIntegracao_TodosTemsDescricao() {
        // Garantir que todos os horários têm descrição
        assertNotNull(ValidadorHorario.getDescricao(ValidadorHorario.HORARIO_5X2));
        assertNotNull(ValidadorHorario.getDescricao(ValidadorHorario.HORARIO_MANHA));
        assertNotNull(ValidadorHorario.getDescricao(ValidadorHorario.HORARIO_NOITE));
    }
    
    @Test
    public void testIntegracao_TodosTemsturno() {
        // Garantir que todos os horários podem ser convertidos para turno
        assertNotNull(ValidadorHorario.getTurnoParaHorario(ValidadorHorario.HORARIO_5X2));
        assertNotNull(ValidadorHorario.getTurnoParaHorario(ValidadorHorario.HORARIO_MANHA));
        assertNotNull(ValidadorHorario.getTurnoParaHorario(ValidadorHorario.HORARIO_NOITE));
    }
    
    @Test
    public void testIntegracao_TodosTemsUracao() {
        // Garantir que todos têm duração
        assertTrue(ValidadorHorario.getDuracao(ValidadorHorario.HORARIO_5X2) > 0);
        assertTrue(ValidadorHorario.getDuracao(ValidadorHorario.HORARIO_MANHA) > 0);
        assertTrue(ValidadorHorario.getDuracao(ValidadorHorario.HORARIO_NOITE) > 0);
    }
}
