package com.example.visualizadorapp.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.visualizadorapp.DashboardPreparoActivity;
import com.example.visualizadorapp.GestaoIngredientesActivity;
import com.example.visualizadorapp.MainActivity;
import com.example.visualizadorapp.PassagemTurnoActivity;
import com.example.visualizadorapp.R;

/**
 * Classe utilitária para gerenciar notificações do sistema de pré-preparo
 */
public class NotificationHelper {
    
    public static final String CHANNEL_ID_DEFAULT = "cardapio_notifications";
    public static final String CHANNEL_ID_PREPARO = "preparo_notifications";
    public static final String CHANNEL_ID_URGENTE = "urgente_notifications";
    
    private static final String CHANNEL_NAME_DEFAULT = "Notificações de Cardápio";
    private static final String CHANNEL_NAME_PREPARO = "Notificações de Pré-Preparo";
    private static final String CHANNEL_NAME_URGENTE = "Notificações Urgentes";
    
    /**
     * Cria os canais de notificação necessários
     */
    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            if (notificationManager == null) return;
            
            // Canal padrão
            NotificationChannel channelDefault = new NotificationChannel(
                CHANNEL_ID_DEFAULT,
                CHANNEL_NAME_DEFAULT,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channelDefault.setDescription("Notificações sobre cardápios e reservas");
            channelDefault.enableVibration(true);
            notificationManager.createNotificationChannel(channelDefault);
            
            // Canal de pré-preparo
            NotificationChannel channelPreparo = new NotificationChannel(
                CHANNEL_ID_PREPARO,
                CHANNEL_NAME_PREPARO,
                NotificationManager.IMPORTANCE_HIGH
            );
            channelPreparo.setDescription("Notificações sobre tarefas e ingredientes");
            channelPreparo.enableVibration(true);
            notificationManager.createNotificationChannel(channelPreparo);
            
            // Canal urgente
            NotificationChannel channelUrgente = new NotificationChannel(
                CHANNEL_ID_URGENTE,
                CHANNEL_NAME_URGENTE,
                NotificationManager.IMPORTANCE_HIGH
            );
            channelUrgente.setDescription("Notificações urgentes que requerem atenção imediata");
            channelUrgente.enableVibration(true);
            channelUrgente.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channelUrgente);
        }
    }
    
    /**
     * Envia notificação sobre mudança no cardápio
     */
    public static void notificarMudancaCardapio(Context context, String mensagem) {
        Intent intent = new Intent(context, DashboardPreparoActivity.class);
        enviarNotificacao(context, 
            "⚠️ Mudança no Cardápio", 
            mensagem, 
            intent,
            CHANNEL_ID_URGENTE,
            NotificationCompat.PRIORITY_HIGH);
    }
    
    /**
     * Envia notificação sobre tarefa pendente
     */
    public static void notificarTarefaPendente(Context context, String descricaoTarefa) {
        Intent intent = new Intent(context, DashboardPreparoActivity.class);
        enviarNotificacao(context, 
            "✅ Tarefa Pendente", 
            descricaoTarefa, 
            intent,
            CHANNEL_ID_PREPARO,
            NotificationCompat.PRIORITY_DEFAULT);
    }
    
    /**
     * Envia notificação sobre ingrediente faltando
     */
    public static void notificarIngredienteFaltando(Context context, String nomeIngrediente) {
        Intent intent = new Intent(context, GestaoIngredientesActivity.class);
        enviarNotificacao(context, 
            "📦 Ingrediente Faltando", 
            "Atenção: " + nomeIngrediente + " está faltando!", 
            intent,
            CHANNEL_ID_URGENTE,
            NotificationCompat.PRIORITY_HIGH);
    }
    
    /**
     * Envia notificação sobre nova passagem de turno
     */
    public static void notificarPassagemTurno(Context context, String turno, String mensagem) {
        Intent intent = new Intent(context, PassagemTurnoActivity.class);
        enviarNotificacao(context, 
            "💬 Passagem de Turno - " + turno, 
            mensagem, 
            intent,
            CHANNEL_ID_PREPARO,
            NotificationCompat.PRIORITY_HIGH);
    }
    
    /**
     * Envia notificação sobre tarefas urgentes
     */
    public static void notificarTarefasUrgentes(Context context, int numTarefas) {
        Intent intent = new Intent(context, DashboardPreparoActivity.class);
        enviarNotificacao(context, 
            "🚨 Tarefas Urgentes", 
            "Você tem " + numTarefas + " tarefa(s) urgente(s) pendente(s)!", 
            intent,
            CHANNEL_ID_URGENTE,
            NotificationCompat.PRIORITY_MAX);
    }

    /**
     * Envia notificação sobre tarefas geradas
     */
    public static void notificarTarefasGeradas(Context context, String titulo, String mensagem) {
        Intent intent = new Intent(context, DashboardPreparoActivity.class);
        enviarNotificacao(context, 
            titulo, 
            mensagem, 
            intent,
            CHANNEL_ID_PREPARO,
            NotificationCompat.PRIORITY_DEFAULT);
    }
    
    /**
     * Envia notificação sobre nova reserva (para cozinheiro/copeiro)
     */
    public static void notificarNovaReserva(Context context, String nomeUsuario, String turno, String prato) {
        Intent intent = new Intent(context, MainActivity.class);
        enviarNotificacao(context, 
            "🍽️ Nova Reserva - " + turno, 
            nomeUsuario + " reservou: " + prato, 
            intent,
            CHANNEL_ID_PREPARO,
            NotificationCompat.PRIORITY_HIGH);
    }

    /**
     * Envia notificação sobre novo cardápio publicado (para todos os funcionários)
     */
    public static void notificarNovoCardapio(Context context, String mensagem) {
        Intent intent = new Intent(context, MainActivity.class);
        enviarNotificacao(context, 
            "📋 Novo Cardápio Disponível", 
            mensagem, 
            intent,
            CHANNEL_ID_DEFAULT,
            NotificationCompat.PRIORITY_DEFAULT);
    }

    /**
     * Envia notificação genérica
     */
    public static void enviarNotificacao(
            Context context,
            String titulo,
            String mensagem,
            Intent intent,
            String channelId,
            int prioridade) {
        
        if (intent == null) {
            intent = new Intent(context, MainActivity.class);
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            (int) System.currentTimeMillis(),
            intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setAutoCancel(true)
            .setPriority(prioridade)
            .setContentIntent(pendingIntent)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(mensagem));
        
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
    
    /**
     * Limpa todas as notificações
     */
    public static void limparTodasNotificacoes(Context context) {
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
}
