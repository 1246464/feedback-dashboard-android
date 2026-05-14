package com.example.visualizadorapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import java.io.Serializable;

@Entity(tableName = "reservas")
public class Reserva implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String userId;
    
    @NonNull
    private String data; // yyyy-MM-dd
    
    private String nomeUsuario;
    private String emailUsuario;
    private String escolhaPrato; // A escolha do usuário (ovo, peixe, etc)
    private String setor; // Setor do usuário
    private String turno; // "ALMOÇO", "JANTA" - Identifica qual turno é a reserva
    private long timestampReserva;
    private String statusReserva; // "ATIVA", "CANCELADA", "UTILIZADA"
    private String observacao;
    
    public Reserva() {
        this.timestampReserva = System.currentTimeMillis();
        this.statusReserva = "ATIVA";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getData() {
        return data;
    }

    public void setData(@NonNull String data) {
        this.data = data;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public long getTimestampReserva() {
        return timestampReserva;
    }

    public void setTimestampReserva(long timestampReserva) {
        this.timestampReserva = timestampReserva;
    }

    public String getStatusReserva() {
        return statusReserva;
    }

    public void setStatusReserva(String statusReserva) {
        this.statusReserva = statusReserva;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getEscolhaPrato() {
        return escolhaPrato;
    }

    public void setEscolhaPrato(String escolhaPrato) {
        this.escolhaPrato = escolhaPrato;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
}
