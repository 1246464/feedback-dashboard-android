package com.example.visualizadorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.adapter.IngredienteAdapter;
import com.example.visualizadorapp.model.Ingrediente;
import com.example.visualizadorapp.viewmodel.IngredienteViewModel;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GestaoIngredientesActivity extends AppCompatActivity {
    
    private IngredienteViewModel viewModel;
    private RecyclerView recyclerViewIngredientes;
    private IngredienteAdapter adapter;
    private TextView textDataSelecionada, textStatusGeral;
    private ChipGroup chipGroupFiltros;
    private FloatingActionButton fabNovoIngrediente;
    private Button btnAnterior, btnProximo, btnVoltar;
    
    private Calendar calendarioAtual;
    private String filtroAtual = "TODOS";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_ingredientes);
        
        inicializarComponentes();
        configurarViewModel();
        configurarRecyclerView();
        configurarListeners();
        
        calendarioAtual = Calendar.getInstance();
        atualizarData();
    }
    
    private void inicializarComponentes() {
        recyclerViewIngredientes = findViewById(R.id.recyclerViewIngredientes);
        textDataSelecionada = findViewById(R.id.textDataSelecionadaIng);
        textStatusGeral = findViewById(R.id.textStatusGeralIng);
        chipGroupFiltros = findViewById(R.id.chipGroupFiltrosIng);
        fabNovoIngrediente = findViewById(R.id.fabNovoIngrediente);
        btnAnterior = findViewById(R.id.btnDiaAnteriorIng);
        btnProximo = findViewById(R.id.btnProximoDiaIng);
        btnVoltar = findViewById(R.id.btnVoltarIng);
        
        configurarFiltros();
    }
    
    private void configurarFiltros() {
        chipGroupFiltros.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipTodosIng) {
                filtroAtual = "TODOS";
            } else if (checkedId == R.id.chipDisponiveisIng) {
                filtroAtual = "DISPONIVEL";
            } else if (checkedId == R.id.chipFaltandoIng) {
                filtroAtual = "FALTANDO";
            } else if (checkedId == R.id.chipParciaisIng) {
                filtroAtual = "PARCIAL";
            }
            carregarIngredientes();
        });
    }
    
    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this).get(IngredienteViewModel.class);
    }
    
    private void configurarRecyclerView() {
        adapter = new IngredienteAdapter(
            this::mostrarDialogEditarIngrediente,
            new IngredienteAdapter.OnStatusChangeListener() {
                @Override
                public void onMarcarDisponivel(Ingrediente ingrediente) {
                    marcarComoDisponivel(ingrediente);
                }
                
                @Override
                public void onMarcarFaltando(Ingrediente ingrediente) {
                    marcarComoFaltando(ingrediente);
                }
                
                @Override
                public void onMarcarParcial(Ingrediente ingrediente) {
                    marcarComoParcial(ingrediente);
                }
            }
        );
        
        recyclerViewIngredientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewIngredientes.setAdapter(adapter);
    }
    
    private void configurarListeners() {
        fabNovoIngrediente.setOnClickListener(v -> mostrarDialogNovoIngrediente());
        btnAnterior.setOnClickListener(v -> navegarDia(-1));
        btnProximo.setOnClickListener(v -> navegarDia(1));
        btnVoltar.setOnClickListener(v -> finish());
    }
    
    private void carregarIngredientes() {
        String dataAtual = getDataAtual();
        
        if (filtroAtual.equals("TODOS")) {
            viewModel.getIngredientesPorData(dataAtual).observe(this, ingredientes -> {
                adapter.setIngredientes(ingredientes);
                atualizarStatusGeral(ingredientes.size());
            });
        } else {
            viewModel.getIngredientesPorDataEStatus(dataAtual, filtroAtual).observe(this, ingredientes -> {
                adapter.setIngredientes(ingredientes);
                atualizarStatusGeral(ingredientes.size());
            });
        }
    }
    
    private void atualizarData() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("pt", "BR"));
        textDataSelecionada.setText(sdf.format(calendarioAtual.getTime()));
        carregarIngredientes();
    }
    
    private void navegarDia(int dias) {
        calendarioAtual.add(Calendar.DAY_OF_MONTH, dias);
        atualizarData();
    }
    
    private String getDataAtual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendarioAtual.getTime());
    }
    
    private void atualizarStatusGeral(int totalIngredientes) {
        viewModel.getCountIngredientesFaltando(getDataAtual()).observe(this, count -> {
            if (count != null && count > 0) {
                textStatusGeral.setText("⚠ " + count + " ingrediente(s) faltando de " + totalIngredientes);
                textStatusGeral.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                textStatusGeral.setText("✓ Todos os ingredientes disponíveis");
                textStatusGeral.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        });
    }
    
    private void mostrarDialogNovoIngrediente() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ingrediente, null);
        
        EditText edtNome = dialogView.findViewById(R.id.edtNomeIngrediente);
        EditText edtQuantidade = dialogView.findViewById(R.id.edtQuantidadeIngrediente);
        EditText edtObservacao = dialogView.findViewById(R.id.edtObservacaoIngrediente);
        
        new AlertDialog.Builder(this)
            .setTitle("Novo Ingrediente")
            .setView(dialogView)
            .setPositiveButton("Adicionar", (dialog, which) -> {
                String nome = edtNome.getText().toString().trim();
                String quantidade = edtQuantidade.getText().toString().trim();
                String observacao = edtObservacao.getText().toString().trim();
                
                if (nome.isEmpty()) {
                    Toast.makeText(this, "Digite o nome do ingrediente", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Ingrediente ingrediente = new Ingrediente();
                ingrediente.setDataCardapio(getDataAtual());
                ingrediente.setNomeIngrediente(nome);
                ingrediente.setQuantidade(quantidade.isEmpty() ? "N/A" : quantidade);
                ingrediente.setObservacao(observacao);
                ingrediente.setStatus("DISPONIVEL");
                ingrediente.setResponsavelVerificacao(getUsuarioAtual());
                ingrediente.setTimestampVerificacao(System.currentTimeMillis());
                
                viewModel.inserirIngrediente(ingrediente);
                Toast.makeText(this, "✓ Ingrediente adicionado", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
    
    private void mostrarDialogEditarIngrediente(Ingrediente ingrediente) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ingrediente, null);
        
        EditText edtNome = dialogView.findViewById(R.id.edtNomeIngrediente);
        EditText edtQuantidade = dialogView.findViewById(R.id.edtQuantidadeIngrediente);
        EditText edtObservacao = dialogView.findViewById(R.id.edtObservacaoIngrediente);
        
        edtNome.setText(ingrediente.getNomeIngrediente());
        edtQuantidade.setText(ingrediente.getQuantidade());
        edtObservacao.setText(ingrediente.getObservacao());
        
        new AlertDialog.Builder(this)
            .setTitle("Editar Ingrediente")
            .setView(dialogView)
            .setPositiveButton("Salvar", (dialog, which) -> {
                ingrediente.setNomeIngrediente(edtNome.getText().toString().trim());
                ingrediente.setQuantidade(edtQuantidade.getText().toString().trim());
                ingrediente.setObservacao(edtObservacao.getText().toString().trim());
                ingrediente.setResponsavelVerificacao(getUsuarioAtual());
                ingrediente.setTimestampVerificacao(System.currentTimeMillis());
                
                viewModel.atualizarIngrediente(ingrediente);
                Toast.makeText(this, "✓ Ingrediente atualizado", Toast.LENGTH_SHORT).show();
            })
            .setNeutralButton("Excluir", (dialog, which) -> {
                new AlertDialog.Builder(this)
                    .setTitle("Confirmar exclusão")
                    .setMessage("Deseja realmente excluir este ingrediente?")
                    .setPositiveButton("Sim", (d, w) -> {
                        viewModel.deletarIngrediente(ingrediente);
                        Toast.makeText(this, "✓ Ingrediente removido", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", null)
                    .show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
    
    private void marcarComoDisponivel(Ingrediente ingrediente) {
        ingrediente.setResponsavelVerificacao(getUsuarioAtual());
        viewModel.marcarComoDisponivel(ingrediente);
        Toast.makeText(this, "✓ Marcado como disponível", Toast.LENGTH_SHORT).show();
    }
    
    private void marcarComoFaltando(Ingrediente ingrediente) {
        ingrediente.setResponsavelVerificacao(getUsuarioAtual());
        viewModel.marcarComoFaltando(ingrediente);
        Toast.makeText(this, "⚠ Marcado como faltando", Toast.LENGTH_SHORT).show();
    }
    
    private void marcarComoParcial(Ingrediente ingrediente) {
        ingrediente.setResponsavelVerificacao(getUsuarioAtual());
        viewModel.marcarComoParcial(ingrediente);
        Toast.makeText(this, "⚠ Marcado como parcial", Toast.LENGTH_SHORT).show();
    }
    
    private String getUsuarioAtual() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            return email != null ? email.split("@")[0] : "Admin";
        }
        return "Admin";
    }
}
