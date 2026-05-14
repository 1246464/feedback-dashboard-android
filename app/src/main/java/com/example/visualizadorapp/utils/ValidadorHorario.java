package com.example.visualizadorapp.utils;

/**
 * Validador de Horários de Trabalho
 * Garante que apenas os 3 horários fixos padronizados sejam usados
 * 
 * Horários válidos (sem horas quebradas):
 * - "07:00-17:00" (Regime 5x2)
 * - "06:00-18:00" (Turno Manhã)
 * - "18:00-06:00" (Turno Noite)
 * 
 * @author Sistema de Validação
 * @since 1.0
 */
public class ValidadorHorario {
    
    // Constantes dos 3 horários permitidos
    public static final String HORARIO_5X2 = "07:00-17:00";      // 10 horas
    public static final String HORARIO_MANHA = "06:00-18:00";    // 12 horas
    public static final String HORARIO_NOITE = "18:00-06:00";    // 12 horas (cruza noite)
    
    /**
     * Array com todos os horários válidos
     */
    private static final String[] HORARIOS_VALIDOS = {
        HORARIO_5X2,
        HORARIO_MANHA,
        HORARIO_NOITE
    };
    
    /**
     * Valida se um horário é um dos 3 permitidos
     * 
     * @param horario String a validar (ex: "07:00-17:00")
     * @return true se é um dos 3 horários padrão, false caso contrário
     */
    public static boolean isValido(String horario) {
        if (horario == null || horario.trim().isEmpty()) {
            return false;
        }
        
        String horarioTrimmed = horario.trim();
        
        // Verifica se está entre os 3 horários permitidos
        for (String horarioValido : HORARIOS_VALIDOS) {
            if (horarioValido.equals(horarioTrimmed)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Retorna o turno correspondente ao horário
     * 
     * @param horario String com o horário (ex: "07:00-17:00")
     * @return String com o turno (5X2, MANHA, NOITE) ou null se inválido
     */
    public static String getTurnoParaHorario(String horario) {
        if (horario == null) {
            return null;
        }
        
        switch (horario.trim()) {
            case HORARIO_5X2:
                return "5X2";
            case HORARIO_MANHA:
                return "MANHA";
            case HORARIO_NOITE:
                return "NOITE";
            default:
                return null;
        }
    }
    
    /**
     * Retorna o horário correspondente ao turno
     * 
     * @param turno String com o turno (5X2, MANHA, NOITE)
     * @return String com o horário ou null se inválido
     */
    public static String getHorarioParaTurno(String turno) {
        if (turno == null) {
            return null;
        }
        
        switch (turno.trim().toUpperCase()) {
            case "5X2":
                return HORARIO_5X2;
            case "MANHA":
                return HORARIO_MANHA;
            case "NOITE":
                return HORARIO_NOITE;
            default:
                return null;
        }
    }
    
    /**
     * Formata um horário para o padrão (adiciona zeros à esquerda)
     * 
     * @param horario String potencialmente mal formatada
     * @return String formatada ou null se não conseguir
     */
    public static String formatar(String horario) {
        if (horario == null || horario.isEmpty()) {
            return null;
        }
        
        // Remove espaços
        String cleaned = horario.trim();
        
        // Se já está em um dos formatos válidos, retorna
        if (isValido(cleaned)) {
            return cleaned;
        }
        
        // Tenta extrair números e formatar
        // Esperado formato: "HH:MM-HH:MM" ou "H:MM-H:MM" ou "H-MM" etc
        try {
            // Remove o separador e espaços
            String partes[] = cleaned.split("-");
            if (partes.length != 2) {
                return null; // Formato inválido
            }
            
            String horaInicio = partes[0].trim();
            String horaFim = partes[1].trim();
            
            // Processa hora de início
            String horaInicioFormatada = formatarHora(horaInicio);
            String horaFimFormatada = formatarHora(horaFim);
            
            if (horaInicioFormatada == null || horaFimFormatada == null) {
                return null;
            }
            
            String resultado = horaInicioFormatada + "-" + horaFimFormatada;
            
            // Verifica se o resultado é válido
            if (isValido(resultado)) {
                return resultado;
            }
            
            return null;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Formata uma hora individual para HH:MM
     * 
     * @param hora String com a hora (ex: "7", "7:00", "07:00")
     * @return String formatada "HH:MM" ou null se inválido
     */
    private static String formatarHora(String hora) {
        if (hora == null || hora.isEmpty()) {
            return null;
        }
        
        try {
            // Se já tem ":", separa
            if (hora.contains(":")) {
                String[] partes = hora.split(":");
                if (partes.length != 2) {
                    return null;
                }
                int hh = Integer.parseInt(partes[0].trim());
                int mm = Integer.parseInt(partes[1].trim());
                return String.format("%02d:%02d", hh, mm);
            } else {
                // Só tem a hora
                int hh = Integer.parseInt(hora.trim());
                return String.format("%02d:00", hh);
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Retorna a duração de um horário em horas
     * 
     * @param horario String com o horário (ex: "07:00-17:00")
     * @return Duração em horas (10, 12, ou 12 para noite) ou -1 se inválido
     */
    public static int getDuracao(String horario) {
        if (!isValido(horario)) {
            return -1;
        }
        
        switch (horario.trim()) {
            case HORARIO_5X2:
                return 10; // 7h à 17h = 10 horas
            case HORARIO_MANHA:
                return 12; // 6h à 18h = 12 horas
            case HORARIO_NOITE:
                return 12; // 18h à 6h = 12 horas (cruza dia)
            default:
                return -1;
        }
    }
    
    /**
     * Lista todos os horários válidos
     * 
     * @return Array com os 3 horários permitidos
     */
    public static String[] getHorariosValidos() {
        return HORARIOS_VALIDOS.clone();
    }
    
    /**
     * Retorna uma descrição legível do horário
     * 
     * @param horario String com o horário (ex: "07:00-17:00")
     * @return Descrição formatada ou null se inválido
     */
    public static String getDescricao(String horario) {
        if (!isValido(horario)) {
            return null;
        }
        
        switch (horario.trim()) {
            case HORARIO_5X2:
                return "Regime 5x2 (7h às 17h)";
            case HORARIO_MANHA:
                return "Turno Manhã (6h às 18h)";
            case HORARIO_NOITE:
                return "Turno Noite (18h às 6h)";
            default:
                return null;
        }
    }
    
    /**
     * Valida a correspondência entre turno e horário
     * 
     * @param turno String com o turno (MANHA, NOITE, 5X2)
     * @param horario String com o horário (ex: "07:00-17:00")
     * @return true se o par turno-horário é válido, false caso contrário
     */
    public static boolean isParValido(String turno, String horario) {
        if (!isValido(horario)) {
            return false;
        }
        
        String turnoEsperado = getTurnoParaHorario(horario);
        return turnoEsperado != null && turnoEsperado.equalsIgnoreCase(turno);
    }
    
}
