package com.example.visualizadorapp.util;

import com.example.visualizadorapp.model.TarefaPreparo;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para geração automática de tarefas de preparo
 * baseadas no cardápio do dia
 */
public class AutoTaskGenerator {
    
    /**
     * Gera tarefas de preparo baseadas no cardápio
     * @param data Data do cardápio (yyyy-MM-dd)
     * @param pratoPrincipal Nome do prato principal
     * @param guarnicao Nome da guarnição
     * @param acompanhamento Nome do acompanhamento
     * @param salada Nome da salada
     * @param sobremesa Nome da sobremesa
     * @return Lista de tarefas geradas automaticamente
     */
    public static List<TarefaPreparo> gerarTarefasDoCardapio(
            String data, 
            String pratoPrincipal, 
            String guarnicao,
            String acompanhamento,
            String salada,
            String sobremesa) {
        
        List<TarefaPreparo> tarefas = new ArrayList<>();
        
        // Tarefa 1: Pré-preparo do prato principal
        if (pratoPrincipal != null && !pratoPrincipal.isEmpty()) {
            TarefaPreparo tarefa1 = new TarefaPreparo();
            tarefa1.setDataCardapio(data);
            tarefa1.setDescricaoTarefa("Pré-preparar: " + pratoPrincipal);
            tarefa1.setTurnoResponsavel("MANHA");
            tarefa1.setPrioridade(5); // Alta prioridade
            tarefa1.setObservacao("Verificar temperos e tempo de cocção");
            tarefas.add(tarefa1);
        }
        
        // Tarefa 2: Guarnição
        if (guarnicao != null && !guarnicao.isEmpty()) {
            TarefaPreparo tarefa2 = new TarefaPreparo();
            tarefa2.setDataCardapio(data);
            tarefa2.setDescricaoTarefa("Preparar guarnição: " + guarnicao);
            tarefa2.setTurnoResponsavel("MANHA");
            tarefa2.setPrioridade(4);
            tarefas.add(tarefa2);
        }
        
        // Tarefa 3: Acompanhamento
        if (acompanhamento != null && !acompanhamento.isEmpty()) {
            TarefaPreparo tarefa3 = new TarefaPreparo();
            tarefa3.setDataCardapio(data);
            tarefa3.setDescricaoTarefa("Preparar acompanhamento: " + acompanhamento);
            tarefa3.setTurnoResponsavel("MANHA");
            tarefa3.setPrioridade(3);
            tarefas.add(tarefa3);
        }
        
        // Tarefa 4: Salada (pode ser turno da tarde)
        if (salada != null && !salada.isEmpty()) {
            TarefaPreparo tarefa4 = new TarefaPreparo();
            tarefa4.setDataCardapio(data);
            tarefa4.setDescricaoTarefa("Higienizar e preparar salada: " + salada);
            tarefa4.setTurnoResponsavel("TARDE");
            tarefa4.setPrioridade(4);
            tarefa4.setObservacao("Lavar bem, sanitizar e cortar");
            tarefas.add(tarefa4);
        }
        
        // Tarefa 5: Sobremesa
        if (sobremesa != null && !sobremesa.isEmpty()) {
            TarefaPreparo tarefa5 = new TarefaPreparo();
            tarefa5.setDataCardapio(data);
            tarefa5.setDescricaoTarefa("Preparar sobremesa: " + sobremesa);
            tarefa5.setTurnoResponsavel("MANHA");
            tarefa5.setPrioridade(2);
            tarefas.add(tarefa5);
        }
        
        // Tarefa 6: Verificação geral
        TarefaPreparo tarefaVerificacao = new TarefaPreparo();
        tarefaVerificacao.setDataCardapio(data);
        tarefaVerificacao.setDescricaoTarefa("Verificar todos os ingredientes antes do serviço");
        tarefaVerificacao.setTurnoResponsavel("TARDE");
        tarefaVerificacao.setPrioridade(5);
        tarefaVerificacao.setObservacao("Conferir quantidades e qualidade");
        tarefas.add(tarefaVerificacao);
        
        return tarefas;
    }
    
    /**
     * Identifica mudanças entre dois valores de cardápio
     */
    public static boolean houveMudanca(String valorAntigo, String valorNovo) {
        if (valorAntigo == null && valorNovo == null) return false;
        if (valorAntigo == null) return true;
        if (valorNovo == null) return true;
        return !valorAntigo.trim().equalsIgnoreCase(valorNovo.trim());
    }
    
    /**
     * Gera mensagem de mudança para passagem de turno
     */
    public static String gerarMensagemMudanca(
            String itemAlterado,
            String valorAnterior, 
            String valorNovo) {
        
        StringBuilder msg = new StringBuilder();
        msg.append("⚠️ MUDANÇA NO CARDÁPIO\n\n");
        msg.append("Item alterado: ").append(itemAlterado).append("\n");
        
        if (valorAnterior != null && !valorAnterior.isEmpty()) {
            msg.append("Anterior: ").append(valorAnterior).append("\n");
        }
        
        msg.append("Novo: ").append(valorNovo).append("\n\n");
        msg.append("⚡ Tarefas de preparo foram atualizadas automaticamente!");
        
        return msg.toString();
    }
}
