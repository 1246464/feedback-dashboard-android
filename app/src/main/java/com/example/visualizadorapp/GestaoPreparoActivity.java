package com.example.visualizadorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.adapter.TarefaPreparoAdapter;
import com.example.visualizadorapp.model.TarefaPreparo;
import com.example.visualizadorapp.viewmodel.GestaoPreparoViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GestaoPreparoActivity extends AppCompatActivity {
    
    private GestaoPreparoViewModel viewModel;
    private RecyclerView recyclerViewTarefas;
    private TarefaPreparoAdapter adapter;
    private TextView textDataSelecionada, textStatusGeral;
    private ChipGroup chipGroupFiltros;
    private FloatingActionButton fabNovaTarefa;
    private Button btnAnterior, btnProximo;
    
    private Calendar calendarioAtual;
    private String filtroAtual = "TODAS";
    private String turnoAtual = "MANHA"; // valor default
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_preparo);
        
        // Receber turno do Intent se disponível
        turnoAtual = getIntent().getStringExtra("turno");
        if (turnoAtual == null || turnoAtual.isEmpty()) {
            turnoAtual = detectarTurnoAtual();
        }
        
        inicializarComponentes();
        configurarViewModel();
        configurarRecyclerView();
        configurarListeners();
        
        calendarioAtual = Calendar.getInstance();
        atualizarData();
    }
    
    private void inicializarComponentes() {
        recyclerViewTarefas = findViewById(R.id.recyclerViewTarefas);
        textDataSelecionada = findViewById(R.id.textDataSelecionada);
        textStatusGeral = findViewById(R.id.textStatusGeral);
        chipGroupFiltros = findViewById(R.id.chipGroupFiltros);
        fabNovaTarefa = findViewById(R.id.fabNovaTarefa);
        btnAnterior = findViewById(R.id.btnDiaAnterior);
        btnProximo = findViewById(R.id.btnProximoDia);
        
        // Configurar filtros
        configurarFiltros();
    }
    
    private void configurarFiltros() {
        chipGroupFiltros.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipTodas) {
                filtroAtual = "TODAS";
            } else if (checkedId == R.id.chipPendentes) {
                filtroAtual = "PENDENTE";
            } else if (checkedId == R.id.chipEmAndamento) {
                filtroAtual = "EM_ANDAMENTO";
            } else if (checkedId == R.id.chipConcluidas) {
                filtroAtual = "CONCLUIDA";
            } else if (checkedId == R.id.chipMeuTurno) {
                filtroAtual = "MEU_TURNO";
            }
            carregarTarefas();
        });
    }
    
    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this).get(GestaoPreparoViewModel.class);
    }
    
    private void configurarRecyclerView() {
        adapter = new TarefaPreparoAdapter(
            tarefa -> abrirDetalhesTarefa(tarefa),
            tarefa -> marcarComoEmAndamento(tarefa),
            tarefa -> marcarComoConcluida(tarefa)
        );
        
        recyclerViewTarefas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTarefas.setAdapter(adapter);
    }
    
    private void configurarListeners() {
        btnAnterior.setOnClickListener(v -> {
            calendarioAtual.add(Calendar.DAY_OF_MONTH, -1);
            atualizarData();
        });
        
        btnProximo.setOnClickListener(v -> {
            calendarioAtual.add(Calendar.DAY_OF_MONTH, 1);
            atualizarData();
        });
        
        fabNovaTarefa.setOnClickListener(v -> abrirDialogNovaTarefa());
    }
    
    private void atualizarData() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        textDataSelecionada.setText(sdf.format(calendarioAtual.getTime()));
        carregarTarefas();
    }
    
    private void carregarTarefas() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dataAtual = sdf.format(calendarioAtual.getTime());
        
        if (filtroAtual.equals("TODAS")) {
            viewModel.getTarefasPorData(dataAtual).observe(this, tarefas -> {
                adapter.setTarefas(tarefas);
                atualizarStatusGeral(tarefas.size());
            });
        } else if (filtroAtual.equals("MEU_TURNO")) {
            viewModel.getTarefasPorDataETurno(dataAtual, turnoAtual).observe(this, tarefas -> {
                adapter.setTarefas(tarefas);
                atualizarStatusGeral(tarefas.size());
            });
        } else {
            viewModel.getTarefasPorDataEStatus(dataAtual, filtroAtual).observe(this, tarefas -> {
                adapter.setTarefas(tarefas);
                atualizarStatusGeral(tarefas.size());
            });
        }
        
        // Atualizar contadores
        viewModel.getCountTarefasPendentes(dataAtual).observe(this, count -> {
            Chip chipPendentes = findViewById(R.id.chipPendentes);
            if (chipPendentes != null && count != null) {
                chipPendentes.setText("Pendentes (" + count + ")");
            }
        });
        
        viewModel.getCountTarefasConcluidas(dataAtual).observe(this, count -> {
            Chip chipConcluidas = findViewById(R.id.chipConcluidas);
            if (chipConcluidas != null && count != null) {
                chipConcluidas.setText("Concluídas (" + count + ")");
            }
        });
    }
    
    private void atualizarStatusGeral(int totalTarefas) {
        textStatusGeral.setText("Total de tarefas: " + totalTarefas);
    }
    
    private void abrirDialogNovaTarefa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_nova_tarefa, null);
        
        EditText editDescricao = dialogView.findViewById(R.id.editDescricaoTarefa);
        Spinner spinnerTurno = dialogView.findViewById(R.id.spinnerTurno);
        Spinner spinnerPrioridade = dialogView.findViewById(R.id.spinnerPrioridade);
        EditText editObservacao = dialogView.findViewById(R.id.editObservacao);
        
        // Configurar spinner de turno
        String[] turnos = {"MANHA", "TARDE", "NOITE"};
        ArrayAdapter<String> turnoAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, turnos);
        turnoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurno.setAdapter(turnoAdapter);
        
        // Configurar spinner de prioridade
        String[] prioridades = {"1 - Baixa", "2 - Normal", "3 - Média", "4 - Alta", "5 - Urgente"};
        ArrayAdapter<String> prioridadeAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, prioridades);
        prioridadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(prioridadeAdapter);
        spinnerPrioridade.setSelection(2); // Média por padrão
        
        builder.setView(dialogView)
            .setTitle("Nova Tarefa de Pré-Preparo")
            .setPositiveButton("Criar", (dialog, which) -> {
                String descricao = editDescricao.getText().toString().trim();
                if (descricao.isEmpty()) {
                    Toast.makeText(this, "Descrição é obrigatória", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                TarefaPreparo novaTarefa = new TarefaPreparo();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                novaTarefa.setDataCardapio(sdf.format(calendarioAtual.getTime()));
                novaTarefa.setDescricaoTarefa(descricao);
                novaTarefa.setTurnoResponsavel((String) spinnerTurno.getSelectedItem());
                novaTarefa.setPrioridade(spinnerPrioridade.getSelectedItemPosition() + 1);
                novaTarefa.setObservacao(editObservacao.getText().toString().trim());
                novaTarefa.setStatus("PENDENTE");
                
                viewModel.inserirTarefa(novaTarefa);
                Toast.makeText(this, "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null)
            .create()
            .show();
    }
    
    private void abrirDetalhesTarefa(TarefaPreparo tarefa) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_detalhes_tarefa, null);
        
        TextView textDescricao = dialogView.findViewById(R.id.textDetalhesDescricao);
        TextView textTurno = dialogView.findViewById(R.id.textDetalhesTurno);
        TextView textStatus = dialogView.findViewById(R.id.textDetalhesStatus);
        TextView textPrioridade = dialogView.findViewById(R.id.textDetalhesPrioridade);
        TextView textResponsavel = dialogView.findViewById(R.id.textDetalhesResponsavel);
        TextView textObservacao = dialogView.findViewById(R.id.textDetalhesObservacao);
        
        textDescricao.setText(tarefa.getDescricaoTarefa());
        textTurno.setText("Turno: " + tarefa.getTurnoResponsavel());
        textStatus.setText("Status: " + tarefa.getStatus());
        textPrioridade.setText("Prioridade: " + tarefa.getPrioridade());
        textResponsavel.setText("Responsável: " + 
            (tarefa.getResponsavel() != null ? tarefa.getResponsavel() : "Não atribuído"));
        textObservacao.setText(tarefa.getObservacao() != null ? tarefa.getObservacao() : "Sem observações");
        
        builder.setView(dialogView)
            .setTitle("Detalhes da Tarefa")
            .setPositiveButton("OK", null)
            .create()
            .show();
    }
    
    private void marcarComoEmAndamento(TarefaPreparo tarefa) {
        String nomeUsuario = FirebaseAuth.getInstance().getCurrentUser() != null ?
            FirebaseAuth.getInstance().getCurrentUser().getDisplayName() : "Usuário";
        
        viewModel.iniciarTarefa(tarefa, nomeUsuario);
        Toast.makeText(this, "Tarefa iniciada!", Toast.LENGTH_SHORT).show();
    }
    
    private void marcarComoConcluida(TarefaPreparo tarefa) {
        viewModel.marcarTarefaComoConcluida(tarefa);
        Toast.makeText(this, "Tarefa concluída! ✅", Toast.LENGTH_SHORT).show();
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
}
