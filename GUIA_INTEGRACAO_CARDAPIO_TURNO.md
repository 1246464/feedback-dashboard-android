# 📖 Guia de Integração: Cardápio Filtrado por Turno

## 🎯 Objetivo

Este guia mostra como integrar a visualização de cardápios por turno do usuário nas atividades existentes.

---

## 1. Integração no CardapioSemanalActivity

### 📍 Localização
`app/src/main/java/com/example/visualizadorapp/CardapioSemanalActivity.java`

### Código de Exemplo

```java
public class CardapioSemanalActivity extends AppCompatActivity {

    private CardapioTurnoRepository cardapioTurnoRepository;
    private Usuario usuarioLogado;
    private RecyclerView recyclerViewCardapios;
    private CardapioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio_semanal);

        // Inicializar repositório
        cardapioTurnoRepository = new CardapioTurnoRepository(getApplication());

        // Obter usuário logado (exemplo)
        usuarioLogado = obterUsuarioLogado();

        // Sincronizar cardápios do Firebase
        cardapioTurnoRepository.sincronizarComFirebase();

        // Configurar RecyclerView
        recyclerViewCardapios = findViewById(R.id.recyclerViewCardapios);
        recyclerViewCardapios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardapioAdapter();
        recyclerViewCardapios.setAdapter(adapter);

        // Carregar cardápios do turno do usuário
        carregarCardapioDoTurno();
    }

    /**
     * Carrega cardápios apenas do turno do usuário logado
     */
    private void carregarCardapioDoTurno() {
        if (usuarioLogado == null || usuarioLogado.getTurno() == null) {
            Toast.makeText(this, "Erro ao obter turno do usuário", Toast.LENGTH_SHORT).show();
            return;
        }

        String turnoUsuario = usuarioLogado.getTurno();
        
        // Observar cardápios do turno
        cardapioTurnoRepository.buscarPorTurnoAsync(turnoUsuario)
                .observe(this, cardapios -> {
                    if (cardapios != null && !cardapios.isEmpty()) {
                        adapter.submitList(cardapios);
                    } else {
                        Toast.makeText(
                                CardapioSemanalActivity.this,
                                "Nenhum cardápio disponível para seu turno",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

        // Observar mensagens de erro
        cardapioTurnoRepository.getErrorMessage()
                .observe(this, errorMsg -> {
                    if (errorMsg != null) {
                        Toast.makeText(CardapioSemanalActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Carrega cardápios de um período específico para o turno do usuário
     * Exemplo: próximas 2 semanas
     */
    private void carregarCardapiosPeriodo() {
        String dataInicio = obterDataAtual();
        String dataFim = obterDataDaqui(14); // 14 dias à frente

        cardapioTurnoRepository.buscarPorPeriodoETurnoAsync(
                dataInicio,
                dataFim,
                usuarioLogado.getTurno()
        ).observe(this, cardapios -> {
            adapter.submitList(cardapios);
        });
    }

    private Usuario obterUsuarioLogado() {
        // TODO: Implementar obtenção do usuário logado
        // Pode vir de SharedPreferences, ViewModel, ou Firebase Auth
        return null;
    }

    private String obterDataAtual() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(new Date());
    }

    private String obterDataDaqui(int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardapioTurnoRepository != null) {
            cardapioTurnoRepository.shutdown();
        }
    }
}
```

---

## 2. Criar um ViewModel para CardapioTurno

### 📍 Criar novo arquivo
`app/src/main/java/com/example/visualizadorapp/viewmodel/CardapioTurnoViewModel.java`

```java
public class CardapioTurnoViewModel extends AndroidViewModel {

    private CardapioTurnoRepository repository;
    private LiveData<List<CardapioTurno>> cardapios;
    private LiveData<String> errorMessage;

    public CardapioTurnoViewModel(@NonNull Application application) {
        super(application);
        repository = new CardapioTurnoRepository(application);
        repository.sincronizarComFirebase();
        errorMessage = repository.getErrorMessage();
    }

    /**
     * Carrega cardápios do turno especificado
     */
    public LiveData<List<CardapioTurno>> carregarCardapiosPorTurno(String turno) {
        cardapios = repository.buscarPorTurnoAsync(turno);
        return cardapios;
    }

    /**
     * Carrega cardápios de um período para um turno
     */
    public LiveData<List<CardapioTurno>> carregarCardapiosPeriodo(
            String dataInicio,
            String dataFim,
            String turno
    ) {
        cardapios = repository.buscarPorPeriodoETurnoAsync(dataInicio, dataFim, turno);
        return cardapios;
    }

    /**
     * Carrega próximos cardápios a partir de uma data
     */
    public LiveData<List<CardapioTurno>> carregarProximos(String data) {
        cardapios = repository.buscarProximosAsync(data);
        return cardapios;
    }

    /**
     * Carrega cardápios favoritos do turno
     */
    public LiveData<List<CardapioTurno>> carregarFavoritosPorTurno(String turno) {
        cardapios = repository.buscarFavoritosPorTurnoAsync(turno);
        return cardapios;
    }

    /**
     * Atualiza favorito
     */
    public void atualizarFavorito(String id, boolean isFavorito) {
        repository.atualizarFavorito(id, isFavorito);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.shutdown();
    }
}
```

