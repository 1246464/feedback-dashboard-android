package com.example.visualizadorapp.utils;

import android.text.TextUtils;

/**
 * Validador de Telefone Brasileiro
 * Valida números com 11 dígitos (2 DDD + 9 dígitos do número)
 */
public class ValidadorTelefone {

    /**
     * Valida se um número de telefone é válido
     * Aceita: (11) 98765-4321, (11) 3456-7890, 11987654321, 1134567890, etc
     * @param telefone Número de telefone com ou sem formatação
     * @return true se o telefone é válido
     */
    public static boolean isValido(String telefone) {
        if (TextUtils.isEmpty(telefone)) {
            return false;
        }

        // Remove tudo que não é número
        String apenasNumeros = removerFormatacao(telefone);

        // Telefone brasileiro deve ter 11 dígitos (DDD + número)
        if (apenasNumeros.length() != 11) {
            return false;
        }

        // Verifica se é apenas números
        if (!apenasNumeros.matches("\\d+")) {
            return false;
        }

        // Verifica DDD (primeiro dígito não pode ser 0)
        int ddd = Integer.parseInt(apenasNumeros.substring(0, 2));
        if (ddd < 11 || ddd > 99) {
            return false;
        }

        // Verifica se o primeiro dígito do número é 9 (celular) ou 2-5 (fixo)
        int primeiroDigitoNumero = Integer.parseInt(String.valueOf(apenasNumeros.charAt(2)));
        if (primeiroDigitoNumero < 2) {
            return false;
        }

        return true;
    }

    /**
     * Remove formatação do telefone
     * @param telefone Telefone formatado ou não
     * @return Telefone sem formatação (apenas dígitos)
     */
    public static String removerFormatacao(String telefone) {
        if (telefone == null) {
            return "";
        }
        return telefone.replaceAll("\\D", "");
    }

    /**
     * Formata o telefone
     * @param telefone Telefone sem formatação
     * @return Telefone formatado (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX
     */
    public static String formatar(String telefone) {
        telefone = removerFormatacao(telefone);

        if (telefone.length() != 11) {
            return telefone;
        }

        // Verifica se é celular (9º dígito)
        if (telefone.charAt(2) == '9') {
            return "(" + telefone.substring(0, 2) + ") " +
                   telefone.substring(2, 7) + "-" +
                   telefone.substring(7);
        } else {
            // Fixo
            return "(" + telefone.substring(0, 2) + ") " +
                   telefone.substring(2, 6) + "-" +
                   telefone.substring(6);
        }
    }

    /**
     * Extrai o DDD do telefone
     * @param telefone Telefone formatado ou não
     * @return DDD (ex: 11, 21, 85)
     */
    public static String extrairDDD(String telefone) {
        String apenasNumeros = removerFormatacao(telefone);
        if (apenasNumeros.length() >= 2) {
            return apenasNumeros.substring(0, 2);
        }
        return "";
    }

    /**
     * Verifica se é um celular (9º dígito = 9)
     * @param telefone Telefone formatado ou não
     * @return true se é celular
     */
    public static boolean isCelular(String telefone) {
        String apenasNumeros = removerFormatacao(telefone);
        if (apenasNumeros.length() == 11) {
            return apenasNumeros.charAt(2) == '9';
        }
        return false;
    }

    /**
     * Verifica se é um telefone fixo
     * @param telefone Telefone formatado ou não
     * @return true se é fixo
     */
    public static boolean isFixo(String telefone) {
        String apenasNumeros = removerFormatacao(telefone);
        if (apenasNumeros.length() == 11) {
            char terceiroDigito = apenasNumeros.charAt(2);
            return terceiroDigito >= '2' && terceiroDigito <= '5';
        }
        return false;
    }
}
