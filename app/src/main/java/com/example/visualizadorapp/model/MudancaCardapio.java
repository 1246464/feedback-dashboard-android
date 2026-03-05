package com.example.visualizadorapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "mudancas_cardapio")
public class MudancaCardapio {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String data; // yyyy-MM-dd
    
    private String itemAlterado; // "PRATO_PRINCIPAL", "GUARNICAO", etc
    private String valorAnterior;
    private String valorNovo;
    private String motivoMudanca;
    private String usuarioResponsavel;
    private long timestamp;
    private boolean notificadoTurnos; // Se os turnos foram notificados
    
    public MudancaCardapio() {
        this.timestamp = System.currentTimeMillis();
        this.notificadoTurnos = false;
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

    public String getItemAlterado() {
        return itemAlterado;
    }

    public void setItemAlterado(String itemAlterado) {
        this.itemAlterado = itemAlterado;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNovo() {
        return valorNovo;
    }

    public void setValorNovo(String valorNovo) {
        this.valorNovo = valorNovo;
    }

    public String getMotivoMudanca() {
        return motivoMudanca;
    }

    public void setMotivoMudanca(String motivoMudanca) {
        this.motivoMudanca = motivoMudanca;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(String usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isNotificadoTurnos() {
        return notificadoTurnos;
    }

    public void setNotificadoTurnos(boolean notificadoTurnos) {
        this.notificadoTurnos = notificadoTurnos;
    }
}
