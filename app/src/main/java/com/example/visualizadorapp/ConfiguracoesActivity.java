package com.example.visualizadorapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.example.visualizadorapp.utils.ThemeManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ConfiguracoesActivity extends AppCompatActivity {
    private RadioGroup radioGroupTheme;
    private RadioButton radioThemeLight, radioThemeDark, radioThemeSystem;
    private SwitchMaterial switchNotificacoes, switchReservas;
    private Button btnVoltar;
    private ThemeManager themeManager;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        themeManager = new ThemeManager(this);
        preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // Inicializar componentes
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        radioThemeLight = findViewById(R.id.radioThemeLight);
        radioThemeDark = findViewById(R.id.radioThemeDark);
        radioThemeSystem = findViewById(R.id.radioThemeSystem);
        switchNotificacoes = findViewById(R.id.switchNotificacoes);
        switchReservas = findViewById(R.id.switchReservas);
        btnVoltar = findViewById(R.id.btnVoltar);

        // Carregar preferências atuais
        loadPreferences();

        // Listener para mudança de tema
        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int themeMode = ThemeManager.THEME_SYSTEM;
            if (checkedId == R.id.radioThemeLight) {
                themeMode = ThemeManager.THEME_LIGHT;
            } else if (checkedId == R.id.radioThemeDark) {
                themeMode = ThemeManager.THEME_DARK;
            }
            themeManager.setThemeMode(themeMode);
        });

        // Listener para notificações
        switchNotificacoes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("notificacoes_cardapio", isChecked).apply();
        });

        switchReservas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("notificacoes_reservas", isChecked).apply();
        });

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void loadPreferences() {
        // Carregar tema
        int currentTheme = themeManager.getThemeMode();
        switch (currentTheme) {
            case ThemeManager.THEME_LIGHT:
                radioThemeLight.setChecked(true);
                break;
            case ThemeManager.THEME_DARK:
                radioThemeDark.setChecked(true);
                break;
            case ThemeManager.THEME_SYSTEM:
                radioThemeSystem.setChecked(true);
                break;
        }

        // Carregar preferências de notificações
        switchNotificacoes.setChecked(preferences.getBoolean("notificacoes_cardapio", true));
        switchReservas.setChecked(preferences.getBoolean("notificacoes_reservas", true));
    }
}
