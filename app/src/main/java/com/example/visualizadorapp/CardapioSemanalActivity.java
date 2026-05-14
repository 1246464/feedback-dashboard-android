package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.visualizadorapp.model.TarefaPreparo;
import com.example.visualizadorapp.util.AutoTaskGenerator;
import com.example.visualizadorapp.util.NotificationHelper;
import com.example.visualizadorapp.viewmodel.GestaoPreparoViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CardapioSemanalActivity extends AppCompatActivity {

    private DatabaseReference database;
    private GestaoPreparoViewModel tarefaViewModel;
    private LinearLayout containerSemanal;
    private Button btnSalvarSemana, btnVoltarAdmin, btnListaCompras, 
                   btnVisaoConsolidada, btnGerarTarefasProximos3Dias;
    
    // Arrays para armazenar os EditTexts de cada dia
    private final EditText[] edtPrincipal = new EditText[7];
    private final EditText[] edtGuarnicao = new EditText[7];
    private final EditText[] edtAcompanhamento = new EditText[7];
    private final EditText[] edtSalada = new EditText[7];
    private final EditText[] edtSobremesa = new EditText[7];
    
    private final String[] datasProximos7Dias = new String[7];
    private final String[] nomeDias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio_semanal);

        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();
        tarefaViewModel = new ViewModelProvider(this).get(GestaoPreparoViewModel.class);
        
        containerSemanal = findViewById(R.id.containerCardapioSemanal);
        btnSalvarSemana = findViewById(R.id.btnSalvarSemana);
        btnVoltarAdmin = findViewById(R.id.btnVoltarAdmin);
        btnListaCompras = findViewById(R.id.btnListaCompras);
        btnVisaoConsolidada = findViewById(R.id.btnVisaoConsolidada);
        btnGerarTarefasProximos3Dias = findViewById(R.id.btnGerarTarefasProximos3Dias);

        calcularDatasProximos7Dias();
        criarFormulariosSemana();
        carregarCardapiosSemana();

        btnSalvarSemana.setOnClickListener(v -> salvarCardapiosSemana());
        btnListaCompras.setOnClickListener(v -> abrirListaCompras());
        btnVisaoConsolidada.setOnClickListener(v -> abrirVisaoConsolidada());
        btnGerarTarefasProximos3Dias.setOnClickListener(v -> gerarTarefasProximos3Dias());
        
        btnVoltarAdmin.setOnClickListener(v -> {
            finish(); // Volta para a activity anterior (AdminActivity)
        });
    }

    private void calcularDatasProximos7Dias() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        for (int i = 0; i < 7; i++) {
            datasProximos7Dias[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void criarFormulariosSemana() {
        for (int i = 0; i < 7; i++) {
            View diaView = getLayoutInflater().inflate(R.layout.item_cardapio_dia, containerSemanal, false);
            
            TextView txtDia = diaView.findViewById(R.id.txtDiaSemana);
            TextView txtData = diaView.findViewById(R.id.txtDataCardapio);
            
            // Formatar data para exibição
            String dataFormatada = formatarDataParaExibicao(datasProximos7Dias[i]);
            txtDia.setText(nomeDias[i % 7]);
            txtData.setText(dataFormatada);
            
            // Salvar referências dos EditTexts
            edtPrincipal[i] = diaView.findViewById(R.id.edtPrincipal);
            edtGuarnicao[i] = diaView.findViewById(R.id.edtGuarnicao);
            edtAcompanhamento[i] = diaView.findViewById(R.id.edtAcompanhamento);
            edtSalada[i] = diaView.findViewById(R.id.edtSalada);
            edtSobremesa[i] = diaView.findViewById(R.id.edtSobremesa);
            
            containerSemanal.addView(diaView);
        }
    }

    private String formatarDataParaExibicao(String data) {
        try {
            SimpleDateFormat sdfOriginal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfFormatado = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdfOriginal.parse(data);
            return date != null ? sdfFormatado.format(date) : data;
        } catch (Exception e) {
            return data;
        }
    }

    private void carregarCardapiosSemana() {
        for (int i = 0; i < 7; i++) {
            final int index = i;
            database.child("cardapios").child(datasProximos7Dias[i])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            edtPrincipal[index].setText(snapshot.child("pratoPrincipal").getValue(String.class));
                            edtGuarnicao[index].setText(snapshot.child("guarnicao").getValue(String.class));
                            edtAcompanhamento[index].setText(snapshot.child("acompanhamento").getValue(String.class));
                            edtSalada[index].setText(snapshot.child("salada").getValue(String.class));
                            edtSobremesa[index].setText(snapshot.child("sobremesa").getValue(String.class));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
        }
    }

    private void salvarCardapiosSemana() {
        btnSalvarSemana.setEnabled(false);
        btnSalvarSemana.setText("Salvando...");
        
        int diasSalvos = 0;
        StringBuilder cardapiosNovos = new StringBuilder();
        
        for (int i = 0; i < 7; i++) {
            String principal = edtPrincipal[i].getText().toString().trim();
            String guarnicao = edtGuarnicao[i].getText().toString().trim();
            String acompanhamento = edtAcompanhamento[i].getText().toString().trim();
            String salada = edtSalada[i].getText().toString().trim();
            String sobremesa = edtSobremesa[i].getText().toString().trim();
            
            // Só salva se pelo menos um campo estiver preenchido
            if (!principal.isEmpty() || !guarnicao.isEmpty() || !acompanhamento.isEmpty() || 
                !salada.isEmpty() || !sobremesa.isEmpty()) {
                
                Map<String, Object> cardapio = new HashMap<>();
                cardapio.put("pratoPrincipal", principal);
                cardapio.put("guarnicao", guarnicao);
                cardapio.put("acompanhamento", acompanhamento);
                cardapio.put("salada", salada);
                cardapio.put("sobremesa", sobremesa);
                
                database.child("cardapios").child(datasProximos7Dias[i]).setValue(cardapio);
                diasSalvos++;
                
                // Registrar dia com novo cardápio
                if (cardapiosNovos.length() > 0) {
                    cardapiosNovos.append(", ");
                }
                cardapiosNovos.append(nomeDias[i]);
            }
        }
        
        final int totalSalvos = diasSalvos;
        final String diasCardapio = cardapiosNovos.toString();
        
        // Aguardar um pouco para dar tempo de salvar
        containerSemanal.postDelayed(() -> {
            btnSalvarSemana.setEnabled(true);
            btnSalvarSemana.setText("💾 Salvar Cardápios da Semana");
            Toast.makeText(this, "✓ " + totalSalvos + " cardápio(s) salvos com sucesso!", Toast.LENGTH_SHORT).show();
            
            // Enviar notificação para todos os funcionários sobre novo cardápio
            if (totalSalvos > 0) {
                NotificationHelper.notificarNovoCardapio(CardapioSemanalActivity.this,
                    "Novos cardápios disponíveis: " + diasCardapio);
            }
        }, 1000);
    }
    
    private void abrirListaCompras() {
        Intent intent = new Intent(this, ListaComprasActivity.class);
        startActivity(intent);
    }
    
    private void abrirVisaoConsolidada() {
        Intent intent = new Intent(this, VisaoSemanalActivity.class);
        startActivity(intent);
    }
    
    private void gerarTarefasProximos3Dias() {
        btnGerarTarefasProximos3Dias.setEnabled(false);
        btnGerarTarefasProximos3Dias.setText("Gerando...");
        
        final int[] diasProcessados = {0};
        final int[] tarefasGeradas = {0};
        
        // Gerar apenas para os próximos 3 dias (mais urgentes)
        for (int i = 0; i < 3; i++) {
            final int diaIndex = i;
            
            database.child("cardapios").child(datasProximos7Dias[i])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String principal = snapshot.child("pratoPrincipal").getValue(String.class);
                            String guarnicao = snapshot.child("guarnicao").getValue(String.class);
                            String acompanhamento = snapshot.child("acompanhamento").getValue(String.class);
                            String salada = snapshot.child("salada").getValue(String.class);
                            String sobremesa = snapshot.child("sobremesa").getValue(String.class);
                            
                            if (principal != null || guarnicao != null || acompanhamento != null || 
                                salada != null || sobremesa != null) {
                                
                                // Gerar tarefas para este dia
                                List<TarefaPreparo> tarefas = AutoTaskGenerator.gerarTarefasDoCardapio(
                                    datasProximos7Dias[diaIndex],
                                    principal != null ? principal : "",
                                    guarnicao != null ? guarnicao : "",
                                    acompanhamento != null ? acompanhamento : "",
                                    salada != null ? salada : "",
                                    sobremesa != null ? sobremesa : ""
                                );
                                
                                // Inserir no banco de dados local
                                for (TarefaPreparo tarefa : tarefas) {
                                    tarefaViewModel.inserirTarefa(tarefa);
                                }
                                
                                tarefasGeradas[0] += tarefas.size();
                            }
                        }
                        
                        diasProcessados[0]++;
                        
                        if (diasProcessados[0] == 3) {
                            // Terminou de processar os 3 dias
                            btnGerarTarefasProximos3Dias.setEnabled(true);
                            btnGerarTarefasProximos3Dias.setText("⚡ Gerar Tarefas (3 dias)");
                            
                            if (tarefasGeradas[0] > 0) {
                                Toast.makeText(CardapioSemanalActivity.this, 
                                    "✓ " + tarefasGeradas[0] + " tarefas geradas para os próximos 3 dias!", 
                                    Toast.LENGTH_LONG).show();
                                
                                // Enviar notificação usando o helper corrigido
                                NotificationHelper.notificarTarefasGeradas(
                                    CardapioSemanalActivity.this,
                                    "Tarefas Geradas",
                                    tarefasGeradas[0] + " novas tarefas foram criadas para os próximos 3 dias"
                                );
                            } else {
                                Toast.makeText(CardapioSemanalActivity.this, 
                                    "⚠ Nenhum cardápio encontrado para os próximos 3 dias", 
                                    Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        diasProcessados[0]++;
                        
                        if (diasProcessados[0] == 3) {
                            btnGerarTarefasProximos3Dias.setEnabled(true);
                            btnGerarTarefasProximos3Dias.setText("⚡ Gerar Tarefas (3 dias)");
                            Toast.makeText(CardapioSemanalActivity.this, 
                                "Erro ao carregar cardápios", 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
}
