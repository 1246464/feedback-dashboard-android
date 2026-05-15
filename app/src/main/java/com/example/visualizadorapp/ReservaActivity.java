package com.example.visualizadorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.visualizadorapp.model.Reserva;
import com.example.visualizadorapp.viewmodel.CardapioViewModel;
import com.example.visualizadorapp.viewmodel.ReservaViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.visualizadorapp.util.TurnoUtil;
import com.example.visualizadorapp.util.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReservaActivity extends AppCompatActivity {
    private TextView txtPratoPrincipal, txtGuarnicao, txtAcompanhamento;
    private TextView txtSalada, txtSobremesa, txtReservaAtiva;
    private TextInputEditText edtObservacao;
    private MaterialButton btnReservar, btnCancelarReserva, btnVoltar;
    private MaterialCardView cardMinhasReservas;
    
    private CardapioViewModel cardapioViewModel;
    private ReservaViewModel reservaViewModel;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private String dataHoje;
    private Reserva reservaAtual;
    private String setorUsuario;
    private String escolhaUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_reserva);

            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();
            dataHoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Inicializar ViewModels usando o factory padrão do Android (AndroidViewModelFactory)
            // O CardapioViewModel já gerencia internamente o CardapioRepository
            cardapioViewModel = new ViewModelProvider(this).get(CardapioViewModel.class);
            reservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);

            // Inicializar componentes
            txtPratoPrincipal = findViewById(R.id.txtPratoPrincipal);
            txtGuarnicao = findViewById(R.id.txtGuarnicao);
            txtAcompanhamento = findViewById(R.id.txtAcompanhamento);
            txtSalada = findViewById(R.id.txtSalada);
            txtSobremesa = findViewById(R.id.txtSobremesa);
            txtReservaAtiva = findViewById(R.id.txtReservaAtiva);
            edtObservacao = findViewById(R.id.edtObservacao);
            btnReservar = findViewById(R.id.btnReservar);
            btnCancelarReserva = findViewById(R.id.btnCancelarReserva);
            btnVoltar = findViewById(R.id.btnVoltar);
            cardMinhasReservas = findViewById(R.id.cardMinhasReservas);

            if (txtPratoPrincipal == null || btnReservar == null) {
                Toast.makeText(this, "Erro ao carregar interface", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            loadCardapio();
            checkReservaAtiva();
            carregarDadosUsuario();

            btnReservar.setOnClickListener(v -> fazerReserva());
            btnCancelarReserva.setOnClickListener(v -> cancelarReserva());
            btnVoltar.setOnClickListener(v -> finish());
        } catch (Exception e) {
            android.util.Log.e("ReservaActivity", "❌ ERRO no onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Erro ao inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void carregarDadosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        database.child("usuarios").child(uid).child("setor")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    setorUsuario = snapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

        database.child("escolhas").child(dataHoje).child(uid).child("escolha")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    escolhaUsuario = snapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }

    private void loadCardapio() {
        try {
            cardapioViewModel.getCardapioByData(dataHoje).observe(this, cardapio -> {
                if (cardapio != null) {
                    if (txtPratoPrincipal != null) {
                        txtPratoPrincipal.setText("🍗 Prato: " + (cardapio.getPratoPrincipal() != null ? cardapio.getPratoPrincipal() : "---"));
                    }
                    if (txtGuarnicao != null) {
                        txtGuarnicao.setText("🍖 Guarnição: " + (cardapio.getGuarnicao() != null ? cardapio.getGuarnicao() : "---"));
                    }
                    if (txtAcompanhamento != null) {
                        txtAcompanhamento.setText("🍚 Acompanhamento: " + (cardapio.getAcompanhamento() != null ? cardapio.getAcompanhamento() : "---"));
                    }
                    if (txtSalada != null) {
                        txtSalada.setText("🥗 Salada: " + (cardapio.getSalada() != null ? cardapio.getSalada() : "---"));
                    }
                    if (txtSobremesa != null) {
                        txtSobremesa.setText("🍮 Sobremesa: " + (cardapio.getSobremesa() != null ? cardapio.getSobremesa() : "---"));
                    }
                } else {
                    Toast.makeText(ReservaActivity.this, "Cardápio do dia não disponível", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            android.util.Log.e("ReservaActivity", "Erro ao exibir cardápio: " + e.getMessage());
        }
    }

    private void checkReservaAtiva() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            reservaViewModel.getReservasByData(dataHoje).observe(this, reservas -> {
                Reserva reservaEncontrada = null;
                if (reservas != null) {
                    for (Reserva r : reservas) {
                        if (r != null && user.getUid().equals(r.getUserId()) && "ATIVA".equals(r.getStatusReserva())) {
                            reservaEncontrada = r;
                            break;
                        }
                    }
                }
                
                if (reservaEncontrada != null) {
                    reservaAtual = reservaEncontrada;
                    mostrarReservaAtiva(reservaEncontrada);
                } else {
                    reservaAtual = null;
                    if (cardMinhasReservas != null) cardMinhasReservas.setVisibility(View.GONE);
                    if (btnReservar != null) {
                        btnReservar.setEnabled(true);
                        btnReservar.setText("Fazer Reserva");
                    }
                }
            });
        }
    }

    private void mostrarReservaAtiva(Reserva reserva) {
        if (cardMinhasReservas != null) cardMinhasReservas.setVisibility(View.VISIBLE);
        if (btnReservar != null) {
            btnReservar.setEnabled(false);
            btnReservar.setText("Você já tem uma reserva");
        }
        if (txtReservaAtiva != null) {
            String turno = reserva.getTurno() != null ? reserva.getTurno() : "Não especificado";
            txtReservaAtiva.setText("Reserva ativa para " + turno + "\nStatus: " + reserva.getStatusReserva());
        }
    }

    private void fazerReserva() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Você precisa estar logado", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean podeAlmoco = TurnoUtil.podeReservar(TurnoUtil.TURNO_ALMOCO);
        boolean podeJanta = TurnoUtil.podeReservar(TurnoUtil.TURNO_JANTA);

        if (!podeAlmoco && !podeJanta) {
            Toast.makeText(this, "Fora do horário de reserva", Toast.LENGTH_LONG).show();
            return;
        }

        String[] turnos;
        String[] mensagens;
        
        if (podeAlmoco && podeJanta) {
            turnos = new String[]{TurnoUtil.TURNO_ALMOCO, TurnoUtil.TURNO_JANTA};
            mensagens = new String[]{
                "🍽️ ALMOÇO (11:00 às 14:30)\nRetirada até 18:00",
                "🍽️ JANTA (18:00 às 22:00)\nRetirada até 00:00"
            };
        } else if (podeAlmoco) {
            turnos = new String[]{TurnoUtil.TURNO_ALMOCO};
            mensagens = new String[]{"🍽️ ALMOÇO (11:00 às 14:30)\nRetirada até 18:00"};
        } else {
            turnos = new String[]{TurnoUtil.TURNO_JANTA};
            mensagens = new String[]{"🍽️ JANTA (18:00 às 22:00)\nRetirada até 00:00"};
        }

        if (turnos.length == 1) {
            confirmarReserva(user, turnos[0]);
        } else {
            new AlertDialog.Builder(this)
                .setTitle("Escolha o Turno")
                .setItems(mensagens, (dialog, which) -> confirmarReserva(user, turnos[which]))
                .setNegativeButton("Cancelar", null)
                .show();
        }
    }

    private void confirmarReserva(FirebaseUser user, String turno) {
        new AlertDialog.Builder(this)
            .setTitle("Confirmar Reserva")
            .setMessage("Deseja reservar para o " + turno + "?")
            .setPositiveButton("Confirmar", (dialog, which) -> {
                Reserva r = new Reserva();
                r.setUserId(user.getUid());
                r.setData(dataHoje);
                r.setNomeUsuario(user.getDisplayName() != null ? user.getDisplayName() : "Usuário");
                r.setEmailUsuario(user.getEmail());
                r.setObservacao(edtObservacao.getText() != null ? edtObservacao.getText().toString() : "");
                r.setStatusReserva("ATIVA");
                r.setSetor(setorUsuario != null ? setorUsuario : "Não informado");
                r.setEscolhaPrato(escolhaUsuario != null ? escolhaUsuario : "Não votou");
                r.setTurno(turno);
                
                reservaViewModel.insert(r, () -> {
                    Toast.makeText(ReservaActivity.this, "Reserva realizada!", Toast.LENGTH_SHORT).show();
                    edtObservacao.setText("");
                    enviarNotificacaoReservaSegura(r.getNomeUsuario(), turno, r.getEscolhaPrato());
                    checkReservaAtiva();
                });
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void enviarNotificacaoReservaSegura(String nome, String turno, String prato) {
        android.util.Log.d("Reserva", "enviarNotificacaoReservaSegura: " + nome + " | " + turno + " | " + prato);
        // Notificar usuário que fez a reserva
        NotificationHelper.notificarNovaReserva(ReservaActivity.this, 
            "Você (confirmação)", turno, "✅ Sua reserva foi confirmada!");
        
        // Notificar equipe de cozinha/copa
        database.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String cargo = userSnapshot.child("cargo").getValue(String.class);
                    
                    // Notificar se cargo contém "COZINHA", "COPEI", "MEIO_OFICIAL" ou "AUXILIAR"
                    if (cargo != null && (
                        cargo.contains("COZINHA") || 
                        cargo.contains("COPEI") || 
                        cargo.contains("MEIO_OFICIAL") ||
                        cargo.contains("AUXILIAR") ||
                        cargo.equals("LIDER_COZINHA"))) {
                        
                        NotificationHelper.notificarNovaReserva(ReservaActivity.this, nome, turno, prato);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void cancelarReserva() {
        if (reservaAtual == null) return;
        new AlertDialog.Builder(this)
            .setTitle("Cancelar Reserva")
            .setMessage("Tem certeza?")
            .setPositiveButton("Sim", (dialog, which) -> {
                reservaViewModel.updateStatus(reservaAtual.getId(), "CANCELADA");
                checkReservaAtiva();
            })
            .setNegativeButton("Não", null)
            .show();
    }
}
