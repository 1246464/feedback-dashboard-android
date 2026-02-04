package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference database;
    private ProgressBar progressBar;
    private ScrollView contentScrollView;
    private TextView txtMensagemVoto;
    private Button btnConfirmarEscolha, btnVerEstatisticas, btnEnviarAvaliacao, btnSugestoes;
    private RadioGroup radioGrupoEscolha, radioGrupoAvaliacao;
    private EditText edtComentario;
    private LinearLayout cardapioContainer;
    private String hoje;
    private String setorUsuario; // Cache do setor do usuário
    private ValueEventListener cardapioListener;
    private boolean cardapioCarregado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();
        String uid = currentUser.getUid();
        hoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // --- Inicialização dos Views ---
        iniciarViews();

        // --- Configurações Iniciais ---
        mostrarCarregamento(true);
        carregarDadosUsuario(uid);
        verificarTipoUsuario(uid);
        ouvirCardapio();
        verificarVotosDoDia(uid, hoje);
        verificarAvaliacaoDoDia(uid, hoje);

        // --- Listeners de Clique ---
        configurarListeners(uid, hoje);
    }

    private void iniciarViews() {
        progressBar = findViewById(R.id.progressBar);
        contentScrollView = findViewById(R.id.contentScrollView);
        txtMensagemVoto = findViewById(R.id.txtMensagemVoto);
        btnConfirmarEscolha = findViewById(R.id.btnConfirmarEscolha);
        btnVerEstatisticas = findViewById(R.id.btnVerEstatisticas);
        btnEnviarAvaliacao = findViewById(R.id.btnEnviarAvaliacao);
        btnSugestoes = findViewById(R.id.btnSugestoes);
        radioGrupoEscolha = findViewById(R.id.radioGrupoEscolha);
        radioGrupoAvaliacao = findViewById(R.id.radioGrupoAvaliacao);
        edtComentario = findViewById(R.id.edtComentario);
        cardapioContainer = findViewById(R.id.cardapioContainer);

        // Mostrar data formatada
        TextView txtDataCardapio = findViewById(R.id.txtDataCardapio);
        String dataFormatada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        txtDataCardapio.setText("Data: " + dataFormatada);

        // Inicia com os campos de voto desabilitados
        findViewById(R.id.labelFacaSuaEscolha).setVisibility(View.GONE);
        radioGrupoEscolha.setVisibility(View.GONE);
        btnConfirmarEscolha.setVisibility(View.GONE);
        btnConfirmarEscolha.setEnabled(false); // Desabilita até cardápio carregar
    }

    private void mostrarCarregamento(boolean mostrar) {
        progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        contentScrollView.setVisibility(mostrar ? View.GONE : View.VISIBLE);
    }

    private void carregarDadosUsuario(String uid) {
        database.child("usuarios").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    setorUsuario = snapshot.child("setor").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(contentScrollView, "Erro ao carregar dados do usuário", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void ouvirCardapio() {
        // CORRIGIDO: Ouvindo o nó "cardapios" com a data de hoje
        cardapioListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ((TextView) findViewById(R.id.txtPrincipal)).setText(getString(R.string.prato_principal, snapshot.child("pratoPrincipal").getValue(String.class)));
                    ((TextView) findViewById(R.id.txtGuarnicao)).setText(getString(R.string.guarnicao, snapshot.child("guarnicao").getValue(String.class)));
                    ((TextView) findViewById(R.id.txtAcompanhamento)).setText(getString(R.string.acompanhamento, snapshot.child("acompanhamento").getValue(String.class)));
                    ((TextView) findViewById(R.id.txtSalada)).setText(getString(R.string.salada, snapshot.child("salada").getValue(String.class)));
                    ((TextView) findViewById(R.id.txtSobremesa)).setText(getString(R.string.sobremesa, snapshot.child("sobremesa").getValue(String.class)));
                    cardapioContainer.setVisibility(View.VISIBLE);
                    cardapioCarregado = true;
                    btnConfirmarEscolha.setEnabled(true); // Habilita votação
                } else {
                    cardapioContainer.setVisibility(View.GONE);
                    cardapioCarregado = false;
                    btnConfirmarEscolha.setEnabled(false);
                    
                    // Mensagem mais amigável
                    Snackbar.make(contentScrollView, 
                        "⏳ Cardápio ainda não publicado hoje. Aguarde o administrador cadastrar o menu.", 
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", v -> {})
                        .show();
                }
                mostrarCarregamento(false);
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Snackbar.make(contentScrollView, "Falha ao carregar o cardápio.", Snackbar.LENGTH_LONG).show();
                mostrarCarregamento(false);
            }
        };
        database.child("cardapios").child(hoje).addValueEventListener(cardapioListener);
    }

    private void verificarAvaliacaoDoDia(String uid, String hoje) {
        database.child("avaliacoes").child(hoje).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Se já avaliou, esconde o formulário de avaliação
                    findViewById(R.id.labelAvaliacao).setVisibility(View.GONE);
                    radioGrupoAvaliacao.setVisibility(View.GONE);
                    edtComentario.setVisibility(View.GONE);
                    btnEnviarAvaliacao.setVisibility(View.GONE);
                } else {
                    // Se não avaliou, mostra o formulário de avaliação
                    findViewById(R.id.labelAvaliacao).setVisibility(View.VISIBLE);
                    radioGrupoAvaliacao.setVisibility(View.VISIBLE);
                    edtComentario.setVisibility(View.VISIBLE);
                    btnEnviarAvaliacao.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) { /* Não faz nada aqui */ }
        });
    }

    private void verificarVotosDoDia(String uid, String hoje) {
        database.child("escolhas").child(hoje).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Se já votou, mostra a mensagem e esconde a área de votação
                    txtMensagemVoto.setVisibility(View.VISIBLE);
                    findViewById(R.id.labelFacaSuaEscolha).setVisibility(View.GONE);
                    radioGrupoEscolha.setVisibility(View.GONE);
                    btnConfirmarEscolha.setVisibility(View.GONE);
                } else {
                    // Se não votou, esconde a mensagem e mostra a área de votação
                    txtMensagemVoto.setVisibility(View.GONE);
                    findViewById(R.id.labelFacaSuaEscolha).setVisibility(View.VISIBLE);
                    radioGrupoEscolha.setVisibility(View.VISIBLE);
                    btnConfirmarEscolha.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) { /* Não faz nada aqui */ }
        });
    }

    private void configurarListeners(String uid, String hoje) {
        btnConfirmarEscolha.setOnClickListener(v -> {
            // Verifica se cardápio foi carregado
            if (!cardapioCarregado) {
                Snackbar.make(v, "Aguarde o cardápio ser carregado para votar.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            int idSelecionado = radioGrupoEscolha.getCheckedRadioButtonId();
            if (idSelecionado != -1) {
                RadioButton rb = findViewById(idSelecionado);
                String escolha = rb.getText().toString();

                // Validar se o setor foi carregado
                if (setorUsuario == null || setorUsuario.isEmpty()) {
                    Snackbar.make(v, "Erro: Setor não encontrado. Tente novamente.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                // Desabilitar botão durante operação
                btnConfirmarEscolha.setEnabled(false);
                btnConfirmarEscolha.setText("Enviando...");

                Map<String, Object> dadosEscolha = new HashMap<>();
                dadosEscolha.put("escolha", escolha);
                dadosEscolha.put("setor", setorUsuario);
                dadosEscolha.put("timestamp", System.currentTimeMillis());

                Log.d("MainActivity", "Salvando escolha: " + escolha + " | Setor: " + setorUsuario + " | Data: " + hoje);

                database.child("escolhas").child(hoje).child(uid).setValue(dadosEscolha)
                    .addOnSuccessListener(aVoid -> {
                        Snackbar.make(v, getString(R.string.escolha_registrada), Snackbar.LENGTH_SHORT).show();
                        verificarVotosDoDia(uid, hoje); // Re-verifica para atualizar a UI
                        btnConfirmarEscolha.setEnabled(true);
                        btnConfirmarEscolha.setText("Confirmar Escolha");
                    })
                    .addOnFailureListener(e -> {
                        Snackbar.make(v, "Erro ao registrar escolha: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        btnConfirmarEscolha.setEnabled(true);
                        btnConfirmarEscolha.setText("Confirmar Escolha");
                    });
            } else {
                Snackbar.make(v, getString(R.string.selecione_uma_opcao), Snackbar.LENGTH_SHORT).show();
            }
        });

        btnEnviarAvaliacao.setOnClickListener(v -> {
            int idSelecionado = radioGrupoAvaliacao.getCheckedRadioButtonId();
            if (idSelecionado == -1) {
                Snackbar.make(v, getString(R.string.selecione_uma_opcao), Snackbar.LENGTH_SHORT).show();
                return;
            }
            RadioButton rb = findViewById(idSelecionado);
            String nivelCompleto = rb.getText().toString();
            // Remove emoji e espaço extra (ex: "😊 Bom" -> "Bom")
            String nivel = nivelCompleto.replaceAll("[^a-zA-ZÀ-ÿ\\s]", "").trim();
            String comentario = edtComentario.getText().toString().trim();

            // Validar se o setor foi carregado
            if (setorUsuario == null || setorUsuario.isEmpty()) {
                Snackbar.make(v, "Erro: Setor não encontrado. Tente novamente.", Snackbar.LENGTH_LONG).show();
                return;
            }

            // Desabilitar botão durante operação
            btnEnviarAvaliacao.setEnabled(false);
            btnEnviarAvaliacao.setText("Enviando...");

            Map<String, Object> avaliacao = new HashMap<>();
            avaliacao.put("nivel", nivel);
            avaliacao.put("comentario", comentario);
            avaliacao.put("data", System.currentTimeMillis());
            Log.d("MainActivity", "Salvando avaliação: " + nivel + " (original: " + nivelCompleto + ") | Setor: " + setorUsuario + " | Data: " + hoje);

            avaliacao.put("setor", setorUsuario);

            database.child("avaliacoes").child(hoje).child(uid).setValue(avaliacao).addOnSuccessListener(aVoid -> {
                Snackbar.make(v, getString(R.string.obrigado_pelo_feedback), Snackbar.LENGTH_SHORT).show();
                edtComentario.setText("");
                radioGrupoAvaliacao.clearCheck();
                
                // Esconder formulário após enviar
                findViewById(R.id.labelAvaliacao).setVisibility(View.GONE);
                radioGrupoAvaliacao.setVisibility(View.GONE);
                edtComentario.setVisibility(View.GONE);
                btnEnviarAvaliacao.setVisibility(View.GONE);
                
                btnEnviarAvaliacao.setEnabled(true);
                btnEnviarAvaliacao.setText("Enviar Avaliação");
            }).addOnFailureListener(e -> {
                Snackbar.make(v, "Erro ao enviar avaliação: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                btnEnviarAvaliacao.setEnabled(true);
                btnEnviarAvaliacao.setText("Enviar Avaliação");
            });
        });

        btnVerEstatisticas.setOnClickListener(v -> startActivity(new Intent(this, EstatisticasActivity.class)));

        btnSugestoes.setOnClickListener(v -> startActivity(new Intent(this, SugestoesActivity.class)));

        findViewById(R.id.btnSair).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void verificarTipoUsuario(String uid) {
        database.child("usuarios").child(uid).child("tipo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ("admin".equals(snapshot.getValue(String.class))) {
                    btnVerEstatisticas.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) { /* O botão continua invisível */ }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove listeners para evitar memory leaks
        if (cardapioListener != null) {
            database.child("cardapios").child(hoje).removeEventListener(cardapioListener);
        }
    }
}