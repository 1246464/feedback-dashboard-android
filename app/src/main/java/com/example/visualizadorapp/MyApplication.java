package com.example.visualizadorapp;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;
import com.example.visualizadorapp.utils.ThemeManager;
import com.example.visualizadorapp.utils.AppStartupOptimizer;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Iniciar monitor de performance
        AppStartupOptimizer.StartupMonitor.start();
        
        // Tarefas críticas (executar imediatamente)
        // Habilita persistência offline do Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        
        // Aplica o tema salvo nas preferências
        ThemeManager themeManager = new ThemeManager(this);
        themeManager.applyCurrentTheme();
        
        AppStartupOptimizer.StartupMonitor.logStartupTime("Critical tasks completed");
        
        // Tarefas importantes (executar em background)
        AppStartupOptimizer.initializeAsync(this,
            // Pré-carregar database
            () -> AppStartupOptimizer.preloadCriticalResources(this)
        );
        
        // Tarefas de limpeza (executar com delay)
        AppStartupOptimizer.initializeDelayed(
            () -> AppStartupOptimizer.cleanupOldData(this),
            AppStartupOptimizer.InitStrategy.CLEANUP
        );
        
        AppStartupOptimizer.StartupMonitor.logStartupTime("Application initialized");
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        AppStartupOptimizer.shutdown();
    }
}


