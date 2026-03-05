package com.example.visualizadorapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.adapter.MudancaCardapioAdapter;
import com.example.visualizadorapp.viewmodel.MudancaCardapioViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HistoricoMudancasActivity extends AppCompatActivity {
    
    private MudancaCardapioViewModel viewModel;
    private RecyclerView recyclerViewMudancas;
    private MudancaCardapioAdapter adapter;
    private TextView textPeriodo;
    private Button btnVoltar, btnUltimasSemana, btnUltimoMes;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_mudancas);
        
        inicializarComponentes();
        configurarViewModel();
        configurarRecyclerView();
        configurarListeners();
        
        carregarUltimasMudancas();
    }
    
    private void inicializarComponentes() {
        recyclerViewMudancas = findViewById(R.id.recyclerViewMudancas);
        textPeriodo = findViewById(R.id.textPeriodoMudancas);
        btnVoltar = findViewById(R.id.btnVoltarMudancas);
        btnUltimasSemana = findViewById(R.id.btnUltimaSemana);
        btnUltimoMes = findViewById(R.id.btnUltimoMes);
    }
    
    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this).get(MudancaCardapioViewModel.class);
    }
    
    private void configurarRecyclerView() {
        adapter = new MudancaCardapioAdapter();
        recyclerViewMudancas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMudancas.setAdapter(adapter);
    }
    
    private void configurarListeners() {
        btnVoltar.setOnClickListener(v -> finish());
        
        btnUltimasSemana.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            String dataFim = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, -7);
            String dataInicio = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            
            carregarMudancasPorPeriodo(dataInicio, dataFim);
            textPeriodo.setText("Últimos 7 dias");
        });
        
        btnUltimoMes.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            String dataFim = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, -30);
            String dataInicio = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            
            carregarMudancasPorPeriodo(dataInicio, dataFim);
            textPeriodo.setText("Últimos 30 dias");
        });
    }
    
    private void carregarUltimasMudancas() {
        viewModel.getUltimasMudancas().observe(this, mudancas -> {
            adapter.setMudancas(mudancas);
            textPeriodo.setText("Últimas mudanças");
        });
    }
    
    private void carregarMudancasPorPeriodo(String dataInicio, String dataFim) {
        viewModel.getMudancasPorPeriodo(dataInicio, dataFim).observe(this, mudancas -> {
            adapter.setMudancas(mudancas);
        });
    }
}
