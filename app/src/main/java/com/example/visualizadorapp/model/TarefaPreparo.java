package com.example.visualizadorapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "tarefas_preparo")
public class TarefaPreparo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String dataCardapio; // yyyy-MM-dd
    
    private String descricaoTarefa;
    private String turnoResponsavel; // "MANHA", "TARDE", "NOITE"
    private String status; // "PENDENTE", "EM_ANDAMENTO", "CONCLUIDA"
    private String responsavel;
    private long timestampInicio;
    private long timestampConclusao;
    private String observacao;
    private int prioridade; // 1-5 (1=baixa, 5=urgente)
    
    public TarefaPreparo() {
        this.status = "PENDENTE";
        this.prioridade = 3;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getDataCardapio() {
        return dataCardapio;
    }

    public void setDataCardapio(@NonNull String dataCardapio) {
        this.dataCardapio = dataCardapio;
    }

    public String getDescricaoTarefa() {
        return descricaoTarefa;
    }

    public void setDescricaoTarefa(String descricaoTarefa) {
        this.descricaoTarefa = descricaoTarefa;
    }

    public String getTurnoResponsavel() {
        return turnoResponsavel;
    }

    public void setTurnoResponsavel(String turnoResponsavel) {
        this.turnoResponsavel = turnoResponsavel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public long getTimestampInicio() {
        return timestampInicio;
    }

    public void setTimestampInicio(long timestampInicio) {
        this.timestampInicio = timestampInicio;
    }

    public long getTimestampConclusao() {
        return timestampConclusao;
    }

    public void setTimestampConclusao(long timestampConclusao) {
        this.timestampConclusao = timestampConclusao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }
}
