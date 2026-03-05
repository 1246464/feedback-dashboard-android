package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.example.visualizadorapp.viewmodel.GestaoPreparoViewModel;
import com.example.visualizadorapp.viewmodel.IngredienteViewModel;
import com.example.visualizadorapp.viewmodel.MudancaCardapioViewModel;
import com.example.visualizadorapp.viewmodel.PassagemTurnoViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardPreparoActivity extends AppCompatActivity {
    
    // ViewModels
    private GestaoPreparoViewModel gestaoPreparoViewModel;
    private IngredienteViewModel ingredienteViewModel;
    private PassagemTurnoViewModel passagemTurnoViewModel;
    private MudancaCardapioViewModel mudancaCardapioViewModel;
    
    // Cards
    private CardView cardTarefas, cardIngredientes, cardPassagens, cardMudancas;
    
    // TextViews de contadores
    private TextView txtTarefasPendentes, txtTarefasConcluidas;
    private TextView txtIngredientesDisponiveis, txtIngredientesFaltando;
    private TextView txtPassagensNovas, txtPassagensTotal;
    private TextView txtMudancasHoje;
    private TextView txtDataAtual;
    
    // Botões de ação
    private Button btnIrTarefas, btnIrIngredientes, btnIrPassagens, btnIrMudancas;
    private Button btnVoltar;
    
    private String dataHoje;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_preparo);
        
        dataHoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        
        inicializarComponentes();
        configurarViewModels();
        configurarListeners();
        carregarDados();
    }
    
    private void inicializarComponentes() {
        // Cards
        cardTarefas = findViewById(R.id.cardDashTarefas);
        cardIngredientes = findViewById(R.id.cardDashIngredientes);
        cardPassagens = findViewById(R.id.cardDashPassagens);
        cardMudancas = findViewById(R.id.cardDashMudancas);
        
        // Contadores de tarefas
        txtTarefasPendentes = findViewById(R.id.txtDashTarefasPendentes);
        txtTarefasConcluidas = findViewById(R.id.txtDashTarefasConcluidas);
        
        // Contadores de ingredientes
        txtIngredientesDisponiveis = findViewById(R.id.txtDashIngredientesOk);
        txtIngredientesFaltando = findViewById(R.id.txtDashIngredientesFaltando);
        
        // Contadores de passagens
        txtPassagensNovas = findViewById(R.id.txtDashPassagensNovas);
        txtPassagensTotal = findViewById(R.id.txtDashPassagensTotal);
        
        // Mudanças
        txtMudancasHoje = findViewById(R.id.txtDashMudancasHoje);
        
        // Data
        txtDataAtual = findViewById(R.id.txtDashDataAtual);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM", new Locale("pt", "BR"));
        txtDataAtual.setText(sdf.format(Calendar.getInstance().getTime()));
        
        // Botões
        btnIrTarefas = findViewById(R.id.btnDashIrTarefas);
        btnIrIngredientes = findViewById(R.id.btnDashIrIngredientes);
        btnIrPassagens = findViewById(R.id.btnDashIrPassagens);
        btnIrMudancas = findViewById(R.id.btnDashIrMudancas);
        btnVoltar = findViewById(R.id.btnDashVoltar);
    }
    
    private void configurarViewModels() {
        gestaoPreparoViewModel = new ViewModelProvider(this).get(GestaoPreparoViewModel.class);
        ingredienteViewModel = new ViewModelProvider(this).get(IngredienteViewModel.class);
        passagemTurnoViewModel = new ViewModelProvider(this).get(PassagemTurnoViewModel.class);
        mudancaCardapioViewModel = new ViewModelProvider(this).get(MudancaCardapioViewModel.class);
    }
    
    private void configurarListeners() {
        btnIrTarefas.setOnClickListener(v -> {
            startActivity(new Intent(this, GestaoPreparoActivity.class));
        });
        
        btnIrIngredientes.setOnClickListener(v -> {
            startActivity(new Intent(this, GestaoIngredientesActivity.class));
        });
        
        btnIrPassagens.setOnClickListener(v -> {
            startActivity(new Intent(this, PassagemTurnoActivity.class));
        });
        
        btnIrMudancas.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoricoMudancasActivity.class));
        });
        
        btnVoltar.setOnClickListener(v -> finish());
        
        // Clicks nos cards também navegam
        cardTarefas.setOnClickListener(v -> btnIrTarefas.performClick());
        cardIngredientes.setOnClickListener(v -> btnIrIngredientes.performClick());
        cardPassagens.setOnClickListener(v -> btnIrPassagens.performClick());
        cardMudancas.setOnClickListener(v -> btnIrMudancas.performClick());
    }
    
    private void carregarDados() {
        carregarDadosTarefas();
        carregarDadosIngredientes();
        carregarDadosPassagens();
        carregarDadosMudancas();
    }
    
    private void carregarDadosTarefas() {
        gestaoPreparoViewModel.getCountTarefasPendentes(dataHoje).observe(this, count -> {
            if (count != null) {
                txtTarefasPendentes.setText(String.valueOf(count));
                
                // Alerta visual se houver muitas tarefas pendentes
                if (count > 5) {
                    txtTarefasPendentes.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else if (count > 0) {
                    txtTarefasPendentes.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                } else {
                    txtTarefasPendentes.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            }
        });
        
        gestaoPreparoViewModel.getCountTarefasConcluidas(dataHoje).observe(this, count -> {
            if (count != null) {
                txtTarefasConcluidas.setText(String.valueOf(count));
            }
        });
    }
    
    private void carregarDadosIngredientes() {
        ingredienteViewModel.getIngredientesPorData(dataHoje).observe(this, ingredientes -> {
            if (ingredientes != null) {
                int total = ingredientes.size();
                int faltando = 0;
                
                for (var ing : ingredientes) {
                    if ("FALTANDO".equals(ing.getStatus()) || "PARCIAL".equals(ing.getStatus())) {
                        faltando++;
                    }
                }
                
                int disponiveis = total - faltando;
                txtIngredientesDisponiveis.setText(String.valueOf(disponiveis));
                txtIngredientesFaltando.setText(String.valueOf(faltando));
                
                // Alerta visual
                if (faltando > 0) {
                    txtIngredientesFaltando.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    txtIngredientesFaltando.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            }
        });
    }
    
    private void carregarDadosPassagens() {
        passagemTurnoViewModel.getPassagensPorData(dataHoje).observe(this, passagens -> {
            if (passagens != null) {
                int total = passagens.size();
                int novas = 0;
                
                for (var passagem : passagens) {
                    if (!passagem.isLida()) {
                        novas++;
                    }
                }
                
                txtPassagensTotal.setText(String.valueOf(total));
                txtPassagensNovas.setText(String.valueOf(novas));
                
                // Alerta visual
                if (novas > 0) {
                    txtPassagensNovas.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                } else {
                    txtPassagensNovas.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }
        });
    }
    
    private void carregarDadosMudancas() {
        mudancaCardapioViewModel.getCountMudancasPorData(dataHoje).observe(this, count -> {
            if (count != null) {
                txtMudancasHoje.setText(String.valueOf(count));
                
                // Alerta visual
                if (count > 0) {
                    txtMudancasHoje.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                } else {
                    txtMudancasHoje.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Recarregar dados quando voltar para o dashboard
        carregarDados();
    }
}
