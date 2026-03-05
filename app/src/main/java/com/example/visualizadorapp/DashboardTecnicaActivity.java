package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visualizadorapp.model.Cargo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Dashboard personalizado para Técnica em Nutrição
 * Visualiza: Tarefas de preparo, cardápios, passagem de turno, ingredientes
 */
public class DashboardTecnicaActivity extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private TextView txtBoasVindas, txtPlantao;
    private LinearLayout containerAcoes;
    private Button btnSair, btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_tecnica);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();

        inicializarComponentes();
        carregarDadosUsuario();
    }

    private void inicializarComponentes() {
        txtBoasVindas = findViewById(R.id.txtBoasVindasTecnica);
        txtPlantao = findViewById(R.id.txtPlantaoTecnica);
        containerAcoes = findViewById(R.id.containerAcoesTecnica);
        btnSair = findViewById(R.id.btnSairTecnica);
        btnVoltar = findViewById(R.id.btnVoltarTecnica);

        btnVoltar.setOnClickListener(v -> finish());
        btnSair.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Configurar botões de ação
        configurarBotoes();
    }

    private void configurarBotoes() {
        // Ver Cardápio do Dia
        Button btnCardapio = new Button(this);
        btnCardapio.setText("📋 Ver Cardápio do Dia");
        btnCardapio.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_dark));
        btnCardapio.setTextColor(getColor(android.R.color.white));
        btnCardapio.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        containerAcoes.addView(btnCardapio);

        // Gestão de Pré-Preparo (Tarefas)
        Button btnPrePreparo = new Button(this);
        btnPrePreparo.setText("✅ Gestão de Pré-Preparo");
        btnPrePreparo.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));
        btnPrePreparo.setTextColor(getColor(android.R.color.white));
        btnPrePreparo.setOnClickListener(v -> startActivity(new Intent(this, GestaoPreparoActivity.class)));
        containerAcoes.addView(btnPrePreparo);

        // Passagem de Turno
        Button btnPassagem = new Button(this);
        btnPassagem.setText("📝 Passagem de Turno");
        btnPassagem.setBackgroundTintList(getColorStateList(android.R.color.holo_purple));
        btnPassagem.setTextColor(getColor(android.R.color.white));
        btnPassagem.setOnClickListener(v -> startActivity(new Intent(this, PassagemTurnoActivity.class)));
        containerAcoes.addView(btnPassagem);

        // Ingredientes
        Button btnIngredientes = new Button(this);
        btnIngredientes.setText("📦 Gestão de Ingredientes");
        btnIngredientes.setBackgroundTintList(getColorStateList(android.R.color.holo_orange_dark));
        btnIngredientes.setTextColor(getColor(android.R.color.white));
        btnIngredientes.setOnClickListener(v -> startActivity(new Intent(this, GestaoIngredientesActivity.class)));
        containerAcoes.addView(btnIngredientes);

        // Dashboard de Pré-Preparo
        Button btnDashboard = new Button(this);
        btnDashboard.setText("📊 Dashboard de Pré-Preparo");
        btnDashboard.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_light));
        btnDashboard.setTextColor(getColor(android.R.color.white));
        btnDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardPreparoActivity.class)));
        containerAcoes.addView(btnDashboard);

        // Histórico de Mudanças
        Button btnHistorico = new Button(this);
        btnHistorico.setText("📜 Histórico de Mudanças");
        btnHistorico.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        btnHistorico.setTextColor(getColor(android.R.color.white));
        btnHistorico.setOnClickListener(v -> startActivity(new Intent(this, HistoricoMudancasActivity.class)));
        containerAcoes.addView(btnHistorico);

        // Aplicar estilo aos botões
        for (int i = 0; i < containerAcoes.getChildCount(); i++) {
            Button btn = (Button) containerAcoes.getChildAt(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60)
            );
            params.setMargins(0, 0, 0, dpToPx(12));
            btn.setLayoutParams(params);
            btn.setTextSize(16);
            btn.setAllCaps(false);
        }
    }

    private void carregarDadosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        database.child("usuarios").child(user.getUid())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nome = snapshot.child("nome").getValue(String.class);
                        String plantao = snapshot.child("plantao").getValue(String.class);

                        txtBoasVindas.setText("Bem-vinda, " + (nome != null ? nome : "Técnica") + "!");
                        
                        if (plantao != null) {
                            txtPlantao.setText("Plantão: " + plantao + " (12x36)");
                        } else {
                            txtPlantao.setText("Plantão: Não definido");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Ignorar
                }
            });
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
