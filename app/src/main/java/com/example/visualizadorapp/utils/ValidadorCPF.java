package com.example.visualizadorapp.utils;

/**
 * Validador de CPF com cálculo de dígitos verificadores
 * Segue o padrão oficial brasileiro (módulo 11)
 */
public class ValidadorCPF {

    /**
     * Valida se um CPF é válido
     * @param cpf CPF sem formatação (11 dígitos) ou com formatação (XXX.XXX.XXX-XX)
     * @return true se o CPF é válido
     */
    public static boolean isValido(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }

        // Remove formatação
        cpf = cpf.replaceAll("\\D", "");

        // Verifica se tem exatamente 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int primeiroDigito = calcularDigito(cpf, 10);
        if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito) {
            return false;
        }

        // Calcula o segundo dígito verificador
        int segundoDigito = calcularDigito(cpf, 11);
        if (Character.getNumericValue(cpf.charAt(10)) != segundoDigito) {
            return false;
        }

        return true;
    }

    /**
     * Calcula o dígito verificador do CPF
     * @param cpf CPF sem formatação
     * @param posicao Posição para qual calcular o dígito (10 ou 11)
     * @return O dígito verificador
     */
    private static int calcularDigito(String cpf, int posicao) {
        int soma = 0;
        int multiplicador = 2;

        for (int i = posicao - 2; i >= 0; i--) {
            soma += Character.getNumericValue(cpf.charAt(i)) * multiplicador;
            multiplicador++;
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /**
     * Remove formatação do CPF
     * @param cpf CPF formatado ou não
     * @return CPF sem formatação (11 dígitos)
     */
    public static String removerFormatacao(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("\\D", "");
    }

    /**
     * Formata o CPF
     * @param cpf CPF sem formatação
     * @return CPF formatado (XXX.XXX.XXX-XX)
     */
    public static String formatar(String cpf) {
        cpf = removerFormatacao(cpf);
        if (cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9);
    }
}
