package com.example.visualizadorapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Filtro de Visibilidade de Cardápios por Turno
 * 
 * Regras:
 * 1. Usuário MANHA (06:00-18:00) vê:
 *    - Cardápio TARDE (almoço) do mesmo dia
 *    - NÃO vê cardápio NOITE (jantar) do mesmo dia
 * 
 * 2. Usuário NOITE (18:00-06:00) vê:
 *    - Cardápio NOITE (jantar) do mesmo dia
 *    - NÃO vê cardápio TARDE (almoço) do mesmo dia
 * 
 * 3. Usuário 5X2 (07:00-17:00) vê:
 *    - Cardápio TARDE (almoço) dos dias que trabalha
 *    - Apenas dias futuros (até 7 dias à frente)
 *    - NÃO vê cardápio NOITE
 *    - NÃO vê cardápio de dias que não trabalha
 * 
 * @author Sistema de Filtro de Cardápios
 * @since 1.0
 */
public class FiltroVisibilidadeCardapio {
    
    /**
     * Verifica se um usuário pode ver um determinado cardápio
     * 
     * @param turnoUsuario String do turno do usuário (MANHA, NOITE, 5X2)
     * @param horarioUsuario String do horário do usuário (ex: "06:00-18:00", "18:00-06:00", "07:00-17:00")
     * @param turnoCardapio String do turno do cardápio (TARDE ou NOITE)
     * @param dataCardapio String da data do cardápio (formato: yyyy-MM-dd)
     * @param diasTrabalho Array de dias da semana que o usuário trabalha (0=domingo, 6=sábado)
     *                     Apenas para 5X2. Para MANHA/NOITE, usar null
     * @return true se o usuário pode ver o cardápio, false caso contrário
     */
    public static boolean podeVerCardapio(String horarioUsuario, 
                                           String turnoCardapio,
                                           String dataCardapio,
                                           int[] diasTrabalho) {
        
        if (horarioUsuario == null || turnoCardapio == null || dataCardapio == null) {
            return false;
        }
        
        String horarioNormalizado = horarioUsuario.trim();
        String cardapioNormalizado = turnoCardapio.trim().toUpperCase();
        
        // ===== REGRA 1: Usuário MANHA (06:00-18:00) =====
        if (horarioNormalizado.contains("06:00-18:00")) {
            // Pode ver cardápio TARDE (almoço) de todos os dias
            // NÃO pode ver cardápio NOITE
            
            if (cardapioNormalizado.equals("NOITE")) {
                return false; // MANHA não pode ver NOITE
            }
            
            if (cardapioNormalizado.equals("TARDE")) {
                // Pode ver apenas do dia atual em diante
                return !isDataNoPassado(dataCardapio);
            }
            
            return false;
        }
        
        // ===== REGRA 2: Usuário NOITE (18:00-06:00) =====
        if (horarioNormalizado.contains("18:00-06:00")) {
            // Pode ver cardápio NOITE (jantar) de todos os dias
            // NÃO pode ver cardápio TARDE
            
            if (cardapioNormalizado.equals("TARDE")) {
                return false; // NOITE não pode ver TARDE
            }
            
            if (cardapioNormalizado.equals("NOITE")) {
                // Pode ver apenas do dia atual em diante
                return !isDataNoPassado(dataCardapio);
            }
            
            return false;
        }
        
        // ===== REGRA 3: Usuário 5X2 (07:00-17:00) =====
        if (horarioNormalizado.contains("07:00-17:00")) {
            // NÃO pode ver cardápio NOITE (só trabalha até 17h)
            if (cardapioNormalizado.equals("NOITE")) {
                return false;
            }
            
            // Pode ver cardápio TARDE apenas:
            // - Dos dias que trabalha (segunda-sexta)
            // - Dentro de 7 dias à frente
            if (cardapioNormalizado.equals("TARDE")) {
                return podeVer5X2(dataCardapio, diasTrabalho);
            }
            
            return false;
        }
        
        return false;
    }
    
    /**
     * Verifica se um usuário 5X2 pode ver um cardápio em uma data específica
     * 
     * @param dataCardapio String da data (yyyy-MM-dd)
     * @param diasTrabalho Array de dias da semana (0=domingo, 6=sábado)
     * @return true se pode ver, false caso contrário
     */
    private static boolean podeVer5X2(String dataCardapio, int[] diasTrabalho) {
        // Se não tem dias de trabalho definidos, não pode ver
        if (diasTrabalho == null || diasTrabalho.length == 0) {
            return false;
        }
        
        // Data no passado? Não pode ver
        if (isDataNoPassado(dataCardapio)) {
            return false;
        }
        
        // Data muito longe? (mais de 7 dias) Não pode ver
        if (isDataMuitoLonge(dataCardapio, 7)) {
            return false;
        }
        
        // Verifica se trabalha nesse dia da semana
        int diaSemanaCardapio = getDiaSemana(dataCardapio);
        for (int dia : diasTrabalho) {
            if (dia == diaSemanaCardapio) {
                return true; // Trabalha nesse dia
            }
        }
        
        return false; // Não trabalha nesse dia
    }
    
