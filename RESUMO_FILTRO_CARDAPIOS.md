# 🚀 Quick Guide: Filtro de Cardápios por Turno

## 📌 Resumo Rápido

Implementou-se um **sistema de filtro de cardápios** que controla quais cardápios cada usuário pode ver com base em seu turno de trabalho.

### 3 Regras Simples:

```
┌──────────────────────────────────────────────────────┐
│ TURNO MANHA (06:00-18:00)                            │
├──────────────────────────────────────────────────────┤
│ ✅ Vê: Almoço (TARDE)     todos os dias             │
│ ❌ Não vê: Jantar (NOITE) nenhum dia                │
└──────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────┐
│ TURNO NOITE (18:00-06:00)                            │
├──────────────────────────────────────────────────────┤
│ ✅ Vê: Jantar (NOITE)     todos os dias             │
│ ❌ Não vê: Almoço (TARDE) nenhum dia                │
└──────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────┐
│ TURNO 5X2 (07:00-17:00, seg-sex)                    │
├──────────────────────────────────────────────────────┤
│ ✅ Vê: Almoço (TARDE)     APENAS dias que trabalha  │
│        + máximo 7 dias à frente                     │
│ ❌ Não vê: Jantar (NOITE) nenhum dia                │
│           Cardápios além de 7 dias                  │
│           Cardápios de sábado/domingo               │
└──────────────────────────────────────────────────────┘
```

---

## 📁 Arquivos Criados

### 1. **FiltroVisibilidadeCardapio.java**
Classe com a lógica principal de filtro

```java
// Verificar se usuário pode ver cardápio
boolean podeVer = FiltroVisibilidadeCardapio.podeVerCardapio(
    "MANHA",          // turno do usuário
    "06:00-18:00",    // horário
    "TARDE",          // turno do cardápio
    "2026-05-15",     // data
    null              // diasTrabalho (null para MANHA/NOITE)
);
```

### 2. **FiltroVisibilidadeCardapioTest.java**
50+ testes unitários cobrindo todos os cenários

### 3. **CardapiosViewModel.java**
ViewModel que encapsula a lógica e facilita integração

```java
CardapiosViewModel vm = new ViewModelProvider(this).get(CardapiosViewModel.class);
vm.carregarCardapiosParaUsuario();
vm.getCardapiosVisiveis().observe(this, cardapios -> {
    adapter.setData(cardapios);
});
```

---

## 💻 Como Usar (3 Formas)

### Forma 1️⃣: Direto com FiltroVisibilidadeCardapio (Simples)

```java
// No seu Activity ou Fragment
public void exibirCardapio() {
    String turnoUsuario = usuario.getTurno();  // "MANHA"
    String horarioUsuario = usuario.getHorario(); // "06:00-18:00"
    
    List<CardapioTurno> todosCardapios = obterDoFirebase();
    List<CardapioTurno> cardapiosVisiveis = new ArrayList<>();
    
    for (CardapioTurno cardapio : todosCardapios) {
        if (FiltroVisibilidadeCardapio.podeVerCardapio(
            turnoUsuario,
            horarioUsuario,
            cardapio.getTurno(),    // "TARDE" ou "NOITE"
            cardapio.getData(),     // "2026-05-15"
            null
        )) {
            cardapiosVisiveis.add(cardapio);
        }
    }
    
    adapter.setData(cardapiosVisiveis);
}
```

### Forma 2️⃣: Com ViewModel (Recomendado)

```java
public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Criar ViewModel
        CardapiosViewModel vm = new ViewModelProvider(this)
            .get(CardapiosViewModel.class);
        
        // Observar cardápios filtrados
        vm.getCardapiosVisiveis().observe(this, cardapios -> {
            adapter.setData(cardapios);
        });
        
        // Observar descrição das regras
        vm.getDescricaoRegras().observe(this, regras -> {
            tvRegras.setText(regras);
        });
        
        // Observar carregamento
        vm.isCarregando().observe(this, carregando -> {
            if (carregando) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });
        
        // Carregar dados
        vm.carregarCardapiosParaUsuario();
    }
}
```

### Forma 3️⃣: Com LiveData e Fragment (Padrão Android)

```java
public class CardapioFragment extends Fragment {
    
    private CardapiosViewModel viewModel;
    private RecyclerView recyclerView;
    private CardapioAdapter adapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cardapio, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(CardapiosViewModel.class);
        recyclerView = view.findViewById(R.id.rv_cardapios);
        adapter = new CardapioAdapter();
        
        // Observar e atualizar UI
        viewModel.getCardapiosVisiveis().observe(getViewLifecycleOwner(), 
            cardapios -> adapter.submitList(cardapios));
        
        viewModel.getDescricaoRegras().observe(getViewLifecycleOwner(),
            regras -> view.findViewById(R.id.tv_regras).setText(regras));
        
        recyclerView.setAdapter(adapter);
        
        // Carregar dados
        viewModel.carregarCardapiosParaUsuario();
    }
}
```

---

## 📋 Exemplos por Turno

### Usuário MANHA Vendo Cardápio

```
Data    | Turno  | Resultado | Motivo
--------|--------|-----------|----------------------------------
Hoje    | TARDE  | ✅ Mostra | MANHA trabalha até 18h
Hoje    | NOITE  | ❌ Oculta | MANHA sai antes do jantar
Amanhã  | TARDE  | ✅ Mostra | MANHA trabalha até 18h
Amanhã  | NOITE  | ❌ Oculta | MANHA sai antes do jantar
```

### Usuário NOITE Vendo Cardápio

