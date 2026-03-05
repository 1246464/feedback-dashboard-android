package com.example.visualizadorapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "passagem_turno")
public class PassagemTurno {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String data; // yyyy-MM-dd
    
    private String turnoOrigem; // "MANHA", "TARDE", "NOITE"
    private String turnoDestino; // "MANHA", "TARDE", "NOITE"
    private String usuarioOrigem;
    private String mensagem;
    private String tipoMensagem; // "INFORMACAO", "ALERTA", "URGENTE"
    private long timestamp;
    private boolean lida;
    
    // Informações estruturadas
    private String tarefasConcluidas; // JSON ou texto separado
    private String tarefasPendentes; // JSON ou texto separado
    private String problemas; // Problemas encontrados
    private String mudancas; // Mudanças de cardápio ou ingredientes
    
    public PassagemTurno() {
        this.timestamp = System.currentTimeMillis();
        this.lida = false;
        this.tipoMensagem = "INFORMACAO";
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getData() {
        return data;
    }

    public void setData(@NonNull String data) {
        this.data = data;
    }

    public String getTurnoOrigem() {
        return turnoOrigem;
    }

    public void setTurnoOrigem(String turnoOrigem) {
        this.turnoOrigem = turnoOrigem;
    }

    public String getTurnoDestino() {
        return turnoDestino;
    }

    public void setTurnoDestino(String turnoDestino) {
        this.turnoDestino = turnoDestino;
    }

    public String getUsuarioOrigem() {
        return usuarioOrigem;
    }

    public void setUsuarioOrigem(String usuarioOrigem) {
        this.usuarioOrigem = usuarioOrigem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getTipoMensagem() {
        return tipoMensagem;
    }

    public void setTipoMensagem(String tipoMensagem) {
        this.tipoMensagem = tipoMensagem;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public String getTarefasConcluidas() {
        return tarefasConcluidas;
    }

    public void setTarefasConcluidas(String tarefasConcluidas) {
        this.tarefasConcluidas = tarefasConcluidas;
    }

    public String getTarefasPendentes() {
        return tarefasPendentes;
    }

    public void setTarefasPendentes(String tarefasPendentes) {
        this.tarefasPendentes = tarefasPendentes;
    }

    public String getProblemas() {
        return problemas;
    }

    public void setProblemas(String problemas) {
        this.problemas = problemas;
    }

    public String getMudancas() {
        return mudancas;
    }

    public void setMudancas(String mudancas) {
        this.mudancas = mudancas;
    }
}
