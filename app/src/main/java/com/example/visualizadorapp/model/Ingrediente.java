package com.example.visualizadorapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "ingredientes")
public class Ingrediente {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String dataCardapio; // yyyy-MM-dd
    
    private String nomeIngrediente;
    private String quantidade;
    private String status; // "DISPONIVEL", "FALTANDO", "PARCIAL"
    private String observacao;
    private String responsavelVerificacao;
    private long timestampVerificacao;
    
    public Ingrediente() {
        this.status = "DISPONIVEL";
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

    public String getNomeIngrediente() {
        return nomeIngrediente;
    }

    public void setNomeIngrediente(String nomeIngrediente) {
        this.nomeIngrediente = nomeIngrediente;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getResponsavelVerificacao() {
        return responsavelVerificacao;
    }

    public void setResponsavelVerificacao(String responsavelVerificacao) {
        this.responsavelVerificacao = responsavelVerificacao;
    }

    public long getTimestampVerificacao() {
        return timestampVerificacao;
    }

    public void setTimestampVerificacao(long timestampVerificacao) {
        this.timestampVerificacao = timestampVerificacao;
    }
}
