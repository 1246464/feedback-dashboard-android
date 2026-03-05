package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListaComprasActivity extends AppCompatActivity {
    
    private DatabaseReference database;
    private LinearLayout containerLista;
    private TextView txtTotalItens, txtItensComprados, txtPeriodo;
    private Button btnVoltar, btnLimparTudo, btnMarcarTudo;
    
    private String[] datasProximos7Dias = new String[7];
    private Map<String, Integer> contagemIngredientes = new HashMap<>();
    private Map<String, List<String>> detalhamentoDias = new HashMap<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);
        
        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();
        
        inicializarComponentes();
        calcularDatasProximos7Dias();
        carregarCardapiosDaSemana();
    }
    
    private void inicializarComponentes() {
        containerLista = findViewById(R.id.containerListaCompras);
        txtTotalItens = findViewById(R.id.txtTotalItens);
        txtItensComprados = findViewById(R.id.txtItensComprados);
        txtPeriodo = findViewById(R.id.txtPeriodoCompras);
        btnVoltar = findViewById(R.id.btnVoltarCompras);
        btnLimparTudo = findViewById(R.id.btnLimparTudo);
        btnMarcarTudo = findViewById(R.id.btnMarcarTudo);
        
        // Exibir período
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar inicio = Calendar.getInstance();
        Calendar fim = Calendar.getInstance();
        fim.add(Calendar.DAY_OF_YEAR, 6);
        txtPeriodo.setText("Período: " + sdf.format(inicio.getTime()) + " a " + sdf.format(fim.getTime()));
        
        btnVoltar.setOnClickListener(v -> finish());
        btnLimparTudo.setOnClickListener(v -> desmarcarTodos());
        btnMarcarTudo.setOnClickListener(v -> marcarTodos());
    }
    
    private void calcularDatasProximos7Dias() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        for (int i = 0; i < 7; i++) {
            datasProximos7Dias[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
    
    private void carregarCardapiosDaSemana() {
        final int[] diasCarregados = {0};
        
        for (int i = 0; i < 7; i++) {
            final int diaIndex = i;
            final String nomeDia = getNomeDia(i);
            
            database.child("cardapios").child(datasProximos7Dias[i])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            processarCardapioDoDia(snapshot, nomeDia);
                        }
                        
                        diasCarregados[0]++;
                        if (diasCarregados[0] == 7) {
                            // Todos os dias carregados, montar lista
                            montarListaDeCompras();
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        diasCarregados[0]++;
                        if (diasCarregados[0] == 7) {
                            montarListaDeCompras();
                        }
                    }
                });
        }
    }
    
    private void processarCardapioDoDia(DataSnapshot snapshot, String nomeDia) {
        String[] ingredientes = {
            snapshot.child("pratoPrincipal").getValue(String.class),
            snapshot.child("guarnicao").getValue(String.class),
            snapshot.child("acompanhamento").getValue(String.class),
            snapshot.child("salada").getValue(String.class),
            snapshot.child("sobremesa").getValue(String.class)
        };
        
        for (String ingrediente : ingredientes) {
            if (ingrediente != null && !ingrediente.trim().isEmpty()) {
                // Contar quantas vezes aparece
                contagemIngredientes.put(ingrediente, 
                    contagemIngredientes.getOrDefault(ingrediente, 0) + 1);
                
                // Detalhar em quais dias
                if (!detalhamentoDias.containsKey(ingrediente)) {
                    detalhamentoDias.put(ingrediente, new ArrayList<>());
                }
                detalhamentoDias.get(ingrediente).add(nomeDia);
            }
        }
    }
    
    private void montarListaDeCompras() {
        containerLista.removeAllViews();
        
        if (contagemIngredientes.isEmpty()) {
            TextView txtVazio = new TextView(this);
            txtVazio.setText("📋 Nenhum cardápio cadastrado para esta semana");
            txtVazio.setTextSize(16);
            txtVazio.setPadding(20, 40, 20, 20);
            containerLista.addView(txtVazio);
            txtTotalItens.setText("0 itens");
            return;
        }
        
        // Ordenar alfabeticamente
        List<String> ingredientesOrdenados = new ArrayList<>(contagemIngredientes.keySet());
        ingredientesOrdenados.sort(String::compareToIgnoreCase);
        
        for (String ingrediente : ingredientesOrdenados) {
            adicionarItemNaLista(ingrediente, contagemIngredientes.get(ingrediente));
        }
        
        txtTotalItens.setText(contagemIngredientes.size() + " itens");
        txtItensComprados.setText("0 comprados");
    }
    
    private void adicionarItemNaLista(String ingrediente, int diasUsado) {
        LinearLayout itemView = (LinearLayout) getLayoutInflater()
            .inflate(R.layout.item_lista_compras, containerLista, false);
        
        CheckBox checkBox = itemView.findViewById(R.id.checkItemCompra);
        TextView txtNome = itemView.findViewById(R.id.txtNomeItemCompra);
        TextView txtDetalhes = itemView.findViewById(R.id.txtDetalhesItemCompra);
        
        checkBox.setText(ingrediente);
        txtNome.setText(ingrediente);
        
        // Mostrar em quantos dias é usado
        String detalhe = "Usado em " + diasUsado + (diasUsado == 1 ? " dia" : " dias");
        if (detalhamentoDias.containsKey(ingrediente)) {
            List<String> dias = detalhamentoDias.get(ingrediente);
            detalhe += " (" + String.join(", ", dias) + ")";
        }
        txtDetalhes.setText(detalhe);
        
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            atualizarContador();
            if (isChecked) {
                txtNome.setPaintFlags(txtNome.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                txtDetalhes.setTextColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                txtNome.setPaintFlags(txtNome.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                txtDetalhes.setTextColor(getResources().getColor(android.R.color.black));
            }
        });
        
        containerLista.addView(itemView);
    }
    
    private void atualizarContador() {
        int total = containerLista.getChildCount();
        int comprados = 0;
        
        for (int i = 0; i < total; i++) {
            LinearLayout item = (LinearLayout) containerLista.getChildAt(i);
            CheckBox checkBox = item.findViewById(R.id.checkItemCompra);
            if (checkBox != null && checkBox.isChecked()) {
                comprados++;
            }
        }
        
        txtItensComprados.setText(comprados + " comprados");
        
        if (comprados == total && total > 0) {
            Toast.makeText(this, "🎉 Lista completa!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void marcarTodos() {
        for (int i = 0; i < containerLista.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) containerLista.getChildAt(i);
            CheckBox checkBox = item.findViewById(R.id.checkItemCompra);
            if (checkBox != null) {
                checkBox.setChecked(true);
            }
        }
    }
    
    private void desmarcarTodos() {
        for (int i = 0; i < containerLista.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) containerLista.getChildAt(i);
            CheckBox checkBox = item.findViewById(R.id.checkItemCompra);
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
        }
    }
    
    private String getNomeDia(int index) {
        String[] nomes = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"};
        return nomes[index % 7];
    }
}
