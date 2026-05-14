package com.example.visualizadorapp.util;

import java.util.Calendar;

/**
 * Utilitário para detectar e validar horários de turnos de refeição
 * Cada turno tem:
 * - Horário de funcionamento do serviço
 * - Horário limite de retirada da reserva
 */
public class TurnoUtil {
    
    // Constantes de turnos
    public static final String TURNO_ALMOCO = "ALMOÇO";
    public static final String TURNO_JANTA = "JANTA";
    
    // ===== ALMOÇO =====
    private static final int ALMOCO_INICIO = 11;           // 11:00 - Início do almoço
    private static final int ALMOCO_FUNCIONAMENTO_FIM = 14; // 14:30 - Fim do serviço de almoço
    private static final int ALMOCO_RETIRADA_FIM = 18;     // 18:00 - Horário limite para retirar a reserva
    
    // ===== JANTA =====
    private static final int JANTA_INICIO = 18;            // 18:00 - Início da janta
    private static final int JANTA_FUNCIONAMENTO_FIM = 22; // 22:00 - Fim do serviço de janta
    private static final int JANTA_RETIRADA_FIM = 0;       // 00:00 (meia-noite) - Horário limite para retirar a reserva
    
    /**
     * Detecta o turno baseado na hora atual
     * @return "ALMOÇO", "JANTA" ou null se fora do horário
     */
    public static String detectarTurnoAtual() {
        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (horaAtual >= ALMOCO_INICIO && horaAtual < ALMOCO_FUNCIONAMENTO_FIM) {
            return TURNO_ALMOCO;
        } else if (horaAtual >= JANTA_INICIO && horaAtual < 24) {
            return TURNO_JANTA;
        }
        return null;
    }
    
    /**
     * Verifica se pode fazer reserva no turno especificado
     * @param turno - "ALMOÇO" ou "JANTA"
     * @return true se dentro do horário permitido
     */
    public static boolean podeReservar(String turno) {
        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (TURNO_ALMOCO.equals(turno)) {
            // Almoço: reserva disponível durante o horário de funcionamento
            return horaAtual >= ALMOCO_INICIO && horaAtual < ALMOCO_FUNCIONAMENTO_FIM;
        } else if (TURNO_JANTA.equals(turno)) {
            // Janta: reserva disponível durante o horário de funcionamento
            return horaAtual >= JANTA_INICIO && horaAtual < JANTA_FUNCIONAMENTO_FIM;
        }
        return false;
    }
    
    /**
     * Retorna a hora limite de RETIRADA para o turno
     * @param turno - "ALMOÇO" ou "JANTA"
     * @return Horário como String (ex: "18:00", "00:00")
     */
    public static String getHoraLimiteRetirada(String turno) {
        if (TURNO_ALMOCO.equals(turno)) {
            return "18:00";
        } else if (TURNO_JANTA.equals(turno)) {
            return "00:00 (meia-noite)";
        }
        return "Desconhecido";
    }
    
    /**
     * Retorna o horário de funcionamento do serviço
     * @param turno - "ALMOÇO" ou "JANTA"
     * @return Horário como String
     */
    public static String getHorarioFuncionamento(String turno) {
        if (TURNO_ALMOCO.equals(turno)) {
            return "11:00 às 14:30";
        } else if (TURNO_JANTA.equals(turno)) {
            return "18:00 às 22:00";
        }
        return "Desconhecido";
    }
    
    /**
     * Retorna mensagem formatada sobre o horário de reserva
     * @param turno - "ALMOÇO" ou "JANTA"
     * @return Mensagem descritiva
     */
    public static String getMensagemHorario(String turno) {
        if (TURNO_ALMOCO.equals(turno)) {
            return "Almoço: 11:00 às 14:30 | Retirada até 18:00";
        } else if (TURNO_JANTA.equals(turno)) {
            return "Janta: 18:00 às 22:00 | Retirada até 00:00 (meia-noite)";
        }
        return "Fora do horário de funcionamento";
    }
    
    /**
     * Obtém o turno para uma hora específica
     * @param hora - Hora do dia (0-23)
     * @return Turno correspondente ou null
     */
    public static String getTurnoForHora(int hora) {
        if (hora >= ALMOCO_INICIO && hora < ALMOCO_FUNCIONAMENTO_FIM) {
            return TURNO_ALMOCO;
        } else if (hora >= JANTA_INICIO && hora < 24) {
            return TURNO_JANTA;
        }
        return null;
    }
    
    /**
     * Verifica se um horário faz parte do turno especificado (período de funcionamento)
     * @param hora - Hora (0-23)
     * @param turno - "ALMOÇO" ou "JANTA"
     * @return true se a hora pertence ao turno
     */
    public static boolean horaPertencentTurno(int hora, String turno) {
        if (TURNO_ALMOCO.equals(turno)) {
            return hora >= ALMOCO_INICIO && hora < ALMOCO_FUNCIONAMENTO_FIM;
        } else if (TURNO_JANTA.equals(turno)) {
            return hora >= JANTA_INICIO && hora < 24;
        }
        return false;
    }
}
