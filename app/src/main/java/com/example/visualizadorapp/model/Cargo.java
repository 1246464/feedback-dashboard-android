package com.example.visualizadorapp.model;

/**
 * Enum que define todos os cargos/perfis do sistema
 */
public enum Cargo {
    // Administração
    GERENTE("Gerente", "ADMIN"),
    
    // Plantão 12x36
    TECNICA("Técnica em Nutrição", "TECNICA"),
    COZINHEIRO("Cozinheiro", "COZINHA"),
    AUXILIAR("Auxiliar de Cozinha", "COZINHA"),
    COPEIRA("Copeira", "SERVICO"),
    MEIO_OFICIAL_PLANTAO("Meio Oficial (Plantão)", "COZINHA"),
    
    // Regime 5x2
    LIDER_COZINHA("Líder de Cozinha", "LIDER"),
    ESTOQUISTA("Estoquista", "ESTOQUE"),
    MEIO_OFICIAL_5X2("Meio Oficial (5x2)", "COZINHA"),
    COPEIRA_5X2("Copeira (5x2)", "SERVICO"),
    COPEIRO_NOTURNO("Copeiro Noturno", "SERVICO"),
    
    // Usuário comum (quem come)
    USUARIO_COMUM("Usuário", "COMUM");

    private final String nome;
    private final String categoria;

    Cargo(String nome, String categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    /**
     * Verifica se o cargo tem acesso administrativo
     */
    public boolean isAdmin() {
        return this == GERENTE;
    }

    /**
     * Verifica se o cargo é de liderança (pode ver dashboards avançados)
     */
    public boolean isLider() {
        return this == GERENTE || this == LIDER_COZINHA || this == TECNICA;
    }

    /**
     * Verifica se o cargo trabalha na cozinha (vê tarefas de preparo)
     */
    public boolean isCozinha() {
        return categoria.equals("COZINHA") || categoria.equals("LIDER") || this == TECNICA;
    }

    /**
     * Verifica se o cargo cuida de estoque
     */
    public boolean isEstoque() {
        return this == ESTOQUISTA || this == GERENTE || this == LIDER_COZINHA;
    }

    /**
     * Verifica se o cargo faz passagem de turno
     */
    public boolean fazPassagemTurno() {
        return this == TECNICA || this == COZINHEIRO || this == LIDER_COZINHA;
    }

    /**
     * Verifica se trabalha em plantão 12x36
     */
    public boolean isPlantao() {
        return this == TECNICA || this == COZINHEIRO || this == AUXILIAR || 
               this == COPEIRA || this == MEIO_OFICIAL_PLANTAO || this == COPEIRO_NOTURNO;
    }

    /**
     * Verifica se trabalha 5x2
     */
    public boolean is5x2() {
        return this == LIDER_COZINHA || this == ESTOQUISTA || 
               this == MEIO_OFICIAL_5X2 || this == COPEIRA_5X2 || this == GERENTE;
    }

    /**
     * Retorna o cargo a partir do nome
     */
    public static Cargo fromString(String nome) {
        for (Cargo cargo : Cargo.values()) {
            if (cargo.nome.equalsIgnoreCase(nome) || cargo.name().equalsIgnoreCase(nome)) {
                return cargo;
            }
        }
        return USUARIO_COMUM; // Padrão
    }
}