    /**
     * Verifica se uma data está no passado (comparado com hoje)
     * 
     * @param dataCardapio String da data (yyyy-MM-dd)
     * @return true se está no passado, false caso contrário
     */
    private static boolean isDataNoPassado(String dataCardapio) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date data = sdf.parse(dataCardapio);
            Date hoje = sdf.parse(sdf.format(new Date()));
            
            return data.before(hoje);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verifica se uma data está muito longe no futuro
     * 
     * @param dataCardapio String da data (yyyy-MM-dd)
     * @param diasMaximos Número máximo de dias no futuro permitido
     * @return true se está muito longe, false caso contrário
     */
    private static boolean isDataMuitoLonge(String dataCardapio, int diasMaximos) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date data = sdf.parse(dataCardapio);
            Date hoje = sdf.parse(sdf.format(new Date()));
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(hoje);
            cal.add(Calendar.DAY_OF_MONTH, diasMaximos);
            Date limite = cal.getTime();
            
            return data.after(limite);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtém o dia da semana de uma data
     * 
     * @param dataCardapio String da data (yyyy-MM-dd)
     * @return Dia da semana (0=domingo, 6=sábado) ou -1 se inválido
     */
    private static int getDiaSemana(String dataCardapio) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date data = sdf.parse(dataCardapio);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            
            // Calendar usa 1=domingo, 2=segunda... 7=sábado
            // Convertemos para 0=domingo, 1=segunda... 6=sábado
            return cal.get(Calendar.DAY_OF_WEEK) - 1;
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * Obtém dias da semana em formato mais legível para 5X2
     * Padrão: Segunda a Sexta (seg=1, ter=2, qua=3, qui=4, sex=5)
     * 
     * @return Array com dias da semana para 5X2
     */
    public static int[] getDias5X2Padrao() {
        return new int[]{1, 2, 3, 4, 5}; // Segunda a Sexta
    }
    
    /**
     * Obtém dias de trabalho para Plantão A
     * Padrão: Segunda, Quarta, Sexta (1, 3, 5)
     * 
     * @return Array com dias de trabalho do Plantão A
     */
    public static int[] getDiasPlantaoA() {
        return new int[]{1, 3, 5}; // Segunda, Quarta, Sexta
    }
    
    /**
     * Obtém dias de trabalho para Plantão B
     * Padrão: Terça, Quinta, Sábado (2, 4, 6)
     * 
     * @return Array com dias de trabalho do Plantão B
     */
    public static int[] getDiasPlantaoB() {
        return new int[]{2, 4, 6}; // Terça, Quinta, Sábado
    }
    
    /**
     * Obtém dias da semana em formato mais legível para "dia sim, dia não"
     * 
     * @param dataInicio String da primeira data de trabalho (yyyy-MM-dd)
     * @param numeroDias Número de dias a calcular (ex: 7 para próxima semana)
     * @return Array com dias alternados
     */
    public static int[] getDiasSimDiaNao(String dataInicio, int numeroDias) {
        int[] dias = new int[numeroDias];
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date data = sdf.parse(dataInicio);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            
            for (int i = 0; i < numeroDias; i += 2) {
                int diaSemana = cal.get(Calendar.DAY_OF_WEEK) - 1;
                dias[i] = diaSemana;
                cal.add(Calendar.DAY_OF_MONTH, 2);
            }
        } catch (Exception e) {
            // Retornar array vazio em caso de erro
        }
        
        return dias;
    }
    
    /**
     * Retorna descrição legível das regras de visibilidade
     * 
     * @param turnoUsuario String do turno (MANHA, NOITE, 5X2)
     * @return Descrição das regras
     */
    public static String getDescricaoRegras(String turnoUsuario) {
        if (turnoUsuario == null) {
            return "";
        }
        
        String turnoNormalizado = turnoUsuario.trim().toUpperCase();
        
        if (turnoNormalizado.equals("MANHA")) {
            return "Vê almoço (TARDE) de todos os dias\n" +
                   "Não vê jantar (NOITE)";
        } else if (turnoNormalizado.equals("NOITE")) {
            return "Vê jantar (NOITE) de todos os dias\n" +
                   "Não vê almoço (TARDE)";
        } else if (turnoNormalizado.equals("5X2")) {
            return "Vê almoço (TARDE) apenas dos dias que trabalha\n" +
                   "Máximo 7 dias à frente\n" +
                   "Não vê jantar (NOITE)";
        }
        
        return "";
    }
    
    /**
     * Obtém a data de hoje em formato yyyy-MM-dd
     * 
     * @return String com a data de hoje
     */
    public static String getDataHoje() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    /**
     * Obtém uma data futura
     * 
     * @param diasAdicionar Número de dias a adicionar
     * @return String com a data futura
     */
    public static String getDataFutura(int diasAdicionar) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, diasAdicionar);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(cal.getTime());
    }
}
