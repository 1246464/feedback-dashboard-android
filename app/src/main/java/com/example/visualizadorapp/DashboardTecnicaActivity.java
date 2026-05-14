package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.visualizadorapp.model.CardapioTurno;
import com.example.visualizadorapp.model.Cargo;
import com.example.visualizadorapp.repository.CardapioTurnoRepository;
import com.example.visualizadorapp.repository.FuncionarioRepository;
import com.example.visualizadorapp.util.NotificationHelper;
import com.example.visualizadorapp.viewmodel.CardapiosViewModel;
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
    private CardapiosViewModel cardapiosViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_tecnica);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();

        inicializarComponentes();
        inicializarViewModel();
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

    private void inicializarViewModel() {
        FuncionarioRepository funcionarioRepository = new FuncionarioRepository(getApplication());
        CardapioTurnoRepository cardapioRepository = new CardapioTurnoRepository(getApplication());
        
        cardapiosViewModel = new ViewModelProvider(this, new CardapiosViewModelFactory(
                funcionarioRepository, cardapioRepository))
                .get(CardapiosViewModel.class);
    }

    private void configurarBotoes() {
        // Ver Cardápio do Dia
        adicionarBotao("📋 Ver Cardápio do Dia", android.R.color.holo_blue_dark, () -> exibirCardapioFiltrado());

        // Gestão de Pré-Preparo (Tarefas)
        adicionarBotao("✅ Gestão de Pré-Preparo", android.R.color.holo_green_dark, GestaoPreparoActivity.class);

        // Passagem de Turno
        adicionarBotao("📝 Passagem de Turno", android.R.color.holo_purple, PassagemTurnoActivity.class);

        // Ingredientes
        adicionarBotao("📦 Gestão de Ingredientes", android.R.color.holo_orange_dark, GestaoIngredientesActivity.class);

        // Dashboard de Pré-Preparo
        adicionarBotao("📊 Dashboard de Pré-Preparo", android.R.color.holo_blue_light, DashboardPreparoActivity.class);

        // Histórico de Mudanças
        adicionarBotao("📜 Histórico de Mudanças", android.R.color.darker_gray, HistoricoMudancasActivity.class);
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

    private void adicionarBotao(String texto, int cor, Runnable runnable) {
        Button btn = new Button(this);
        btn.setText(texto);
        btn.setBackgroundTintList(getColorStateList(cor));
        btn.setTextColor(getColor(android.R.color.white));
        btn.setOnClickListener(v -> runnable.run());
        
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

    private void exibirCardapioFiltrado() {
        Toast.makeText(this, "Carregando cardápios...", Toast.LENGTH_SHORT).show();
        
        cardapiosViewModel.carregarCardapiosParaUsuario();
        
        cardapiosViewModel.getCardapiosVisiveis().observe(this, cardapios -> {
            if (cardapios == null || cardapios.isEmpty()) {
                Toast.makeText(this, "Nenhum cardápio disponível para seu turno", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Notificar que cardápio está disponível
            NotificationHelper.notificarNovoCardapio(this, 
                "✅ Cardápio disponível para seu horário e plantão!");
            
            StringBuilder resumo = new StringBuilder();
            resumo.append("📋 CARDÁPIOS VISÍVEIS PARA SEU TURNO\n\n");
            
            for (CardapioTurno cardapio : cardapios) {
                resumo.append("📅 ").append(cardapio.getData()).append("\n");
                resumo.append("🍽️ Turno: ").append(cardapio.getTurno()).append("\n");
                resumo.append("🥘 Prato: ").append(cardapio.getPratoPrincipal()).append("\n");
                resumo.append("---\n");
            }
            
            Toast.makeText(this, resumo.toString(), Toast.LENGTH_LONG).show();
        });
        
        cardapiosViewModel.getDescricaoRegras().observe(this, descricao -> {
            if (descricao != null) {
                Toast.makeText(this, descricao, Toast.LENGTH_LONG).show();
            }
        });
        
        cardapiosViewModel.getErro().observe(this, erro -> {
            if (erro != null) {
                Toast.makeText(this, "Erro: " + erro, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
