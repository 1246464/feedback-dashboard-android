package com.example.visualizadorapp.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;

public class MessageHelper {
    
    public enum MessageType {
        SUCCESS,
        ERROR,
        WARNING,
        INFO
    }
    
    // Toast messages
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    
    // Snackbar messages
    public static void showSnackbar(View view, String message, MessageType type) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        
        // Customizar cor baseada no tipo
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(3);
        
        snackbar.show();
    }
    
    public static void showSnackbarWithAction(View view, String message, String actionText, View.OnClickListener action) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(actionText, action)
            .show();
    }
    
    // AlertDialog messages
    public static void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
    
    public static void showConfirmDialog(Context context, String title, String message, 
                                       Runnable onConfirm) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Confirmar", (dialog, which) -> {
                if (onConfirm != null) onConfirm.run();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
    
    public static void showConfirmDialog(Context context, String title, String message, 
                                       String positiveText, String negativeText,
                                       Runnable onConfirm, Runnable onCancel) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText, (dialog, which) -> {
                if (onConfirm != null) onConfirm.run();
            })
            .setNegativeButton(negativeText, (dialog, which) -> {
                if (onCancel != null) onCancel.run();
            })
            .show();
    }
    
    // Error messages user-friendly
    public static String getFriendlyErrorMessage(Exception e) {
        String error = e.getMessage();
        
        if (error == null) {
            return "Ocorreu um erro inesperado. Tente novamente.";
        }
        
        // Firebase errors
        if (error.contains("network")) {
            return "Erro de conexão. Verifique sua internet e tente novamente.";
        }
        if (error.contains("permission")) {
            return "Você não tem permissão para realizar esta ação.";
        }
        if (error.contains("auth")) {
            return "Erro de autenticação. Faça login novamente.";
        }
        if (error.contains("not found")) {
            return "Dados não encontrados.";
        }
        if (error.contains("timeout")) {
            return "A operação demorou muito. Tente novamente.";
        }
        
        // Database errors
        if (error.contains("database")) {
            return "Erro ao acessar o banco de dados.";
        }
        
        // Default
        return "Ocorreu um erro: " + error;
    }
    
    public static void showError(Context context, String title, Exception e) {
        String message = getFriendlyErrorMessage(e);
        showAlert(context, title, message);
    }
    
    public static void showSuccess(Context context, String message) {
        showAlert(context, "✅ Sucesso", message);
    }
    
    public static void showWarning(Context context, String message) {
        showAlert(context, "⚠️ Atenção", message);
    }
    
    public static void showInfo(Context context, String message) {
        showAlert(context, "ℹ️ Informação", message);
    }
    
    // Validation messages
    public static final class ValidationMessages {
        public static final String CAMPO_OBRIGATORIO = "Este campo é obrigatório";
        public static final String EMAIL_INVALIDO = "Email inválido";
        public static final String SENHA_FRACA = "A senha deve ter no mínimo 6 caracteres";
        public static final String SENHAS_NAO_CONFEREM = "As senhas não conferem";
        public static final String DATA_INVALIDA = "Data inválida";
        public static final String VALOR_INVALIDO = "Valor inválido";
    }
    
    // Network status messages
    public static void showNoInternetMessage(Context context) {
        showWarning(context, 
            "Sem conexão com a internet. Algumas funcionalidades podem estar limitadas.");
    }
    
    // Success operation messages
    public static void showOperationSuccess(Context context, String operation) {
        showSuccess(context, operation + " realizado com sucesso!");
    }
}
