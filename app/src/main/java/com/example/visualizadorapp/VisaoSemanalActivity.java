package com.example.visualizadorapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.visualizadorapp.viewmodel.GestaoPreparoViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VisaoSemanalActivity extends AppCompatActivity {
    
    private DatabaseReference database;
    private GestaoPreparoViewModel tarefaViewModel;
    
    private LinearLayout containerDiasSemana;
    private TextView txtResumoTarefas, txtResumoIngredientes, txtResumoStatus;
    private Button btnVoltar;
    
    private String[] datasProximos7Dias = new String[7];
    private Map<String, Integer> contagemIngredientes = new HashMap<>();
    private int totalCardapios = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visao_semanal);
        
        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();
        tarefaViewModel = new ViewModelProvider(this).get(GestaoPreparoViewModel.class);
        
        inicializarComponentes();
        calcularDatasProximos7Dias();
        carregarDadosDaSemana();
    }
    
    private void inicializarComponentes() {
        containerDiasSemana = findViewById(R.id.containerDiasSemana);
        txtResumoTarefas = findViewById(R.id.txtResumoTarefas);
        txtResumoIngredientes = findViewById(R.id.txtResumoIngredientes);
        txtResumoStatus = findViewById(R.id.txtResumoStatus);
        btnVoltar = findViewById(R.id.btnVoltarVisaoSemanal);
        
        btnVoltar.setOnClickListener(v -> finish());
    }
    
    private void calcularDatasProximos7Dias() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        for (int i = 0; i < 7; i++) {
            datasProximos7Dias[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
    
    private void carregarDadosDaSemana() {
        final int[] diasCarregados = {0};
        
        for (int i = 0; i < 7; i++) {
            final int diaIndex = i;
            
            database.child("cardapios").child(datasProximos7Dias[i])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            totalCardapios++;
                            exibirDiaNaLista(diaIndex, snapshot);
                            contabilizarIngredientes(snapshot);
                        } else {
                            exibirDiaVazio(diaIndex);
                        }
                        
                        diasCarregados[0]++;
                        if (diasCarregados[0] == 7) {
                            atualizarResumos();
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        diasCarregados[0]++;
                        exibirDiaVazio(diaIndex);
                        if (diasCarregados[0] == 7) {
                            atualizarResumos();
                        }
                    }
                });
        }
    }
    
    private void exibirDiaNaLista(int diaIndex, DataSnapshot snapshot) {
        LinearLayout diaView = (LinearLayout) getLayoutInflater()
            .inflate(R.layout.item_dia_semana, containerDiasSemana, false);
        
        TextView txtDia = diaView.findViewById(R.id.txtDiaSemana);
        TextView txtData = diaView.findViewById(R.id.txtDataDia);
        TextView txtCardapio = diaView.findViewById(R.id.txtCardapioDia);
        
        // Configurar dia
        String nomeDia = getNomeDiaCompleto(diaIndex);
        txtDia.setText(nomeDia);
        
        // Configurar data
        SimpleDateFormat sdfExibir = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdfParse.parse(datasProximos7Dias[diaIndex]));
            txtData.setText(sdfExibir.format(cal.getTime()));
        } catch (Exception e) {
            txtData.setText(datasProximos7Dias[diaIndex]);
        }
        
        // Configurar cardápio
        String prato = snapshot.child("pratoPrincipal").getValue(String.class);
        String guarnicao = snapshot.child("guarnicao").getValue(String.class);
        String acomp = snapshot.child("acompanhamento").getValue(String.class);
        String salada = snapshot.child("salada").getValue(String.class);
        String sobremesa = snapshot.child("sobremesa").getValue(String.class);
        
        StringBuilder cardapio = new StringBuilder();
        cardapio.append("🍽️ ").append(prato != null ? prato : "-").append("\n");
        cardapio.append("🥘 ").append(guarnicao != null ? guarnicao : "-").append("\n");
        cardapio.append("🥗 ").append(acomp != null ? acomp : "-").append("\n");
        cardapio.append("🥬 ").append(salada != null ? salada : "-").append("\n");
        cardapio.append("🍰 ").append(sobremesa != null ? sobremesa : "-");
        
        txtCardapio.setText(cardapio.toString());
        
        // Adicionar à lista
        containerDiasSemana.addView(diaView);
    }
    
    private void exibirDiaVazio(int diaIndex) {
        LinearLayout diaView = (LinearLayout) getLayoutInflater()
            .inflate(R.layout.item_dia_semana, containerDiasSemana, false);
        
        TextView txtDia = diaView.findViewById(R.id.txtDiaSemana);
        TextView txtData = diaView.findViewById(R.id.txtDataDia);
        TextView txtCardapio = diaView.findViewById(R.id.txtCardapioDia);
        
        String nomeDia = getNomeDiaCompleto(diaIndex);
        txtDia.setText(nomeDia);
        
        SimpleDateFormat sdfExibir = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdfParse.parse(datasProximos7Dias[diaIndex]));
            txtData.setText(sdfExibir.format(cal.getTime()));
        } catch (Exception e) {
            txtData.setText(datasProximos7Dias[diaIndex]);
        }
        
        txtCardapio.setText("📋 Nenhum cardápio cadastrado");
        txtCardapio.setTextColor(getResources().getColor(android.R.color.darker_gray));
        
        diaView.setAlpha(0.6f);
        containerDiasSemana.addView(diaView);
    }
    
    private void contabilizarIngredientes(DataSnapshot snapshot) {
        String[] ingredientes = {
            snapshot.child("pratoPrincipal").getValue(String.class),
            snapshot.child("guarnicao").getValue(String.class),
            snapshot.child("acompanhamento").getValue(String.class),
            snapshot.child("salada").getValue(String.class),
            snapshot.child("sobremesa").getValue(String.class)
        };
        
        for (String ingrediente : ingredientes) {
            if (ingrediente != null && !ingrediente.trim().isEmpty()) {
                contagemIngredientes.put(ingrediente, 
                    contagemIngredientes.getOrDefault(ingrediente, 0) + 1);
            }
        }
    }
    
    private void atualizarResumos() {
        // Resumo de cardápios
        txtResumoStatus.setText(totalCardapios + " de 7 dias planejados");
        
        // Resumo de ingredientes únicos
        int ingredientesUnicos = contagemIngredientes.size();
        txtResumoIngredientes.setText(ingredientesUnicos + " ingredientes únicos necessários");
        
        // Resumo de tarefas (estimativa: 6 tarefas por cardápio)
        int tarefasEstimadas = totalCardapios * 6;
        txtResumoTarefas.setText("~" + tarefasEstimadas + " tarefas serão geradas");
        
        // Status geral
        if (totalCardapios == 7) {
            txtResumoStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
txtResumoStatus.append(" ✓");
        } else if (totalCardapios >= 3) {
            txtResumoStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            txtResumoStatus.append(" ⚠");
        } else {
            txtResumoStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            txtResumoStatus.append(" ✗");
        }
    }
    
    private String getNomeDiaCompleto(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, index);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("pt", "BR"));
        String dia = sdf.format(calendar.getTime());
        return dia.substring(0, 1).toUpperCase() + dia.substring(1);
    }
}
