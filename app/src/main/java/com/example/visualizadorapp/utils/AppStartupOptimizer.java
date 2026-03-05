package com.example.visualizadorapp.utils;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppStartupOptimizer {
    
    private static final ExecutorService backgroundExecutor = Executors.newFixedThreadPool(3);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    /**
     * Inicializa tarefas em background durante o startup
     */
    public static void initializeAsync(Application application, Runnable... tasks) {
        for (Runnable task : tasks) {
            backgroundExecutor.execute(task);
        }
    }
    
    /**
     * Inicializa tarefas com delay para não bloquear o startup
     */
    public static void initializeDelayed(Runnable task, long delayMillis) {
        mainHandler.postDelayed(task, delayMillis);
    }
    
    /**
     * Executa tarefa em background e retorna resultado na main thread
     */
    public static <T> void executeAsync(BackgroundTask<T> task, OnResultListener<T> listener) {
        backgroundExecutor.execute(() -> {
            try {
                T result = task.execute();
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onSuccess(result);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
            }
        });
    }
    
    /**
     * Pré-carrega recursos críticos
     */
    public static void preloadCriticalResources(Application application) {
        backgroundExecutor.execute(() -> {
            // Pré-carregar database
            com.example.visualizadorapp.database.AppDatabase.getDatabase(application);
            
            // Pré-carregar SharedPreferences
            application.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE);
        });
    }
    
    /**
     * Limpa cache desnecessário
     */
    public static void cleanupOldData(Application application) {
        backgroundExecutor.execute(() -> {
            try {
                com.example.visualizadorapp.database.AppDatabase db = 
                    com.example.visualizadorapp.database.AppDatabase.getDatabase(application);
                
                // Deletar cardápios antigos (mais de 90 dias)
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.add(java.util.Calendar.DAY_OF_MONTH, -90);
                String oldDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(calendar.getTime());
                
                db.cardapioDao().deleteOlderThan(oldDate);
                db.reservaDao().deleteOlderThan(oldDate);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Otimiza memória
     */
    public static void optimizeMemory() {
        System.gc();
    }
    
    /**
     * Shutdown executor ao fechar app
     */
    public static void shutdown() {
        backgroundExecutor.shutdown();
    }
    
    // Interfaces
    public interface BackgroundTask<T> {
        T execute() throws Exception;
    }
    
    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
    
    /**
     * Estratégias de inicialização
     */
    public static class InitStrategy {
        // Tarefas críticas (executar imediatamente)
        public static final int CRITICAL = 0;
        
        // Tarefas importantes (executar com delay pequeno)
        public static final int IMPORTANT = 100;
        
        // Tarefas opcionais (executar com delay maior)
        public static final int OPTIONAL = 500;
        
        // Tarefas de limpeza (executar quando idle)
        public static final int CLEANUP = 2000;
    }
    
    /**
     * Monitora performance do startup
     */
    public static class StartupMonitor {
        private static long startTime;
        
        public static void start() {
            startTime = System.currentTimeMillis();
        }
        
        public static long getElapsedTime() {
            return System.currentTimeMillis() - startTime;
        }
        
        public static void logStartupTime(String tag) {
            long elapsed = getElapsedTime();
            android.util.Log.d("StartupMonitor", tag + ": " + elapsed + "ms");
        }
    }
}
