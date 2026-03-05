package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Dashboard para Copeiras e Copeiros
 * Foco em: Cardápio, reservas e distribuição
 */
public class DashboardCopeiraActivity extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private TextView txtBoasVindas, txtCargo, txtHorario;
    private LinearLayout containerAcoes;
    private Button btnSair, btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_copeira);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();

        inicializarComponentes();
        carregarDadosUsuario();
    }

    private void inicializarComponentes() {
        txtBoasVindas = findViewById(R.id.txtBoasVindasCopeira);
        txtCargo = findViewById(R.id.txtCargoCopeira);
        txtHorario = findViewById(R.id.txtHorarioCopeira);
        containerAcoes = findViewById(R.id.containerAcoesCopeira);
        btnSair = findViewById(R.id.btnSairCopeira);
        btnVoltar = findViewById(R.id.btnVoltarCopeira);

        btnVoltar.setOnClickListener(v -> finish());
        btnSair.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        configurarBotoes();
    }

    private void configurarBotoes() {
        adicionarBotao("📋 Ver Cardápio do Dia", android.R.color.holo_blue_dark, MainActivity.class);
        adicionarBotao("🍽️ Reservas do Dia", android.R.color.holo_green_dark, ListaReservasActivity.class);
        adicionarBotao("📊 Visão Semanal", android.R.color.holo_purple, VisaoSemanalActivity.class);
    }

    private void adicionarBotao(String texto, int cor, Class<?> activityClass) {
        Button btn = new Button(this);
        btn.setText(texto);
        btn.setBackgroundTintList(getColorStateList(cor));
        btn.setTextColor(getColor(android.R.color.white));
        btn.setOnClickListener(v -> startActivity(new Intent(this, activityClass)));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(60)
        );
        params.setMargins(0, 0, 0, dpToPx(12));
        btn.setLayoutParams(params);
        btn.setTextSize(16);
        btn.setAllCaps(false);
        
        containerAcoes.addView(btn);
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
                        String cargo = snapshot.child("cargo").getValue(String.class);
                        String horario = snapshot.child("horario").getValue(String.class);

                        txtBoasVindas.setText("Bem-vindo(a), " + (nome != null ? nome : "Copeira") + "!");
                        
                        if (cargo != null) {
                            txtCargo.setText("Cargo: " + cargo);
                        }
                        
                        if (horario != null) {
                            txtHorario.setText("Horário: " + horario);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
