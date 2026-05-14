package com.example.visualizadorapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.visualizadorapp.MainActivity;
import com.example.visualizadorapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "cardapio_notifications";
    private static final String CHANNEL_NAME = "Notificações de Cardápio";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Processa a mensagem recebida
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            sendNotification(title, body, "default");
        }

        // Processa dados customizados
        if (remoteMessage.getData().size() > 0) {
            String tipo = remoteMessage.getData().get("tipo");
            String mensagem = remoteMessage.getData().get("mensagem");
            String titulo = remoteMessage.getData().get("titulo");
            
            if ("novo_cardapio".equals(tipo)) {
                sendNotification("📋 Novo Cardápio Disponível", mensagem, "cardapio");
            } else if ("reserva".equals(tipo)) {
                sendNotification("🍽️ Lembrete de Reserva", mensagem, "reserva");
            } else if ("mudanca_cardapio".equals(tipo)) {
                sendNotification("⚠️ " + (titulo != null ? titulo : "Mudança no Cardápio"), mensagem, "mudanca");
            } else if ("tarefa_pendente".equals(tipo)) {
                sendNotification("✅ Tarefa Pendente", mensagem, "tarefa");
            } else if ("ingrediente_faltando".equals(tipo)) {
                sendNotification("📦 Ingrediente Faltando", mensagem, "ingrediente");
            } else if ("passagem_turno".equals(tipo)) {
                sendNotification("💬 Nova Passagem de Turno", mensagem, "turno");
            } else {
                sendNotification(titulo != null ? titulo : "Notificação", mensagem, "default");
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Salvar o token no Firebase Realtime Database ou enviar para o servidor
        // Isso permite enviar notificações direcionadas para este dispositivo
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        try {
            // Obter usuário autenticado
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                String userId = auth.getCurrentUser().getUid();
                
                // Salvar token no Firebase Database
                FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/")
                    .getReference()
                    .child("usuarios")
                    .child(userId)
                    .child("fcmToken")
                    .setValue(token)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FCM", "✅ Token FCM salvo com sucesso: " + token.substring(0, 20) + "...");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FCM", "❌ Erro ao salvar token: " + e.getMessage());
                    });
                
                // Se inscrever em tópicos para receber notificações
                subscribeToTopics();
            } else {
                Log.d("FCM", "Usuário não autenticado. Token será salvo no próximo login.");
            }
        } catch (Exception e) {
            Log.e("FCM", "Erro ao enviar token: " + e.getMessage(), e);
        }
    }
    
    private void subscribeToTopics() {
        try {
            // Se inscrever em tópicos gerais
            FirebaseMessaging.getInstance().subscribeToTopic("todos")
                .addOnSuccessListener(aVoid -> Log.d("FCM", "✅ Inscrito em tópico: todos"))
                .addOnFailureListener(e -> Log.e("FCM", "❌ Erro ao inscrever em tópico: " + e.getMessage()));
            
            FirebaseMessaging.getInstance().subscribeToTopic("cardapio")
                .addOnSuccessListener(aVoid -> Log.d("FCM", "✅ Inscrito em tópico: cardapio"))
                .addOnFailureListener(e -> Log.e("FCM", "❌ Erro ao inscrever em tópico: " + e.getMessage()));
            
            // Verificar cargo do usuário para inscrever em tópicos específicos
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                String userId = auth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/")
                    .getReference()
                    .child("usuarios")
                    .child(userId)
                    .child("cargo")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String cargo = snapshot.getValue(String.class);
                            if (cargo != null) {
                                // Inscrever em tópico baseado no cargo
                                if (cargo.equals("COZINHEIRO") || cargo.equals("COPEIRO") || cargo.equals("LIDER_COZINHA")) {
                                    FirebaseMessaging.getInstance().subscribeToTopic("cozinha")
                                        .addOnSuccessListener(aVoid -> Log.d("FCM", "✅ Inscrito em tópico: cozinha"))
                                        .addOnFailureListener(e -> Log.e("FCM", "❌ Erro: " + e.getMessage()));
                                }
                                
                                if (cargo.equals("GERENTE")) {
                                    FirebaseMessaging.getInstance().subscribeToTopic("admin")
                                        .addOnSuccessListener(aVoid -> Log.d("FCM", "✅ Inscrito em tópico: admin"))
                                        .addOnFailureListener(e -> Log.e("FCM", "❌ Erro: " + e.getMessage()));
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("FCM", "Erro ao verificar cargo: " + e.getMessage()));
            }
        } catch (Exception e) {
            Log.e("FCM", "Erro ao inscrever em tópicos: " + e.getMessage(), e);
        }
    }

    private void sendNotification(String title, String message, String tipo) {
        // Intent para abrir o app ao clicar na notificação
        Intent intent;
        
        // Determinar qual activity abrir baseado no tipo
        switch (tipo) {
            case "mudanca":
            case "tarefa":
                intent = new Intent(this, com.example.visualizadorapp.DashboardPreparoActivity.class);
                break;
            case "ingrediente":
                intent = new Intent(this, com.example.visualizadorapp.GestaoIngredientesActivity.class);
                break;
            case "turno":
                intent = new Intent(this, com.example.visualizadorapp.PassagemTurnoActivity.class);
                break;
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 
            (int) System.currentTimeMillis(), // ID único para cada notificação
            intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Criar notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManager notificationManager = 
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificações sobre cardápios e reservas");
            channel.enableVibration(true);

            NotificationManager notificationManager = 
                getSystemService(NotificationManager.class);
            
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