```
Data    | Turno  | Resultado | Motivo
--------|--------|-----------|----------------------------------
Hoje    | TARDE  | ❌ Oculta | NOITE entra depois do almoço
Hoje    | NOITE  | ✅ Mostra | NOITE trabalha no jantar
Amanhã  | TARDE  | ❌ Oculta | NOITE entra depois do almoço
Amanhã  | NOITE  | ✅ Mostra | NOITE trabalha no jantar
```

### Usuário 5X2 (segunda-feira) Vendo Cardápio

```
Data           | Dia   | Turno  | Resultado | Motivo
----------------|-------|--------|-----------|----------------------------------
Segunda        | Trab. | TARDE  | ✅ Mostra | 5X2 trabalha seg-sex
Sábado         | NÃO   | TARDE  | ❌ Oculta | 5X2 não trabalha sábado
Daqui a 6 dias | Trab. | TARDE  | ✅ Mostra | Até 7 dias
Daqui a 9 dias | Trab. | TARDE  | ❌ Oculta | Além de 7 dias
Qualquer       | ----  | NOITE  | ❌ Oculta | 5X2 sai antes do jantar
```

---

## 🔧 Integração Passo a Passo

### Passo 1: Adicionar ViewModel ao projeto
✅ CardapiosViewModel.java criado

### Passo 2: Em sua Activity principal

```java
public class MainActivity extends AppCompatActivity {
    
    private CardapiosViewModel viewModel;
    private RecyclerView recyclerView;
    private CardapioAdapter adapter;
    private TextView tvRegras;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Inicializar views
        recyclerView = findViewById(R.id.rv_cardapios);
        tvRegras = findViewById(R.id.tv_regras);
        adapter = new CardapioAdapter();
        recyclerView.setAdapter(adapter);
        
        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(CardapiosViewModel.class);
        
        // Observar cardápios
        viewModel.getCardapiosVisiveis().observe(this, cardapios -> {
            adapter.setData(cardapios);  // RecyclerView atualiza automaticamente
        });
        
        // Mostrar regras ao usuário
        viewModel.getDescricaoRegras().observe(this, regras -> {
            tvRegras.setText("📋 " + regras);
        });
        
        // Carregar dados do Firebase
        viewModel.carregarCardapiosParaUsuario();
    }
}
```

### Passo 3: Layout XML

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- Exibir regras -->
    <TextView
        android:id="@+id/tv_regras"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="16dp"
        android:textSize="14sp" />
    
    <!-- Exibir cardápios filtrados -->
    <RecyclerView
        android:id="@+id/rv_cardapios"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
        
</LinearLayout>
```

---

## 🧪 Testes

```bash
# Rodar testes unitários
Clique direito em FiltroVisibilidadeCardapioTest
→ Run 'FiltroVisibilidadeCardapioTest'

# Esperado: Todos passando ✅
```

---

## 📊 Checklist de Implementação

- [ ] Classes criadas (FiltroVisibilidadeCardapio, CardapiosViewModel)
- [ ] Testes criados e passando
- [ ] ViewModel injetado em MainActivity
- [ ] LiveData observando e atualizando UI
- [ ] Cardápios filtrados exibindo corretamente
- [ ] Regras exibidas ao usuário
- [ ] Testado com:
  - [ ] Usuário MANHA
  - [ ] Usuário NOITE
  - [ ] Usuário 5X2

---

## 💡 Dicas Importantes

### 1️⃣ Sempre filtrar no ViewModel, não na UI
```java
// ❌ ERRADO: Filtrar na Activity
for (CardapioTurno c : cardapios) {
    if (c.getTurno().equals("TARDE")) { }
}

// ✅ CORRETO: Deixar o ViewModel filtrar
viewModel.carregarCardapiosParaUsuario();
```

### 2️⃣ Usar LiveData para atualizar automaticamente
```java
// ✅ Correto: Observa mudanças
viewModel.getCardapiosVisiveis().observe(this, cardapios -> {
    adapter.setData(cardapios);
});

// ❌ Errado: Chamar manualmente
adapter.setData(viewModel.getCardapiosVisiveis().getValue());
```

### 3️⃣ Sempre obter dados do Firebase antes de filtrar
```java
// O ViewModel faz isso automaticamente:
// 1. Busca usuário do Firebase
// 2. Obtém turno/horário
// 3. Busca cardápios do Firebase
// 4. Filtra
// 5. Envia para UI
```

---

## 🔗 Arquivos Relacionados

- [FiltroVisibilidadeCardapio.java](app/src/main/java/.../FiltroVisibilidadeCardapio.java)
- [FiltroVisibilidadeCardapioTest.java](app/src/test/java/.../FiltroVisibilidadeCardapioTest.java)
- [CardapiosViewModel.java](app/src/main/java/.../CardapiosViewModel.java)
- [GUIA_FILTRO_VISIBILIDADE_CARDAPIOS.md](GUIA_FILTRO_VISIBILIDADE_CARDAPIOS.md) - Guia detalhado
- [CardapioTurno.java](app/src/main/java/.../CardapioTurno.java)
- [Funcionario.java](app/src/main/java/.../Funcionario.java)

---

## 🎯 Próximos Passos

1. ✅ Lógica implementada (FiltroVisibilidadeCardapio)
2. ✅ ViewModel criado (CardapiosViewModel)
3. ✅ Testes criados
4. ⏳ **Integrar em MainActivity**
5. ⏳ Testar no emulador
6. ⏳ Integrar em VisaoSemanalActivity
7. ⏳ Integrar em todos os dashboards

---

**Status**: ✅ Pronto para integração  
**Data**: Maio 2026  
**Versão**: 1.0
