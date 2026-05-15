package com.example.visualizadorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.adapter.PassagemTurnoAdapter;
import com.example.visualizadorapp.model.PassagemTurno;
import com.example.visualizadorapp.util.NotificationHelper;
import com.example.visualizadorapp.viewmodel.PassagemTurnoViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PassagemTurnoActivity extends AppCompatActivity {
    
    private PassagemTurnoViewModel viewModel;
    private RecyclerView recyclerViewPassagens;
    private PassagemTurnoAdapter adapter;
    private TextView textTurnoAtual, textMensagensNaoLidas;
    private TabLayout tabLayoutFiltros;
    private FloatingActionButton fabNovaPassagem;
    
    private String turnoAtual;
    private String dataAtual;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passagem_turno);
        
        turnoAtual = detectarTurnoAtual();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dataAtual = sdf.format(Calendar.getInstance().getTime());
        
        inicializarComponentes();
        configurarViewModel();
        configurarRecyclerView();
        configurarListeners();
        
        carregarPassagens();
    }
    
    private void inicializarComponentes() {
        recyclerViewPassagens = findViewById(R.id.recyclerViewPassagens);
        textTurnoAtual = findViewById(R.id.textTurnoAtual);
        textMensagensNaoLidas = findViewById(R.id.textMensagensNaoLidas);
        tabLayoutFiltros = findViewById(R.id.tabLayoutFiltros);
        fabNovaPassagem = findViewById(R.id.fabNovaPassagem);
        
        textTurnoAtual.setText("Turno Atual: " + turnoAtual);
        
        // Configurar Tabs
        tabLayoutFiltros.addTab(tabLayoutFiltros.newTab().setText("Não Lidas"));
        tabLayoutFiltros.addTab(tabLayoutFiltros.newTab().setText("Todas"));
        tabLayoutFiltros.addTab(tabLayoutFiltros.newTab().setText("Urgentes"));
    }
    
    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this).get(PassagemTurnoViewModel.class);
        
        // Observar contagem de não lidas
        viewModel.getCountPassagensNaoLidas(turnoAtual).observe(this, count -> {
            if (count != null && count > 0) {
                textMensagensNaoLidas.setText(count + " mensagens não lidas");
                textMensagensNaoLidas.setVisibility(View.VISIBLE);
            } else {
                textMensagensNaoLidas.setVisibility(View.GONE);
            }
        });
    }
    
    private void configurarRecyclerView() {
        adapter = new PassagemTurnoAdapter(
            passagem -> marcarComoLida(passagem),
            passagem -> abrirDetalhesPassagem(passagem)
        );
        
        recyclerViewPassagens.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPassagens.setAdapter(adapter);
    }
    
    private void configurarListeners() {
        tabLayoutFiltros.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                carregarPassagens();
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        fabNovaPassagem.setOnClickListener(v -> abrirDialogNovaPassagem());
    }
    
    private void carregarPassagens() {
        int posicaoTab = tabLayoutFiltros.getSelectedTabPosition();
        
        switch (posicaoTab) {
            case 0: // Não Lidas
                viewModel.getPassagensNaoLidasPorTurno(turnoAtual).observe(this, passagens -> {
                    adapter.setPassagens(passagens);
                });
                break;
                
            case 1: // Todas
                viewModel.getPassagensPorData(dataAtual).observe(this, passagens -> {
                    adapter.setPassagens(passagens);
                });
                break;
                
            case 2: // Urgentes
                viewModel.getPassagensUrgentes().observe(this, passagens -> {
                    adapter.setPassagens(passagens);
                });
                break;
        }
    }
    
    private void abrirDialogNovaPassagem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_nova_passagem, null);
        
        EditText editMensagem = dialogView.findViewById(R.id.editMensagemPassagem);
        EditText editTarefasConcluidas = dialogView.findViewById(R.id.editTarefasConcluidas);
        EditText editTarefasPendentes = dialogView.findViewById(R.id.editTarefasPendentes);
        EditText editProblemas = dialogView.findViewById(R.id.editProblemas);
        EditText editMudancas = dialogView.findViewById(R.id.editMudancas);
        Spinner spinnerTurnoDestino = dialogView.findViewById(R.id.spinnerTurnoDestino);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipoMensagem);
        
        // Configurar spinner de turno destino
        String[] turnos = getProximosTurnos();
        ArrayAdapter<String> turnoAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, turnos);
        turnoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurnoDestino.setAdapter(turnoAdapter);
        
        // Configurar spinner de tipo
        String[] tipos = {"INFORMACAO", "ALERTA", "URGENTE"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);
        
        builder.setView(dialogView)
            .setTitle("Nova Passagem de Turno")
            .setPositiveButton("Enviar", (dialog, which) -> {
                String mensagem = editMensagem.getText().toString().trim();
                if (mensagem.isEmpty()) {
                    Toast.makeText(this, "Mensagem é obrigatória", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                PassagemTurno novaPassagem = new PassagemTurno();
                novaPassagem.setData(dataAtual);
                novaPassagem.setTurnoOrigem(turnoAtual);
                novaPassagem.setTurnoDestino((String) spinnerTurnoDestino.getSelectedItem());
                novaPassagem.setMensagem(mensagem);
                novaPassagem.setTipoMensagem((String) spinnerTipo.getSelectedItem());
                novaPassagem.setTarefasConcluidas(editTarefasConcluidas.getText().toString().trim());
                novaPassagem.setTarefasPendentes(editTarefasPendentes.getText().toString().trim());
                novaPassagem.setProblemas(editProblemas.getText().toString().trim());
                novaPassagem.setMudancas(editMudancas.getText().toString().trim());
                
                // Usuário atual
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    novaPassagem.setUsuarioOrigem(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                }
                
                viewModel.inserirPassagem(novaPassagem);
                Toast.makeText(this, "Passagem enviada com sucesso!", Toast.LENGTH_SHORT).show();
                android.util.Log.d("PassagemTurno", "Enviando notificação para turno: " + novaPassagem.getTurnoDestino());
                NotificationHelper.notificarPassagemTurno(this, 
                    novaPassagem.getTurnoDestino(), 
                    novaPassagem.getMensagem());
            })
            .setNegativeButton("Cancelar", null)
            .create()
            .show();
    }
    
    private void abrirDetalhesPassagem(PassagemTurno passagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_detalhes_passagem, null);
        
        TextView textUsuario = dialogView.findViewById(R.id.textDetalhesUsuario);
        TextView textTurnos = dialogView.findViewById(R.id.textDetalhesTurnos);
        TextView textMensagem = dialogView.findViewById(R.id.textDetalhesMensagem);
        TextView textTarefasConcluidas = dialogView.findViewById(R.id.textDetalhesTarefasConcluidas);
        TextView textTarefasPendentes = dialogView.findViewById(R.id.textDetalhesTarefasPendentes);
        TextView textProblemas = dialogView.findViewById(R.id.textDetalhesProblemas);
        TextView textMudancas = dialogView.findViewById(R.id.textDetalhesMudancas);
        TextView textHora = dialogView.findViewById(R.id.textDetalhesHora);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        textUsuario.setText("De: " + (passagem.getUsuarioOrigem() != null ? passagem.getUsuarioOrigem() : "Sistema"));
        textTurnos.setText("Turno " + passagem.getTurnoOrigem() + " → " + passagem.getTurnoDestino());
        textMensagem.setText(passagem.getMensagem());
        textHora.setText(sdf.format(passagem.getTimestamp()));
        
        // Campos opcionais
        configurarCampoOpcional(textTarefasConcluidas, passagem.getTarefasConcluidas(), "✅ Tarefas Concluídas:\n");
        configurarCampoOpcional(textTarefasPendentes, passagem.getTarefasPendentes(), "⏳ Tarefas Pendentes:\n");
        configurarCampoOpcional(textProblemas, passagem.getProblemas(), "⚠️ Problemas:\n");
        configurarCampoOpcional(textMudancas, passagem.getMudancas(), "🔄 Mudanças:\n");
        
        builder.setView(dialogView)
            .setTitle(getTipoEmoji(passagem.getTipoMensagem()) + " Passagem de Turno")
            .setPositiveButton("OK", null)
            .create()
            .show();
    }
    
    private void configurarCampoOpcional(TextView textView, String valor, String prefixo) {
        if (valor != null && !valor.trim().isEmpty()) {
            textView.setText(prefixo + valor);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
    
    private void marcarComoLida(PassagemTurno passagem) {
        if (!passagem.isLida()) {
            viewModel.marcarComoLida(passagem.getId());
        }
    }
    
    private String[] getProximosTurnos() {
        switch (turnoAtual) {
            case "MANHA":
                return new String[]{"TARDE", "NOITE"};
            case "TARDE":
                return new String[]{"NOITE", "MANHA"};
            case "NOITE":
                return new String[]{"MANHA", "TARDE"};
            default:
                return new String[]{"MANHA", "TARDE", "NOITE"};
        }
    }
    
    private String detectarTurnoAtual() {
        int hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hora >= 6 && hora < 14) {
            return "MANHA";
        } else if (hora >= 14 && hora < 22) {
            return "TARDE";
        } else {
            return "NOITE";
        }
    }
    
    private String getTipoEmoji(String tipo) {
        switch (tipo) {
            case "INFORMACAO": return "ℹ️";
            case "ALERTA": return "⚠️";
            case "URGENTE": return "🚨";
            default: return "";
        }
    }
}
