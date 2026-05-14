package com.example.visualizadorapp.model;

public class Usuario {
    private String uid;
    private String nome;
    private String email;
    private String cpf; // CPF do funcionário (11 dígitos, sem formatação)
    private String telefone; // Telefone para contato (11 dígitos com DDD)
    private String cargo; // Enum Cargo como String
    private String plantao; // "A", "B", ou null
    private String horario; // "10-22", "19-07", "5x2", ou null
    private String turno; // "MANHA", "TARDE", "NOITE", "5X2"
    private String setor;
    private String tipo; // backward compatibility
    private boolean validado; // Se o funcionário foi validado contra a base de dados
    private long dataRegistro; // Data de registro no sistema

    public Usuario() {
        // Construtor vazio necessário para Firebase
    }

    public Usuario(String uid, String nome, String email, String cargo) {
        this.uid = uid;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
    }

    // Getters e Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getPlantao() {
        return plantao;
    }

    public void setPlantao(String plantao) {
        this.plantao = plantao;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public boolean isValidado() {
        return validado;
    }

    public void setValidado(boolean validado) {
        this.validado = validado;
    }

    public long getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(long dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    // Métodos auxiliares
    public String getCargoFormatado() {
        if (cargo == null) return "Não definido";
        
        Cargo c = Cargo.fromString(cargo);
        if (c == null) return cargo;
        
        switch (c) {
            case GERENTE: return "Gerente";
            case TECNICA: return "Técnica em Nutrição";
            case LIDER_COZINHA: return "Líder de Cozinha";
            case COZINHEIRO: return "Cozinheiro";
            case AUXILIAR: return "Auxiliar de Cozinha";
            case MEIO_OFICIAL_PLANTAO: return "Meio Oficial (Plantão)";
            case MEIO_OFICIAL_5X2: return "Meio Oficial (5x2)";
            case ESTOQUISTA: return "Estoquista";
            case COPEIRA: return "Copeira";
            case COPEIRA_5X2: return "Copeira (5x2)";
            case COPEIRO_NOTURNO: return "Copeiro Noturno";
            case USUARIO_COMUM: return "Usuário Comum";
            default: return cargo;
        }
    }

    public String getDescricaoCompleta() {
        StringBuilder desc = new StringBuilder(getCargoFormatado());
        
        if (plantao != null && !plantao.isEmpty()) {
            desc.append(" - Plantão ").append(plantao);
        }
        
        if (horario != null && !horario.isEmpty()) {
            desc.append(" (").append(horario).append(")");
        }
        
        return desc.toString();
    }
}
