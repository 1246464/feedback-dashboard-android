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
import androidx.lifecycle.ViewModelProvider;
import com.example.visualizadorapp.model.MudancaCardapio;
import com.example.visualizadorapp.model.PassagemTurno;
import com.example.visualizadorapp.model.TarefaPreparo;
import com.example.visualizadorapp.util.AutoTaskGenerator;
import com.example.visualizadorapp.util.NotificationHelper;
import com.example.visualizadorapp.viewmodel.GestaoPreparoViewModel;
import com.example.visualizadorapp.viewmodel.MudancaCardapioViewModel;
import com.example.visualizadorapp.viewmodel.PassagemTurnoViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import java.util.List;
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
    
    // ViewModels para integração
    private GestaoPreparoViewModel gestaoPreparoViewModel;
    private MudancaCardapioViewModel mudancaCardapioViewModel;
    private PassagemTurnoViewModel passagemTurnoViewModel;
    
    // Cache do cardápio atual (para detectar mudanças)
    private String cardapioAnteriorPrato;
    private String cardapioAnteriorGuarnicao;
    private String cardapioAnteriorAcompanhamento;
    private String cardapioAnteriorSalada;
    private String cardapioAnteriorSobremesa;

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
        Button btnGestaoPreparo = findViewById(R.id.btnGestaoPreparo);
        Button btnPassagemTurno = findViewById(R.id.btnPassagemTurno);
        Button btnGestaoIngredientes = findViewById(R.id.btnGestaoIngredientes);
        Button btnDashboardPreparo = findViewById(R.id.btnDashboardPreparo);
        Button btnListaReservas = findViewById(R.id.btnListaReservas);
        Button btnGerenciarUsuarios = findViewById(R.id.btnGerenciarUsuarios);
        
        // Inicializar ViewModels
        gestaoPreparoViewModel = new ViewModelProvider(this).get(GestaoPreparoViewModel.class);
        mudancaCardapioViewModel = new ViewModelProvider(this).get(MudancaCardapioViewModel.class);
        passagemTurnoViewModel = new ViewModelProvider(this).get(PassagemTurnoViewModel.class);

        btnEstatisticas.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, EstatisticasActivity.class));
        });

        btnComentarios.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ComentariosActivity.class));
        });

        btnCardapioSemanal.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, CardapioSemanalActivity.class));
        });

        btnGestaoPreparo.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, GestaoPreparoActivity.class));
        });

        btnPassagemTurno.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, PassagemTurnoActivity.class));
        });
        
        btnGestaoIngredientes.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, GestaoIngredientesActivity.class));
        });
        
        btnDashboardPreparo.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, DashboardPreparoActivity.class));
        });

        btnListaReservas.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ListaReservasActivity.class));
        });

        btnGerenciarUsuarios.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, GerenciarUsuariosActivity.class));
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
                    
                    // Atualiza cache para detecção de mudanças
                    cardapioAnteriorPrato = prato;
                    cardapioAnteriorGuarnicao = guarnicao;
                    cardapioAnteriorAcompanhamento = acompanhamento;
                    cardapioAnteriorSalada = salada;
                    cardapioAnteriorSobremesa = sobremesa;
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
        database.child("cardapios").child(dataHoje).setValue(cardapio).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "✓ Cardápio atualizado! Gerando tarefas...", Toast.LENGTH_SHORT).show();
                
                // ========== INTEGRAÇÃO AUTOMÁTICA ==========
                processarIntegracaoAutomatica(
                    pratoPrincipal, guarnicao, acompanhamento, salada, sobremesa
                );
            })
            .addOnFailureListener(e ->
                Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    
    /**
     * Processa toda a lógica de integração automática:
     * 1. Detecta mudanças no cardápio
     * 2. Registra histórico de mudanças
     * 3. Gera tarefas de preparo automaticamente
     * 4. Cria passagem de turno
     * 5. Envia notificações
     */
    private void processarIntegracaoAutomatica(
            String pratoPrincipal,
            String guarnicao,
            String acompanhamento,
            String salada,
            String sobremesa) {
        
        boolean houveMudanca = false;
        StringBuilder mensagemMudancas = new StringBuilder();
        mensagemMudancas.append("MUDANÇAS NO CARDÁPIO DE HOJE:\n\n");
        
        String usuarioLogado = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getEmail() : "Admin";
        
        // 1. DETECTAR E REGISTRAR MUDANÇAS
        if (AutoTaskGenerator.houveMudanca(cardapioAnteriorPrato, pratoPrincipal)) {
            registrarMudanca("PRATO_PRINCIPAL", cardapioAnteriorPrato, pratoPrincipal, usuarioLogado);
            mensagemMudancas.append("🍗 Prato Principal: ").append(pratoPrincipal).append("\n");
            houveMudanca = true;
        }
        
        if (AutoTaskGenerator.houveMudanca(cardapioAnteriorGuarnicao, guarnicao)) {
            registrarMudanca("GUARNICAO", cardapioAnteriorGuarnicao, guarnicao, usuarioLogado);
            mensagemMudancas.append("🍖 Guarnição: ").append(guarnicao).append("\n");
            houveMudanca = true;
        }
        
        if (AutoTaskGenerator.houveMudanca(cardapioAnteriorAcompanhamento, acompanhamento)) {
            registrarMudanca("ACOMPANHAMENTO", cardapioAnteriorAcompanhamento, acompanhamento, usuarioLogado);
            mensagemMudancas.append("🍚 Acompanhamento: ").append(acompanhamento).append("\n");
            houveMudanca = true;
        }
        
        if (AutoTaskGenerator.houveMudanca(cardapioAnteriorSalada, salada)) {
            registrarMudanca("SALADA", cardapioAnteriorSalada, salada, usuarioLogado);
            mensagemMudancas.append("🥗 Salada: ").append(salada).append("\n");
            houveMudanca = true;
        }
        
        if (AutoTaskGenerator.houveMudanca(cardapioAnteriorSobremesa, sobremesa)) {
            registrarMudanca("SOBREMESA", cardapioAnteriorSobremesa, sobremesa, usuarioLogado);
            mensagemMudancas.append("🍮 Sobremesa: ").append(sobremesa).append("\n");
            houveMudanca = true;
        }
        
        // 2. GERAR TAREFAS AUTOMATICAMENTE
        List<TarefaPreparo> tarefasGeradas = AutoTaskGenerator.gerarTarefasDoCardapio(
            dataHoje, pratoPrincipal, guarnicao, acompanhamento, salada, sobremesa
        );
        
        for (TarefaPreparo tarefa : tarefasGeradas) {
            gestaoPreparoViewModel.inserirTarefa(tarefa);
        }
        
        Log.d("AdminActivity", "Geradas " + tarefasGeradas.size() + " tarefas automaticamente");
        
        // 3. CRIAR PASSAGEM DE TURNO AUTOMÁTICA
        if (houveMudanca) {
            mensagemMudancas.append("\n⚡ Tarefas de preparo atualizadas!");
            criarPassagemTurnoAutomatica(mensagemMudancas.toString());
        }
        
        // 4. ENVIAR NOTIFICAÇÃO PUSH
        if (houveMudanca) {
            enviarNotificacaoMudanca("Cardápio Alterado", 
                "O cardápio de hoje foi modificado. Verifique as novas tarefas.");
        }
        
        Toast.makeText(this, 
            "✓ " + tarefasGeradas.size() + " tarefas criadas automaticamente!", 
            Toast.LENGTH_LONG).show();
    }
    
    /**
     * Registra mudança no histórico
     */
    private void registrarMudanca(String itemAlterado, String valorAnterior, 
                                  String valorNovo, String usuario) {
        MudancaCardapio mudanca = new MudancaCardapio();
        mudanca.setData(dataHoje);
        mudanca.setItemAlterado(itemAlterado);
        mudanca.setValorAnterior(valorAnterior != null ? valorAnterior : "(vazio)");
        mudanca.setValorNovo(valorNovo);
        mudanca.setUsuarioResponsavel(usuario);
        mudanca.setMotivoMudanca("Atualização via painel Admin");
        mudanca.setNotificadoTurnos(false);
        
        mudancaCardapioViewModel.inserirMudanca(mudanca);
    }
    
    /**
     * Cria passagem de turno alertando sobre mudanças
     */
    private void criarPassagemTurnoAutomatica(String mensagem) {
        PassagemTurno passagem = new PassagemTurno();
        passagem.setData(dataHoje);
        passagem.setTurnoOrigem("ADMIN");
        passagem.setTurnoDestino("TODOS");
        passagem.setUsuarioOrigem("Sistema Automático");
        passagem.setMensagem(mensagem);
        passagem.setTipoMensagem("ALERTA");
        passagem.setMudancas(mensagem);
        passagem.setLida(false);
        
        passagemTurnoViewModel.inserirPassagem(passagem);
    }
    
    /**
     * Envia notificação push via Firebase Cloud Messaging
     */
    private void enviarNotificacaoMudanca(String titulo, String corpo) {
        // Enviar para tópico "kitchen_staff"
        try {
            Map<String, String> data = new HashMap<>();
            data.put("tipo", "mudanca_cardapio");
            data.put("data", dataHoje);
            data.put("titulo", titulo);
            data.put("mensagem", corpo);
            
            // Publicar no tópico
            database.child("notifications").child("mudancas").push().setValue(data);
            
            // Enviar notificação local também
            NotificationHelper.notificarMudancaCardapio(this, corpo);
            
            Log.d("AdminActivity", "Notificação enviada: " + titulo);
        } catch (Exception e) {
            Log.e("AdminActivity", "Erro ao enviar notificação: " + e.getMessage());
        }
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