---

## 3. Usando ViewModel em Activity

```java
public class CardapioSemanalActivity extends AppCompatActivity {

    private CardapioTurnoViewModel viewModel;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio_semanal);

        // Criar ViewModel
        viewModel = new ViewModelProvider(this).get(CardapioTurnoViewModel.class);

        // Obter usuário logado
        usuarioLogado = obterUsuarioLogado();

        // Observar cardápios do turno
        if (usuarioLogado != null && usuarioLogado.getTurno() != null) {
            viewModel.carregarCardapiosPorTurno(usuarioLogado.getTurno())
                    .observe(this, cardapios -> {
                        // Atualizar UI com cardápios
                        mostrarCardapios(cardapios);
                    });
        }

        // Observar erros
        viewModel.getErrorMessage().observe(this, erro -> {
            if (erro != null) {
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarCardapios(List<CardapioTurno> cardapios) {
        // TODO: Implementar exibição dos cardápios
    }
}
```

---

## 4. Adapter para Exibir CardapioTurno

### 📍 Criar novo arquivo
`app/src/main/java/com/example/visualizadorapp/adapter/CardapioTurnoAdapter.java`

```java
public class CardapioTurnoAdapter extends 
        RecyclerView.Adapter<CardapioTurnoAdapter.ViewHolder> {

    private List<CardapioTurno> cardapios;
    private OnCardapioClickListener listener;

    public CardapioTurnoAdapter(OnCardapioClickListener listener) {
        this.cardapios = new ArrayList<>();
        this.listener = listener;
    }

    public void submitList(List<CardapioTurno> newList) {
        this.cardapios = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardapio_turno, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardapioTurno cardapio = cardapios.get(position);
        
        holder.txtData.setText("Data: " + cardapio.getData());
        holder.txtTurno.setText("Turno: " + cardapio.getTurnoFormatado());
        holder.txtPrato.setText("Prato: " + cardapio.getPratoPrincipal());
        holder.txtSalada.setText("Salada: " + cardapio.getSalada());
        holder.txtSobremesa.setText("Sobremesa: " + cardapio.getSobremesa());

        // Mostrar informações nutricionais se disponíveis
        if (cardapio.getCalorias() != null) {
            holder.txtCalorias.setText("Calorias: " + cardapio.getCalorias());
            holder.txtCalorias.setVisibility(View.VISIBLE);
        }

        // Configurar botão de favorito
        holder.btnFavorito.setSelected(cardapio.isFavorito());
        holder.btnFavorito.setOnClickListener(v -> {
            listener.onFavoritoClick(cardapio.getId(), !cardapio.isFavorito());
        });

        holder.itemView.setOnClickListener(v -> listener.onCardapioClick(cardapio));
    }

    @Override
    public int getItemCount() {
        return cardapios.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtData, txtTurno, txtPrato, txtSalada, txtSobremesa, txtCalorias;
        ImageButton btnFavorito;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtData = itemView.findViewById(R.id.txtData);
            txtTurno = itemView.findViewById(R.id.txtTurno);
            txtPrato = itemView.findViewById(R.id.txtPrato);
            txtSalada = itemView.findViewById(R.id.txtSalada);
            txtSobremesa = itemView.findViewById(R.id.txtSobremesa);
            txtCalorias = itemView.findViewById(R.id.txtCalorias);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
        }
    }

    interface OnCardapioClickListener {
        void onCardapioClick(CardapioTurno cardapio);
        void onFavoritoClick(String cardapioId, boolean isFavorito);
    }
}
```

---

## 5. Layout Item do Cardápio

