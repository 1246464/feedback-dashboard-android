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
        setContentView(R.layout.activity_reserva);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();
        dataHoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Inicializar ViewModels
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

        // Carregar cardápio de hoje
        loadCardapio();

        // Verificar se já tem reserva ativa
        checkReservaAtiva();

        // Carregar dados do usuário (setor e escolha)
        carregarDadosUsuario();

        // Botões
        btnReservar.setOnClickListener(v -> fazerReserva());
        btnCancelarReserva.setOnClickListener(v -> cancelarReserva());
        btnVoltar.setOnClickListener(v -> finish());
    }

    private void carregarDadosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        // Carregar setor
        database.child("usuarios").child(uid).child("setor")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    setorUsuario = snapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Ignorar erro
                }
            });

        // Carregar escolha do dia
        database.child("escolhas").child(dataHoje).child(uid).child("escolha")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    escolhaUsuario = snapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Ignorar erro
                }
            });
    }

    private void loadCardapio() {
        cardapioViewModel.getCardapioByData(dataHoje).observe(this, cardapio -> {
            if (cardapio != null) {
                txtPratoPrincipal.setText("🍗 Prato: " + (cardapio.getPratoPrincipal() != null ? cardapio.getPratoPrincipal() : "---"));
                txtGuarnicao.setText("🍖 Guarnição: " + (cardapio.getGuarnicao() != null ? cardapio.getGuarnicao() : "---"));
                txtAcompanhamento.setText("🍚 Acompanhamento: " + (cardapio.getAcompanhamento() != null ? cardapio.getAcompanhamento() : "---"));
                txtSalada.setText("🥗 Salada: " + (cardapio.getSalada() != null ? cardapio.getSalada() : "---"));
                txtSobremesa.setText("🍮 Sobremesa: " + (cardapio.getSobremesa() != null ? cardapio.getSobremesa() : "---"));
            } else {
                Toast.makeText(this, "Cardápio do dia ainda não disponível", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkReservaAtiva() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            reservaViewModel.getReservaAtiva(dataHoje, user.getUid()).observe(this, reserva -> {
                if (reserva != null) {
                    // Já tem reserva ativa
                    reservaAtual = reserva;
                    cardMinhasReservas.setVisibility(View.VISIBLE);
                    btnReservar.setEnabled(false);
                    btnReservar.setText("Você já tem uma reserva");
                    
                    String observacao = reserva.getObservacao() != null ? reserva.getObservacao() : "";
                    txtReservaAtiva.setText(
                        "Reserva ativa para hoje\n" +
                        "Status: " + reserva.getStatusReserva() + "\n" +
                        (observacao.isEmpty() ? "" : "Observação: " + observacao)
                    );
                } else {
                    // Não tem reserva ativa
                    reservaAtual = null;
                    cardMinhasReservas.setVisibility(View.GONE);
                    btnReservar.setEnabled(true);
                    btnReservar.setText("Fazer Reserva");
                }
            });
        }
    }

    private void fazerReserva() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Você precisa estar logado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar se está dentro do horário permitido
        int horaAtual = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
        if (horaAtual >= 18) {
            Toast.makeText(this, "Horário de reserva encerrado (até 18:00)", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
            .setTitle("Confirmar Reserva")
            .setMessage("Deseja reservar o prato de hoje? Você terá até às 18:00 para retirar.")
            .setPositiveButton("Confirmar", (dialog, which) -> {
                Reserva reserva = new Reserva();
                reserva.setUserId(user.getUid());
                reserva.setData(dataHoje);
                reserva.setNomeUsuario(user.getDisplayName() != null ? user.getDisplayName() : "Usuário");
                reserva.setEmailUsuario(user.getEmail());
                reserva.setObservacao(edtObservacao.getText() != null ? edtObservacao.getText().toString() : "");
                reserva.setStatusReserva("ATIVA");
                reserva.setSetor(setorUsuario != null ? setorUsuario : "Não informado");
                reserva.setEscolhaPrato(escolhaUsuario != null ? escolhaUsuario : "Não votou");
                
                reservaViewModel.insert(reserva, () -> runOnUiThread(() -> {
                    Toast.makeText(ReservaActivity.this, "Reserva realizada com sucesso!", Toast.LENGTH_SHORT).show();
                    edtObservacao.setText("");
                }));
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void cancelarReserva() {
        if (reservaAtual == null) return;

        new AlertDialog.Builder(this)
            .setTitle("Cancelar Reserva")
            .setMessage("Tem certeza que deseja cancelar sua reserva?")
            .setPositiveButton("Sim, cancelar", (dialog, which) -> {
                reservaViewModel.updateStatus(reservaAtual.getId(), "CANCELADA");
                Toast.makeText(this, "Reserva cancelada", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Não", null)
            .show();
    }
}
