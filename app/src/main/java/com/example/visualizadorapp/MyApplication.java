package com.example.visualizadorapp;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Habilita persistência offline do Firebase
        // Permite que o app funcione sem internet e sincronize depois
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
