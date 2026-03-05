package com.example.visualizadorapp.model;

/**
 * Modelo para item da lista de compras semanal
 */
public class ItemCompra {
    private String nomeIngrediente;
    private String quantidadeTotal;
    private String detalhamentoDias; // Ex: "Seg: 5kg, Ter: 3kg"
    private boolean comprado;
    private int diasNecessario; // Quantos dias usa este ingrediente
    
    public ItemCompra() {
    }
    
    public ItemCompra(String nomeIngrediente, String quantidadeTotal) {
        this.nomeIngrediente = nomeIngrediente;
        this.quantidadeTotal = quantidadeTotal;
        this.comprado = false;
        this.diasNecessario = 0;
    }

    // Getters e Setters
    public String getNomeIngrediente() {
        return nomeIngrediente;
    }

    public void setNomeIngrediente(String nomeIngrediente) {
        this.nomeIngrediente = nomeIngrediente;
    }

    public String getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(String quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public String getDetalhamentoDias() {
        return detalhamentoDias;
    }

    public void setDetalhamentoDias(String detalhamentoDias) {
        this.detalhamentoDias = detalhamentoDias;
    }

    public boolean isComprado() {
        return comprado;
    }

    public void setComprado(boolean comprado) {
        this.comprado = comprado;
    }

    public int getDiasNecessario() {
        return diasNecessario;
    }

    public void setDiasNecessario(int diasNecessario) {
        this.diasNecessario = diasNecessario;
    }
}
