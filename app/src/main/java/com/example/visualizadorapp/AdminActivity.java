package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity {
    private EditText edtPrincipal, edtAcompanhamento, edtSalada, edtSobremesa, edtGuarnicao;
    private TextView txtContagemOvos, txtContagemPrato, txtTotalPedidos, txtP_View, txtG_View, txtA_View, txtS_View, txtSob_View;
    private Button btnSalvar, btnSair, btnLimpar, btnVoltar;
    private DatabaseReference database;
    private String dataHoje;
    private ValueEventListener cardapioListener;
    private ValueEventListener escolhasListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Referência correta com a URL do seu banco
        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();

        // Data de hoje para organizar o banco
        dataHoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Inicializar componentes
        edtPrincipal = findViewById(R.id.edtPrincipal);
        edtAcompanhamento = findViewById(R.id.edtAcompanhamento);
        edtSalada = findViewById(R.id.edtSalada);
        edtSobremesa = findViewById(R.id.edtSobremesa);
        edtGuarnicao = findViewById(R.id.edtGuarnicao);
        txtP_View = findViewById(R.id.txtPrincipalView);
        txtG_View = findViewById(R.id.txtGuarnicaoView);
        txtA_View = findViewById(R.id.txtAcompanhamentoView);
        txtS_View = findViewById(R.id.txtSaladaView);
        txtSob_View = findViewById(R.id.txtSobremesaView);
        txtContagemOvos = findViewById(R.id.txtContagemOvo);
        txtContagemPrato = findViewById(R.id.txtContagemPrato);
        txtTotalPedidos = findViewById(R.id.txtTotalPedidos);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSair = findViewById(R.id.btnSair);
        btnLimpar = findViewById(R.id.btnLimpar);
        btnVoltar = findViewById(R.id.btnVoltar);
        Button btnEstatisticas = findViewById(R.id.btnIrParaEstatisticas);
        Button btnComentarios = findViewById(R.id.btnVerComentarios);
        Button btnCardapioSemanal = findViewById(R.id.btnCardapioSemanal);

        btnEstatisticas.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, EstatisticasActivity.class));
        });

        btnComentarios.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ComentariosActivity.class));
        });

        btnCardapioSemanal.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, CardapioSemanalActivity.class));
        });

        // LISTENER DO CARDÁPIO (Usando a data de hoje e corrigindo o nome da pasta para "cardapio")
        cardapioListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Pegando os valores de forma segura
                    String prato = snapshot.child("pratoPrincipal").getValue(String.class);
                    String guarnicao = snapshot.child("guarnicao").getValue(String.class);
                    String acompanhamento = snapshot.child("acompanhamento").getValue(String.class);
                    String salada = snapshot.child("salada").getValue(String.class);
                    String sobremesa = snapshot.child("sobremesa").getValue(String.class);

                    // Atualiza Visualização
                    txtP_View.setText("🍗 Prato: " + (prato != null ? prato : "---"));
                    txtG_View.setText("🍖 Guarnição: " + (guarnicao != null ? guarnicao : "---"));
                    txtA_View.setText("🍚 Acomp: " + (acompanhamento != null ? acompanhamento : "---"));
                    txtS_View.setText("🥗 Salada: " + (salada != null ? salada : "---"));
                    txtSob_View.setText("🍮 Sobremesa: " + (sobremesa != null ? sobremesa : "---"));

                    // Preenche campos de edição
                    edtPrincipal.setText(prato);
                    edtGuarnicao.setText(guarnicao);
                    edtAcompanhamento.setText(acompanhamento);
                    edtSalada.setText(salada);
                    edtSobremesa.setText(sobremesa);
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        };
        database.child("cardapios").child(dataHoje).addValueEventListener(cardapioListener);

        // LISTENER DE OVOS (Corrigido para escutar data específica e verificar campo "escolha")
        escolhasListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int contadorOvos = 0;
                int contadorPrato = 0;
                Log.d("AdminActivity", "Total de votos recebidos: " + snapshot.getChildrenCount());
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    try {
                        // Acessa o campo "escolha" dentro do objeto de cada usuário
                        String escolha = userSnapshot.child("escolha").getValue(String.class);
                        Log.d("AdminActivity", "Escolha encontrada: " + escolha);
                        if (escolha != null) {
                            if (escolha.equals("Ovo")) {
                                contadorOvos++;
                            } else if (escolha.equals("Prato principal")) {
                                contadorPrato++;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("ErroOvo", "Erro ao ler escolha: " + e.getMessage());
                    }
                }
                txtContagemOvos.setText(String.valueOf(contadorOvos));
                txtContagemPrato.setText(String.valueOf(contadorPrato));
                txtTotalPedidos.setText((contadorOvos + contadorPrato) + " pedidos");
            }
            @Override public void onCancelled(DatabaseError error) {
                Log.e("ErroOvo", "Erro ao acessar banco: " + error.getMessage());
            }
        };
        database.child("escolhas").child(dataHoje).addValueEventListener(escolhasListener);

        btnSalvar.setOnClickListener(v -> salvarCardapio());

        btnVoltar.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, MainActivity.class));
            finish();
        });

        btnLimpar.setOnClickListener(v -> {
            // Adicionar confirmação antes de limpar
            new AlertDialog.Builder(this)
                .setTitle("Confirmar exclusão")
                .setMessage("Deseja realmente apagar todos os pedidos de hoje? Esta ação não pode ser desfeita.")
                .setPositiveButton("Sim, apagar", (dialog, which) -> {
                    // Corrigido: Remove apenas as escolhas do dia atual, preservando o histórico
                    database.child("escolhas").child(dataHoje).removeValue().addOnSuccessListener(aVoid ->
                            Toast.makeText(AdminActivity.this, "Pedidos de hoje zerados!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                            Toast.makeText(AdminActivity.this, "Erro ao limpar pedidos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        });

        btnSair.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void salvarCardapio() {
        String pratoPrincipal = edtPrincipal.getText().toString().trim();
        String acompanhamento = edtAcompanhamento.getText().toString().trim();
        String salada = edtSalada.getText().toString().trim();
        String sobremesa = edtSobremesa.getText().toString().trim();
        String guarnicao = edtGuarnicao.getText().toString().trim();

        // Validação de campos vazios
        if (pratoPrincipal.isEmpty() || acompanhamento.isEmpty() || salada.isEmpty() || 
            sobremesa.isEmpty() || guarnicao.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos do cardápio", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> cardapio = new HashMap<>();
        cardapio.put("pratoPrincipal", pratoPrincipal);
        cardapio.put("acompanhamento", acompanhamento);
        cardapio.put("salada", salada);
        cardapio.put("sobremesa", sobremesa);
        cardapio.put("guarnicao", guarnicao);

        // SALVANDO NA DATA DE HOJE (Igual ao que o Listener procura)
        database.child("cardapios").child(dataHoje).setValue(cardapio).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "Cardápio de hoje atualizado!", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e ->
                Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove listeners para evitar memory leaks
        if (cardapioListener != null) {
            database.child("cardapios").child(dataHoje).removeEventListener(cardapioListener);
        }
        if (escolhasListener != null) {
            database.child("escolhas").child(dataHoje).removeEventListener(escolhasListener);
        }
    }
}