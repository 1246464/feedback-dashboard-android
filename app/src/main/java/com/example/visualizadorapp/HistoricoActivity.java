package com.example.visualizadorapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.visualizadorapp.adapter.CardapioAdapter;
import com.example.visualizadorapp.model.Cardapio;
import com.example.visualizadorapp.viewmodel.CardapioViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HistoricoActivity extends AppCompatActivity implements CardapioAdapter.OnCardapioClickListener {
    private RecyclerView recyclerView;
    private CardapioAdapter adapter;
    private CardapioViewModel viewModel;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout layoutEmpty;
    private Chip chipTodos, chipFavoritos, chipRecentes;
    private ImageButton btnBuscar;
    private TextInputLayout searchLayout;
    private TextInputEditText edtBusca;
    private MaterialButton btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(CardapioViewModel.class);

        // Inicializar componentes
        recyclerView = findViewById(R.id.recyclerViewHistorico);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        chipTodos = findViewById(R.id.chipTodos);
        chipFavoritos = findViewById(R.id.chipFavoritos);
        chipRecentes = findViewById(R.id.chipRecentes);
        btnBuscar = findViewById(R.id.btnBuscar);
        searchLayout = findViewById(R.id.searchLayout);
        edtBusca = findViewById(R.id.edtBusca);
        btnVoltar = findViewById(R.id.btnVoltar);

        // Setup RecyclerView
        adapter = new CardapioAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Carregar todos os cardápios por padrão
        loadAllCardapios();

        // Listeners dos filtros
        chipTodos.setOnClickListener(v -> loadAllCardapios());
        chipFavoritos.setOnClickListener(v -> loadFavoritos());
        chipRecentes.setOnClickListener(v -> loadRecentes());

        // Botão de busca
        btnBuscar.setOnClickListener(v -> {
            if (searchLayout.getVisibility() == View.GONE) {
                searchLayout.setVisibility(View.VISIBLE);
                edtBusca.requestFocus();
            } else {
                searchLayout.setVisibility(View.GONE);
                edtBusca.setText("");
            }
        });

        // Busca em tempo real
        edtBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchCardapios(s.toString());
                } else {
                    loadAllCardapios();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Swipe to refresh
        swipeRefresh.setOnRefreshListener(() -> {
            if (chipFavoritos.isChecked()) {
                loadFavoritos();
            } else if (chipRecentes.isChecked()) {
                loadRecentes();
            } else {
                loadAllCardapios();
            }
            swipeRefresh.setRefreshing(false);
        });

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void loadAllCardapios() {
        viewModel.getAllCardapios().observe(this, cardapios -> {
            if (cardapios != null && !cardapios.isEmpty()) {
                adapter.setCardapios(cardapios);
                recyclerView.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadFavoritos() {
        viewModel.getFavoritos().observe(this, cardapios -> {
            if (cardapios != null && !cardapios.isEmpty()) {
                adapter.setCardapios(cardapios);
                recyclerView.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadRecentes() {
        // Últimos 30 dias
        Calendar calendar = Calendar.getInstance();
        String dataFim = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        String dataInicio = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        viewModel.getCardapiosByPeriodo(dataInicio, dataFim).observe(this, cardapios -> {
            if (cardapios != null && !cardapios.isEmpty()) {
                adapter.setCardapios(cardapios);
                recyclerView.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void searchCardapios(String query) {
        viewModel.searchCardapios(query).observe(this, cardapios -> {
            if (cardapios != null && !cardapios.isEmpty()) {
                adapter.setCardapios(cardapios);
                recyclerView.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCardapioClick(Cardapio cardapio) {
        // Abrir detalhes do cardápio (pode ser implementado depois)
        Toast.makeText(this, "Cardápio de " + cardapio.getData(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoritoClick(Cardapio cardapio) {
        boolean novoStatus = !cardapio.isFavorito();
        viewModel.updateFavorito(cardapio.getData(), novoStatus);
        Toast.makeText(this, 
            novoStatus ? "Adicionado aos favoritos" : "Removido dos favoritos", 
            Toast.LENGTH_SHORT).show();
    }
}
