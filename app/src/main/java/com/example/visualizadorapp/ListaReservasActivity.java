package com.example.visualizadorapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.visualizadorapp.model.Reserva;
import com.example.visualizadorapp.viewmodel.ReservaViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListaReservasActivity extends AppCompatActivity {

    private ReservaViewModel reservaViewModel;
    private LinearLayout containerReservas;
    private TextView txtTotalReservas, txtResumoEscolhas, txtDataReservas;
    private Button btnVoltar;
    private String dataHoje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reservas);

        dataHoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        reservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);

        inicializarComponentes();
        carregarReservas();
    }

    private void inicializarComponentes() {
        containerReservas = findViewById(R.id.containerReservas);
        txtTotalReservas = findViewById(R.id.txtTotalReservas);
        txtResumoEscolhas = findViewById(R.id.txtResumoEscolhas);
        txtDataReservas = findViewById(R.id.txtDataReservas);
        btnVoltar = findViewById(R.id.btnVoltarReservas);

        // Exibir data formatada
        SimpleDateFormat sdfExibir = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        txtDataReservas.setText("Reservas do dia: " + sdfExibir.format(new Date()));

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void carregarReservas() {
        reservaViewModel.getReservasByData(dataHoje).observe(this, reservas -> {
            containerReservas.removeAllViews();
            
            if (reservas == null || reservas.isEmpty()) {
                TextView txtVazio = new TextView(this);
                txtVazio.setText("📋 Nenhuma reserva para hoje");
                txtVazio.setTextSize(16);
                txtVazio.setPadding(20, 40, 20, 20);
                txtVazio.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                containerReservas.addView(txtVazio);
                txtTotalReservas.setText("0 reservas");
                txtResumoEscolhas.setText("");
                return;
            }

            // Contar escolhas
            Map<String, Integer> contagemEscolhas = new HashMap<>();
            int ativas = 0;
            
            for (Reserva reserva : reservas) {
                if ("ATIVA".equals(reserva.getStatusReserva())) {
                    ativas++;
                    String escolha = reserva.getEscolhaPrato() != null ? reserva.getEscolhaPrato() : "Não votou";
                    contagemEscolhas.put(escolha, contagemEscolhas.getOrDefault(escolha, 0) + 1);
                }
                
                adicionarItemReserva(reserva);
            }

            // Atualizar resumo
            txtTotalReservas.setText(ativas + " reservas ativas");
            
            // Montar resumo de escolhas
            if (!contagemEscolhas.isEmpty()) {
                StringBuilder resumo = new StringBuilder("📊 Resumo das escolhas:\n");
                for (Map.Entry<String, Integer> entry : contagemEscolhas.entrySet()) {
                    resumo.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                txtResumoEscolhas.setText(resumo.toString());
            } else {
                txtResumoEscolhas.setText("");
            }
        });
    }

    private void adicionarItemReserva(Reserva reserva) {
        LinearLayout itemView = (LinearLayout) getLayoutInflater()
            .inflate(R.layout.item_reserva, containerReservas, false);

        TextView txtNome = itemView.findViewById(R.id.txtNomeReserva);
        TextView txtEscolha = itemView.findViewById(R.id.txtEscolhaReserva);
        TextView txtSetor = itemView.findViewById(R.id.txtSetorReserva);
        TextView txtStatus = itemView.findViewById(R.id.txtStatusReserva);
        TextView txtObservacao = itemView.findViewById(R.id.txtObservacaoReserva);
        Button btnMarcarUtilizada = itemView.findViewById(R.id.btnMarcarUtilizada);
        Button btnCancelar = itemView.findViewById(R.id.btnCancelarReserva);

        txtNome.setText("👤 " + (reserva.getNomeUsuario() != null ? reserva.getNomeUsuario() : "Usuário"));
        
        String escolha = reserva.getEscolhaPrato() != null ? reserva.getEscolhaPrato() : "Não votou";
        txtEscolha.setText("🍽️ Escolha: " + escolha);
        
        String setor = reserva.getSetor() != null ? reserva.getSetor() : "Não informado";
        txtSetor.setText("🏢 Setor: " + setor);
        
        String status = reserva.getStatusReserva();
        txtStatus.setText("📌 Status: " + status);
        
        // Aplicar cor ao status
        if ("ATIVA".equals(status)) {
            txtStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnMarcarUtilizada.setVisibility(Button.VISIBLE);
            btnCancelar.setVisibility(Button.VISIBLE);
        } else if ("CANCELADA".equals(status)) {
            txtStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnMarcarUtilizada.setVisibility(Button.GONE);
            btnCancelar.setVisibility(Button.GONE);
            itemView.setAlpha(0.5f);
        } else if ("UTILIZADA".equals(status)) {
            txtStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            btnMarcarUtilizada.setVisibility(Button.GONE);
            btnCancelar.setVisibility(Button.GONE);
            itemView.setAlpha(0.7f);
        }

        String obs = reserva.getObservacao();
        if (obs != null && !obs.trim().isEmpty()) {
            txtObservacao.setText("💬 Observação: " + obs);
            txtObservacao.setVisibility(TextView.VISIBLE);
        } else {
            txtObservacao.setVisibility(TextView.GONE);
        }

        // Botões de ação
        btnMarcarUtilizada.setOnClickListener(v -> {
            reservaViewModel.updateStatus(reserva.getId(), "UTILIZADA");
            Toast.makeText(this, "Reserva marcada como utilizada", Toast.LENGTH_SHORT).show();
        });

        btnCancelar.setOnClickListener(v -> {
            reservaViewModel.updateStatus(reserva.getId(), "CANCELADA");
            Toast.makeText(this, "Reserva cancelada", Toast.LENGTH_SHORT).show();
        });

        containerReservas.addView(itemView);
    }
}
