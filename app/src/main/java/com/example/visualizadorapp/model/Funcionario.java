package com.example.visualizadorapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

import com.example.visualizadorapp.utils.ValidadorHorario;

/**
 * Modelo de Funcionário Real - Dados validados do sistema de RH
 * Utilizado para validação durante o cadastro de usuários
 */
@Entity(tableName = "funcionarios")
public class Funcionario {
    @PrimaryKey
    @NonNull
    private String cpf; // CPF sem formatação (11 dígitos)
    
    @NonNull
    private String nome;
    
    @NonNull
    private String email;
    
    @NonNull
    private String telefone; // 11 dígitos com DDD
    
    @NonNull
    private String cargo; // Enum Cargo como String
    
    @NonNull
    private String turno; // Turno de trabalho (ex: "MANHA", "TARDE", "NOITE", "5X2")
    // Nota: Cardápios disponíveis apenas para TARDE (almoço) e NOITE (jantar)
    
    private String plantao; // Plantão da escala (ex: "Plantão A", "Plantão B") - para rodízios
    
    @NonNull
    private String setor;
    
    private String horario; // Opções fixas: "07:00-17:00" (5x2), "06:00-18:00" (manhã), "18:00-06:00" (noite)
    
    private boolean ativo; // Se ainda trabalha na instituição
    
    private long dataAdmissao; // Timestamp de quando foi contratado
    
    private long ultimaAtualizacao; // Timestamp da última atualização


    public Funcionario() {
        this.ativo = true;
        this.ultimaAtualizacao = System.currentTimeMillis();
    }

    @Ignore
    public Funcionario(String cpf, String nome, String email, String telefone, 
                      String cargo, String turno, String setor) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cargo = cargo;
        this.turno = turno;
        this.setor = setor;
        this.ativo = true;
        this.ultimaAtualizacao = System.currentTimeMillis();
    }

    // Getters e Setters
    @NonNull
    public String getCpf() {
        return cpf;
    }

    public void setCpf(@NonNull String cpf) {
        this.cpf = cpf;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(@NonNull String telefone) {
        this.telefone = telefone;
    }

    @NonNull
    public String getCargo() {
        return cargo;
    }

    public void setCargo(@NonNull String cargo) {
        this.cargo = cargo;
    }

    @NonNull
    public String getTurno() {
        return turno;
    }

    public void setTurno(@NonNull String turno) {
        this.turno = turno;
    }

    public String getPlantao() {
        return plantao;
    }

    public void setPlantao(String plantao) {
        this.plantao = plantao;
    }

    @NonNull
    public String getSetor() {
        return setor;
    }

    public void setSetor(@NonNull String setor) {
        this.setor = setor;
    }

    public String getHorario() {
        return horario;
    }

    /**
     * Define o horário com validação
     * Apenas os 3 horários padrão são aceitos:
     * - "07:00-17:00" (Regime 5x2, 10 horas)
     * - "06:00-18:00" (Turno Manhã, 12 horas)
     * - "18:00-06:00" (Turno Noite, 12 horas, cruza dia)
     * 
     * @param horario String com o horário a definir
     * @throws IllegalArgumentException se o horário não for um dos 3 padrões
     */
    public void setHorario(String horario) throws IllegalArgumentException {
        if (horario == null || horario.trim().isEmpty()) {
            this.horario = null;
            return;
        }
        
        if (!ValidadorHorario.isValido(horario)) {
            throw new IllegalArgumentException(
                "Horário inválido: '" + horario + "'. " +
                "Use um dos valores padrão: " + 
                "'07:00-17:00', '06:00-18:00' ou '18:00-06:00'"
            );
        }
        
        this.horario = horario.trim();
        this.ultimaAtualizacao = System.currentTimeMillis();
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public long getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(long dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public long getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(long ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    // Métodos auxiliares
    public String getCargoFormatado() {
        Cargo c = Cargo.fromString(cargo);
        return c != null ? c.getNome() : cargo;
    }

    public String getTurnoFormatado() {
        return turno.replace("_", " ");
    }

    /**
     * Retorna a descrição formatada do horário
     * @return Descrição legível do horário ou null se não definido
     */
    public String getHorarioFormatado() {
        if (horario == null) {
            return null;
        }
        return ValidadorHorario.getDescricao(horario);
    }

    /**
     * Valida a correspondência entre turno e horário
     * @return true se turno e horário são válidos e coerentes
     */
    public boolean isHorarioValido() {
        if (horario == null || turno == null) {
            return false;
        }
        return ValidadorHorario.isParValido(turno, horario);
    }

    /**
     * Retorna a duração do trabalho em horas
     * @return Duração em horas (10, 12) ou -1 se inválido
     */
    public int getDuracaoTrabalho() {
        if (horario == null) {
            return -1;
        }
        return ValidadorHorario.getDuracao(horario);
    }

    @Override
    public String toString() {
        return nome + " (" + cargo + " - " + turno + " [" + horario + "])";
    }
}
