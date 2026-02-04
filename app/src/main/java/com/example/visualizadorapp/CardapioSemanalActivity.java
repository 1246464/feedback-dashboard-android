package com.example.visualizadorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
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

public class CardapioSemanalActivity extends AppCompatActivity {

    private DatabaseReference database;
    private LinearLayout containerSemanal;
    private Button btnSalvarSemana;
    private Button btnVoltarAdmin;
    
    // Arrays para armazenar os EditTexts de cada dia
    private EditText[] edtPrincipal = new EditText[7];
    private EditText[] edtGuarnicao = new EditText[7];
    private EditText[] edtAcompanhamento = new EditText[7];
    private EditText[] edtSalada = new EditText[7];
    private EditText[] edtSobremesa = new EditText[7];
    
    private String[] datasProximos7Dias = new String[7];
    private String[] nomeDias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio_semanal);

        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();
        
        containerSemanal = findViewById(R.id.containerCardapioSemanal);
        btnSalvarSemana = findViewById(R.id.btnSalvarSemana);
        btnVoltarAdmin = findViewById(R.id.btnVoltarAdmin);

        calcularDatasProximos7Dias();
        criarFormulariosSemana();
        carregarCardapiosSemana();

        btnSalvarSemana.setOnClickListener(v -> salvarCardapiosSemana());
        
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
            return sdfFormatado.format(sdfOriginal.parse(data));
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
            }
        }
        
        final int totalSalvos = diasSalvos;
        
        // Aguardar um pouco para dar tempo de salvar
        containerSemanal.postDelayed(() -> {
            btnSalvarSemana.setEnabled(true);
            btnSalvarSemana.setText("💾 Salvar Cardápios da Semana");
            Toast.makeText(this, "✓ " + totalSalvos + " cardápio(s) salvos com sucesso!", Toast.LENGTH_SHORT).show();
        }, 1000);
    }
}