### 📍 Criar novo arquivo
`app/src/main/res/layout/item_cardapio_turno.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp"
    android:background="@drawable/item_cardapio_background"
    android:layout_marginVertical="8dp"
    android:layout_marginHorizontal="8dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:layout_marginBottom="8dp">

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:id="@+id/txtData"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textSize="14sp"
                android:textStyle="bold"
                android:textColor="@android:color/black" />

            <TextView
                android:id="@+id/txtTurno"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textSize="12sp"
                android:textColor="@color/teal_700"
                android:layout_marginTop="4dp" />

        </LinearLayout>

        <ImageButton
            android:id="@+id/btnFavorito"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:contentDescription="Adicionar favorito"
            android:src="@android:drawable/ic_menu_add"
            android:scaleType="centerInside"
            android:background="?attr/selectableItemBackgroundBorderless" />

    </LinearLayout>

    <TextView
        android:id="@+id/txtPrato"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="13sp"
        android:layout_marginTop="8dp"
        android:layout_marginBottom="4dp"
        android:textColor="@android:color/black" />

    <TextView
        android:id="@+id/txtSalada"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="13sp"
        android:layout_marginBottom="4dp"
        android:textColor="@android:color/darker_gray" />

    <TextView
        android:id="@+id/txtSobremesa"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="13sp"
        android:layout_marginBottom="8dp"
        android:textColor="@android:color/darker_gray" />

    <TextView
        android:id="@+id/txtCalorias"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="12sp"
        android:textStyle="italic"
        android:textColor="@color/info"
        android:visibility="gone" />

</LinearLayout>
```

---

## 6. Migração de Dados: Cardapio → CardapioTurno

### Código para Migração

```java
/**
 * Migra dados de Cardapio existentes para CardapioTurno
 * Importante: Executar apenas uma vez!
 */
public class CardapioMigracao {

    public static void migrarParaCardapioTurno(
            CardapioRepository cardapioOld,
            CardapioTurnoRepository cardapioNew,
            String turnoDefault
    ) {
        // Buscar todos os cardápios antigos
        List<Cardapio> cardapiosAntigos = cardapioOld.buscarTodos();

        // Converter para CardapioTurno
        for (Cardapio old : cardapiosAntigos) {
            CardapioTurno novo = new CardapioTurno(old.getData(), turnoDefault);
            novo.setPratoPrincipal(old.getPratoPrincipal());
            novo.setGuarnicao(old.getGuarnicao());
            novo.setAcompanhamento(old.getAcompanhamento());
            novo.setSalada(old.getSalada());
            novo.setSobremesa(old.getSobremesa());
            novo.setImagemUrl(old.getImagemUrl());
            novo.setFavorito(old.isFavorito());

            // Salvar no novo sistema
            cardapioNew.inserirLocal(novo);
        }
    }
}
```

---

## 7. Exemplo de Uso Completo

```java
public class MainActivity extends AppCompatActivity {

    private CardapioTurnoViewModel viewModel;
    private RecyclerView recyclerView;
    private CardapioTurnoAdapter adapter;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obter usuário logado
        usuarioLogado = obterUsuarioLogado();

        // Configurar ViewModel
        viewModel = new ViewModelProvider(this).get(CardapioTurnoViewModel.class);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCardapios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardapioTurnoAdapter(new OnCardapioClickListener() {
            @Override
            public void onCardapioClick(CardapioTurno cardapio) {
                mostrarDetalhesCardapio(cardapio);
            }

            @Override
            public void onFavoritoClick(String cardapioId, boolean isFavorito) {
                viewModel.atualizarFavorito(cardapioId, isFavorito);
            }
        });
        recyclerView.setAdapter(adapter);

        // Carregar dados
        if (usuarioLogado != null) {
            viewModel.carregarCardapiosPorTurno(usuarioLogado.getTurno())
                    .observe(this, adapter::submitList);
        }
    }

    private void mostrarDetalhesCardapio(CardapioTurno cardapio) {
        // Mostrar diálogo ou abrir activity com detalhes
        String detalhes = String.format(
                "Data: %s\nTurno: %s\nPrato: %s\nSalada: %s\nSobremesa: %s",
                cardapio.getData(),
                cardapio.getTurnoFormatado(),
                cardapio.getPratoPrincipal(),
                cardapio.getSalada(),
                cardapio.getSobremesa()
        );
        
        new AlertDialog.Builder(this)
                .setTitle("Cardápio")
                .setMessage(detalhes)
                .setPositiveButton("OK", null)
                .show();
    }

    private Usuario obterUsuarioLogado() {
        // TODO: Implementar
        return null;
    }
}
```

---

## ✅ Checklist de Implementação

- [ ] Criar `CardapioTurnoViewModel`
- [ ] Criar `CardapioTurnoAdapter`
- [ ] Criar layout `item_cardapio_turno.xml`
- [ ] Criar drawable `item_cardapio_background.xml`
- [ ] Atualizar `CardapioSemanalActivity` para usar novo repositório
- [ ] Testar filtro por turno
- [ ] Testar favoritos
- [ ] Testar sincronização com Firebase
- [ ] Executar migração de dados (se houver cardápios antigos)
- [ ] Documentar no README

---

**Próxima Etapa**: Implementar gerenciamento de cardápios por turno na interface admin
