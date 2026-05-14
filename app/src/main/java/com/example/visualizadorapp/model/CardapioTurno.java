package com.example.visualizadorapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.annotation.NonNull;
import java.io.Serializable;

/**
 * Modelo que relaciona Cardápio a um Turno específico
 * Um cardápio pode ser diferente para cada turno (MANHA, TARDE, NOITE)
 * ou igual para todos (reutilizado)
 */
@Entity(tableName = "cardapio_turno")
public class CardapioTurno implements Serializable {
    @PrimaryKey
    @NonNull
    private String id; // Formato: "2024-01-15_MANHA" (data_turno)
    
    @NonNull
    private String data; // yyyy-MM-dd
    
    @NonNull
    private String turno; // "TARDE", "NOITE" (Café da manhã é padrão)
    
    private String pratoPrincipal;
    private String guarnicao;
    private String acompanhamento;
    private String salada;
    private String sobremesa;
    private String imagemUrl;
    
    // Informações adicionais
    private String calorias; // Ex: "2500 kcal"
    private String proteinas; // Ex: "45g"
    private String informacoesAdicionais; // Notas do chef, avisos, etc
    
    private long timestamp;
    private boolean isFavorito;
    private int quantidadeDisponivel; // Quantas refeições disponíveis


    public CardapioTurno() {
        this.timestamp = System.currentTimeMillis();
        this.quantidadeDisponivel = -1; // -1 significa ilimitado
    }

    @Ignore
    public CardapioTurno(String data, String turno) {
        this.data = data;
        this.turno = turno;
        this.id = data + "_" + turno;
        this.timestamp = System.currentTimeMillis();
        this.quantidadeDisponivel = -1;
    }

    // Getters e Setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getData() {
        return data;
    }

    public void setData(@NonNull String data) {
        this.data = data;
    }

    @NonNull
    public String getTurno() {
        return turno;
    }

    public void setTurno(@NonNull String turno) {
        this.turno = turno;
    }

    public String getPratoPrincipal() {
        return pratoPrincipal;
    }

    public void setPratoPrincipal(String pratoPrincipal) {
        this.pratoPrincipal = pratoPrincipal;
    }

    public String getGuarnicao() {
        return guarnicao;
    }

    public void setGuarnicao(String guarnicao) {
        this.guarnicao = guarnicao;
    }

    public String getAcompanhamento() {
        return acompanhamento;
    }

    public void setAcompanhamento(String acompanhamento) {
        this.acompanhamento = acompanhamento;
    }

    public String getSalada() {
        return salada;
    }

    public void setSalada(String salada) {
        this.salada = salada;
    }

    public String getSobremesa() {
        return sobremesa;
    }

    public void setSobremesa(String sobremesa) {
        this.sobremesa = sobremesa;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getCalorias() {
        return calorias;
    }

    public void setCalorias(String calorias) {
        this.calorias = calorias;
    }

    public String getProteinas() {
        return proteinas;
    }

    public void setProteinas(String proteinas) {
        this.proteinas = proteinas;
    }

    public String getInformacoesAdicionais() {
        return informacoesAdicionais;
    }

    public void setInformacoesAdicionais(String informacoesAdicionais) {
        this.informacoesAdicionais = informacoesAdicionais;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFavorito() {
        return isFavorito;
    }

    public void setFavorito(boolean favorito) {
        isFavorito = favorito;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    // Métodos auxiliares
    public String getTurnoFormatado() {
        if (turno == null) return "Sem turno";
        switch (turno) {
            case "TARDE": return "Almoço";
            case "NOITE": return "Jantar";
            default: return turno;
        }
    }

    public String getResumoCardapio() {
        StringBuilder sb = new StringBuilder();
        if (pratoPrincipal != null) {
            sb.append("Prato: ").append(pratoPrincipal).append("\n");
        }
        if (guarnicao != null) {
            sb.append("Guarnição: ").append(guarnicao).append("\n");
        }
        if (salada != null) {
            sb.append("Salada: ").append(salada).append("\n");
        }
        if (sobremesa != null) {
            sb.append("Sobremesa: ").append(sobremesa);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return data + " - " + getTurnoFormatado();
    }
}
