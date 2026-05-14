package com.example.visualizadorapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstatisticasActivity extends AppCompatActivity {

    private Spinner spinnerPeriodo;
    private TextView txtNenhumaEstatistica;
    private DatabaseReference database;

    // KPIs
    private TextView kpiTotalAvaliacoes;
    private TextView kpiMediaSatisfacao;
    private TextView kpiTaxaParticipacao;
    private TextView kpiCardapioDestaque;
    private TextView kpiVotosCardapio;

    private PieChart chartSatisfacaoGeral;
    private BarChart chartSetoresAtivos;
    private HorizontalBarChart chartEscolhaSetor;
    private HorizontalBarChart chartSatisfacaoSetor;
    private HorizontalBarChart chartRankingCardapios;
    private LineChart chartEvolucao;
    private BarChart chartSatisfacaoHorario;
    private BarChart chartComparacaoSemanal;

    private ArrayAdapter<String> spinnerAdapter;
    private List<String> datasDisponiveis = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatisticas);

        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();

        // Inicializa os componentes da UI
        spinnerPeriodo = findViewById(R.id.spinnerPeriodo);
        txtNenhumaEstatistica = findViewById(R.id.txtNenhumaEstatistica);
        
        // KPIs
        kpiTotalAvaliacoes = findViewById(R.id.kpiTotalAvaliacoes);
        kpiMediaSatisfacao = findViewById(R.id.kpiMediaSatisfacao);
        kpiTaxaParticipacao = findViewById(R.id.kpiTaxaParticipacao);
        kpiCardapioDestaque = findViewById(R.id.kpiCardapioDestaque);
        kpiVotosCardapio = findViewById(R.id.kpiVotosCardapio);
        
        chartSatisfacaoGeral = findViewById(R.id.chartSatisfacaoGeral);
        chartSetoresAtivos = findViewById(R.id.chartSetoresAtivos);
        chartEscolhaSetor = findViewById(R.id.chartEscolhaSetor);
        chartSatisfacaoSetor = findViewById(R.id.chartSatisfacaoSetor);
        chartRankingCardapios = findViewById(R.id.chartRankingCardapios);
        chartEvolucao = findViewById(R.id.chartEvolucao);
        chartSatisfacaoHorario = findViewById(R.id.chartSatisfacaoHorario);
        chartComparacaoSemanal = findViewById(R.id.chartComparacaoSemanal);

        configurarFiltro();
        buscarDatasDisponiveis();
    }

    private void configurarFiltro() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datasDisponiveis);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriodo.setAdapter(spinnerAdapter);

        spinnerPeriodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarEstatisticas(datasDisponiveis.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void buscarDatasDisponiveis() {
        database.child("avaliacoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                datasDisponiveis.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    datasDisponiveis.add(dataSnapshot.getKey());
                }
                if (datasDisponiveis.isEmpty()) {
                    txtNenhumaEstatistica.setVisibility(View.VISIBLE);
                } else {
                    txtNenhumaEstatistica.setVisibility(View.GONE);
                    Collections.sort(datasDisponiveis, Collections.reverseOrder());
                }
                spinnerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void carregarEstatisticas(String data) {
        Log.d("EstatisticasActivity", "Carregando estatísticas para data: " + data);
        
        // Buscar avaliações e escolhas separadamente (não a raiz inteira)
        database.child("avaliacoes").child(data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot avaliacoesSnapshot) {
                Log.d("EstatisticasActivity", "Avaliações existe: " + avaliacoesSnapshot.exists() + ", count: " + avaliacoesSnapshot.getChildrenCount());
                
                database.child("escolhas").child(data).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot escolhasSnapshot) {
                        Log.d("EstatisticasActivity", "Escolhas existe: " + escolhasSnapshot.exists() + ", count: " + escolhasSnapshot.getChildrenCount());

                        if (!avaliacoesSnapshot.exists() && !escolhasSnapshot.exists()) {
                            txtNenhumaEstatistica.setVisibility(View.VISIBLE);
                            Log.d("EstatisticasActivity", "Nenhum dado encontrado");
                            return;
                        }

                        txtNenhumaEstatistica.setVisibility(View.GONE);
                        processarKPIs(avaliacoesSnapshot, escolhasSnapshot, data);
                        processarSatisfacaoGeral(avaliacoesSnapshot);
                        processarSetoresAtivos(escolhasSnapshot);
                        processarEscolhaPorSetor(escolhasSnapshot);
                        processarSatisfacaoPorSetor(avaliacoesSnapshot);
                        processarSatisfacaoHorario(avaliacoesSnapshot);
                        processarComparacaoSemanal(data);
                        processarRankingCardapios();
                        processarEvolucaoSatisfacao();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("EstatisticasActivity", "Erro ao carregar escolhas: " + error.getMessage());
                        txtNenhumaEstatistica.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EstatisticasActivity", "Erro ao carregar avaliações: " + error.getMessage());
                txtNenhumaEstatistica.setVisibility(View.VISIBLE);
            }
        });
    }

    // --- MÉTODOS DE PROCESSAMENTO E CRIAÇÃO DOS GRÁFICOS ---

    private void processarKPIs(DataSnapshot avaliacoesSnapshot, DataSnapshot escolhasSnapshot, String data) {
        // KPI 1: Total de Avaliações
        int totalAvaliacoes = (int) avaliacoesSnapshot.getChildrenCount();
        kpiTotalAvaliacoes.setText(String.valueOf(totalAvaliacoes));
        
        // KPI 2: Média de Satisfação
        float totalSatisfacao = 0;
        int contadorAvaliacoes = 0;
        Map<String, Integer> ratingMap = new HashMap<String, Integer>() {{ 
            put("Ruim", 1); 
            put("Regular", 2); 
            put("Bom", 3); 
            put("Ótimo", 4); 
        }};
        
        for (DataSnapshot userSnapshot : avaliacoesSnapshot.getChildren()) {
            String nivel = userSnapshot.child("nivel").getValue(String.class);
            if (nivel != null) {
                nivel = nivel.replaceAll("[^a-zA-ZÀ-ÿ\\s]", "").trim();
                if (ratingMap.containsKey(nivel)) {
                    totalSatisfacao += ratingMap.get(nivel);
                    contadorAvaliacoes++;
                }
            }
        }
        
        float mediaSatisfacao = contadorAvaliacoes > 0 ? totalSatisfacao / contadorAvaliacoes : 0;
        kpiMediaSatisfacao.setText(String.format("%.1f/4", mediaSatisfacao));
        
        // KPI 3: Taxa de Participação (Total de Escolhas vs Avaliações)
        int totalEscolhas = (int) escolhasSnapshot.getChildrenCount();
        int totalPessoas = Math.max(totalAvaliacoes, totalEscolhas);
        int taxaParticipacao = totalPessoas > 0 ? (totalEscolhas * 100) / totalPessoas : 0;
        kpiTaxaParticipacao.setText(taxaParticipacao + "%");
        
        // KPI 4 e 5: Cardápio Mais Votado
        encontrarCardapioDestaque(data);
    }
    
    private void encontrarCardapioDestaque(String data) {
        database.child("escolhas").child(data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot escolhasNode) {
                Map<String, Integer> cardapioVotos = new HashMap<>();
                
                for (DataSnapshot userSnapshot : escolhasNode.getChildren()) {
                    String escolha = userSnapshot.child("escolha").getValue(String.class);
                    if (escolha != null) {
                        cardapioVotos.put(escolha, cardapioVotos.getOrDefault(escolha, 0) + 1);
                    }
                }
                
                // Encontrar a escolha mais votada
                String destaqueEscolha = "Nenhuma";
                int maxVotosCalculado = 0;
                for (Map.Entry<String, Integer> entry : cardapioVotos.entrySet()) {
                    if (entry.getValue() > maxVotosCalculado) {
                        maxVotosCalculado = entry.getValue();
                        destaqueEscolha = entry.getKey();
                    }
                }
                final int maxVotos = maxVotosCalculado;
                
                // Buscar o prato principal do cardápio dessa data
                database.child("cardapios").child(data).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot cardapioSnapshot) {
                        String pratoPrincipal = "Não disponível";
                        if (cardapioSnapshot.exists()) {
                            String prato = cardapioSnapshot.child("pratoPrincipal").getValue(String.class);
                            if (prato != null && !prato.isEmpty()) {
                                pratoPrincipal = prato;
                            }
                        }
                        
                        kpiCardapioDestaque.setText(pratoPrincipal);
                        kpiVotosCardapio.setText(String.valueOf(maxVotos));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void processarSatisfacaoGeral(DataSnapshot snapshot) {
        Log.d("EstatisticasActivity", "processarSatisfacaoGeral - Total de registros: " + snapshot.getChildrenCount());
        Map<String, Integer> contagem = new HashMap<>();
        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
            String nivel = userSnapshot.child("nivel").getValue(String.class);
            if (nivel != null) {
                // Remove emojis se existirem
                String nivelOriginal = nivel;
                nivel = nivel.replaceAll("[^a-zA-Z\u00c0-\u00ff\\s]", "").trim();
                Log.d("EstatisticasActivity", "Nível: '" + nivelOriginal + "' -> '" + nivel + "'");
                contagem.put(nivel, contagem.getOrDefault(nivel, 0) + 1);
            }
        }

        Log.d("EstatisticasActivity", "Contagem final: " + contagem.toString());

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        if (entries.isEmpty()) {
            Log.d("EstatisticasActivity", "Gráfico 1: Nenhum dado para exibir");
            chartSatisfacaoGeral.setVisibility(View.GONE);
            return;
        }

        chartSatisfacaoGeral.setVisibility(View.VISIBLE);
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(14f);
        dataSet.setValueFormatter(new PercentFormatter(chartSatisfacaoGeral));

        PieData pieData = new PieData(dataSet);
        chartSatisfacaoGeral.setData(pieData);
        chartSatisfacaoGeral.setUsePercentValues(true);
        chartSatisfacaoGeral.getDescription().setEnabled(false);
        chartSatisfacaoGeral.setEntryLabelTextSize(14f);
        chartSatisfacaoGeral.setDrawEntryLabels(true);
        chartSatisfacaoGeral.setExtraOffsets(10, 10, 10, 10);
        chartSatisfacaoGeral.invalidate(); // Atualiza o gráfico
        Log.d("EstatisticasActivity", "Gráfico 1 (Satisfação Geral): Exibido com " + entries.size() + " entradas");
    }

    private void processarSetoresAtivos(DataSnapshot snapshot) {
        Log.d("EstatisticasActivity", "processarSetoresAtivos - Total de registros: " + snapshot.getChildrenCount());
        Map<String, Integer> contagem = new HashMap<>();
        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
            String setor = userSnapshot.child("setor").getValue(String.class);
            if (setor != null) {
                contagem.put(setor, contagem.getOrDefault(setor, 0) + 1);
            }
        }

        Log.d("EstatisticasActivity", "Setores ativos: " + contagem.toString());

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        if (entries.isEmpty()) {
            Log.d("EstatisticasActivity", "Gráfico 2: Nenhum dado para exibir");
            chartSetoresAtivos.setVisibility(View.GONE);
            return;
        }

        chartSetoresAtivos.setVisibility(View.VISIBLE);
        BarDataSet dataSet = new BarDataSet(entries, "Nº de Votos");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);
        chartSetoresAtivos.setData(barData);
        chartSetoresAtivos.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartSetoresAtivos.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartSetoresAtivos.getXAxis().setGranularity(1f);
        chartSetoresAtivos.getXAxis().setTextSize(12f);
        chartSetoresAtivos.getAxisLeft().setTextSize(12f);
        chartSetoresAtivos.getLegend().setTextSize(12f);
        chartSetoresAtivos.getDescription().setEnabled(false);
        chartSetoresAtivos.setFitBars(true);
        chartSetoresAtivos.invalidate();
        Log.d("EstatisticasActivity", "Gráfico 2: Exibido com sucesso");
    }

    private void processarEscolhaPorSetor(DataSnapshot snapshot) {
        // Mapeia: Setor -> [Contagem Prato, Contagem Ovo]
        Map<String, float[]> contagem = new HashMap<>();
        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
            String setor = userSnapshot.child("setor").getValue(String.class);
            String escolha = userSnapshot.child("escolha").getValue(String.class);
            if (setor != null && escolha != null) {
                float[] votos = contagem.getOrDefault(setor, new float[2]);
                if (escolha.equals("Prato principal")) {
                    votos[0]++;
                } else {
                    votos[1]++;
                }
                contagem.put(setor, votos);
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, float[]> entry : contagem.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setStackLabels(new String[]{"Prato principal", "Ovo"});
        dataSet.setColors(new int[]{Color.rgb(60, 179, 113), Color.rgb(255, 215, 0)});
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        chartEscolhaSetor.setData(barData);
        chartEscolhaSetor.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartEscolhaSetor.getXAxis().setTextSize(12f);
        chartEscolhaSetor.getAxisLeft().setTextSize(12f);
        chartEscolhaSetor.getLegend().setTextSize(12f);
        chartEscolhaSetor.getDescription().setEnabled(false);
        chartEscolhaSetor.setFitBars(true);
        chartEscolhaSetor.invalidate();
    }

    private void processarSatisfacaoPorSetor(DataSnapshot snapshot) {
        // Mapeia: Setor -> [Soma das Notas, Contagem de Avaliações]
        Map<String, float[]> scores = new HashMap<>();
        Map<String, Integer> ratingMap = new HashMap<String, Integer>() {{ put("Ruim", 1); put("Regular", 2); put("Bom", 3); put("Ótimo", 4); }};

        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
            String setor = userSnapshot.child("setor").getValue(String.class);
            String nivel = userSnapshot.child("nivel").getValue(String.class);
            if (setor != null && nivel != null) {
                // Remove emojis se existirem
                nivel = nivel.replaceAll("[^a-zA-ZÀ-ÿ\\s]", "").trim();
                if (ratingMap.containsKey(nivel)) {
                    float[] data = scores.getOrDefault(setor, new float[2]);
                    data[0] += ratingMap.get(nivel);
                    data[1]++;
                    scores.put(setor, data);
                }
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, float[]> entry : scores.entrySet()) {
            float media = entry.getValue()[0] / entry.getValue()[1]; // Média
            entries.add(new BarEntry(index, media));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Média de Satisfação (1 a 4)");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        chartSatisfacaoSetor.setData(barData);
        chartSatisfacaoSetor.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartSatisfacaoSetor.getXAxis().setTextSize(12f);
        chartSatisfacaoSetor.getAxisLeft().setAxisMinimum(0f);
        chartSatisfacaoSetor.getAxisLeft().setAxisMaximum(4f);
        chartSatisfacaoSetor.getAxisLeft().setTextSize(12f);
        chartSatisfacaoSetor.getLegend().setTextSize(12f);
        chartSatisfacaoSetor.getDescription().setEnabled(false);
        chartSatisfacaoSetor.setFitBars(true);
        chartSatisfacaoSetor.invalidate();
    }

    private void processarRankingCardapios() {
        Log.d("EstatisticasActivity", "processarRankingCardapios - Iniciando");
        
        // Buscar escolhas e cardápios separadamente
        database.child("escolhas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot escolhasNode) {
                Log.d("EstatisticasActivity", "Total de datas com escolhas: " + escolhasNode.getChildrenCount());
                
                database.child("cardapios").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot cardapiosNode) {
                        Map<String, Integer> cardapioVotos = new HashMap<>();
                        
                        // Percorrer todas as datas de escolhas
                        for (DataSnapshot dataSnapshot : escolhasNode.getChildren()) {
                            String data = dataSnapshot.getKey();
                            
                            // Buscar o cardápio dessa data
                            DataSnapshot cardapioSnapshot = cardapiosNode.child(data);
                            if (cardapioSnapshot.exists()) {
                                String prato = cardapioSnapshot.child("pratoPrincipal").getValue(String.class);
                                if (prato != null && !prato.isEmpty()) {
                                    int votos = (int) dataSnapshot.getChildrenCount();
                                    String chave = prato + " (" + data.substring(5) + ")";
                                    cardapioVotos.put(chave, cardapioVotos.getOrDefault(chave, 0) + votos);
                                }
                            }
                        }
                        
                        Log.d("EstatisticasActivity", "Cardápios encontrados: " + cardapioVotos.toString());
                        
                        // Ordenar por votos e pegar top 10
                        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(cardapioVotos.entrySet());
                        Collections.sort(sortedList, (a, b) -> b.getValue().compareTo(a.getValue()));
                        
                        List<BarEntry> entries = new ArrayList<>();
                        List<String> labels = new ArrayList<>();
                        
                        int count = Math.min(10, sortedList.size());
                        for (int i = 0; i < count; i++) {
                            Map.Entry<String, Integer> entry = sortedList.get(i);
                            entries.add(new BarEntry(i, entry.getValue()));
                            labels.add(entry.getKey());
                        }
                        
                        if (entries.isEmpty()) {
                            Log.d("EstatisticasActivity", "Gráfico 5 (Ranking): Sem dados");
                            chartRankingCardapios.setVisibility(View.GONE);
                            return;
                        }
                        
                        chartRankingCardapios.setVisibility(View.VISIBLE);
                        BarDataSet dataSet = new BarDataSet(entries, "Número de Votos");
                        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        dataSet.setValueTextSize(12f);
                        
                        BarData barData = new BarData(dataSet);
                        chartRankingCardapios.setData(barData);
                        chartRankingCardapios.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                        chartRankingCardapios.getXAxis().setTextSize(11f);
                        chartRankingCardapios.getAxisLeft().setTextSize(12f);
                        chartRankingCardapios.getLegend().setTextSize(12f);
                        chartRankingCardapios.getDescription().setEnabled(false);
                        chartRankingCardapios.setFitBars(true);
                        chartRankingCardapios.invalidate();
                        Log.d("EstatisticasActivity", "Gráfico 5 (Ranking): Exibido com " + entries.size() + " cardápios");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("EstatisticasActivity", "Erro ao carregar cardápios: " + error.getMessage());
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EstatisticasActivity", "Erro ao carregar escolhas: " + error.getMessage());
            }
        });
    }

    private void processarEvolucaoSatisfacao() {
        Log.d("EstatisticasActivity", "processarEvolucaoSatisfacao - Iniciando");
        database.child("avaliacoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("EstatisticasActivity", "Total de datas: " + snapshot.getChildrenCount());
                Map<String, Float> mediasPorData = new HashMap<>();
                Map<String, Integer> ratingMap = new HashMap<String, Integer>() {{ 
                    put("Ruim", 1); put("Regular", 2); put("Bom", 3); put("Ótimo", 4); 
                }};
                
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String data = dataSnapshot.getKey();
                    float soma = 0;
                    int count = 0;
                    
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String nivel = userSnapshot.child("nivel").getValue(String.class);
                        if (nivel != null) {
                            // Remove emojis se existirem
                            nivel = nivel.replaceAll("[^a-zA-ZÀ-ÿ\\s]", "").trim();
                            if (ratingMap.containsKey(nivel)) {
                                soma += ratingMap.get(nivel);
                                count++;
                            }
                        }
                    }
                    
                    if (count > 0) {
                        mediasPorData.put(data, soma / count);
                    }
                }
                
                Log.d("EstatisticasActivity", "Médias: " + mediasPorData.toString());
                
                // Ordenar por data
                List<String> datas = new ArrayList<>(mediasPorData.keySet());
                Collections.sort(datas);
                
                // Pegar últimos 10 dias
                int start = Math.max(0, datas.size() - 10);
                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                
                for (int i = start; i < datas.size(); i++) {
                    String data = datas.get(i);
                    entries.add(new Entry(i - start, mediasPorData.get(data)));
                    labels.add(data.substring(5)); // Mostra apenas MM-DD
                }
                
                if (entries.isEmpty()) {
                    Log.d("EstatisticasActivity", "Gráfico 6: Sem dados");
                    chartEvolucao.setVisibility(View.GONE);
                    return;
                }
                
                chartEvolucao.setVisibility(View.VISIBLE);
                LineDataSet dataSet = new LineDataSet(entries, "Média de Satisfação");
                dataSet.setColor(Color.rgb(33, 150, 243));
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(12f);
                dataSet.setLineWidth(3f);
                dataSet.setCircleRadius(5f);
                dataSet.setCircleColor(Color.rgb(33, 150, 243));
                dataSet.setDrawValues(true);
                
                LineData lineData = new LineData(dataSet);
                chartEvolucao.setData(lineData);
                chartEvolucao.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                chartEvolucao.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                chartEvolucao.getXAxis().setTextSize(12f);
                chartEvolucao.getAxisLeft().setAxisMinimum(0f);
                chartEvolucao.getAxisLeft().setAxisMaximum(4f);
                chartEvolucao.getAxisLeft().setTextSize(12f);
                chartEvolucao.getLegend().setTextSize(12f);
                chartEvolucao.getDescription().setEnabled(false);
                chartEvolucao.invalidate();
                Log.d("EstatisticasActivity", "Gráfico 6 (Evolução): Exibido com " + entries.size() + " pontos");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EstatisticasActivity", "Erro evolução: " + error.getMessage());
            }
        });
    }

    private void processarSatisfacaoHorario(DataSnapshot snapshot) {
        Log.d("EstatisticasActivity", "processarSatisfacaoHorario - Iniciando");
        Map<String, float[]> satisfacaoPorHorario = new HashMap<>();
        // [0] = soma, [1] = contagem
        satisfacaoPorHorario.put("MANHÃ", new float[2]);
        satisfacaoPorHorario.put("NOITE", new float[2]);
        satisfacaoPorHorario.put("5X2", new float[2]);

        Map<String, Integer> ratingMap = new HashMap<String, Integer>() {{
            put("Ruim", 1);
            put("Regular", 2);
            put("Bom", 3);
            put("Ótimo", 4);
        }};

        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
            String horario = userSnapshot.child("horario").getValue(String.class);
            String nivel = userSnapshot.child("nivel").getValue(String.class);
            if (horario != null && nivel != null) {
                // Normalizar horário
                String horarioChave = "5X2";
                if (horario.contains("06:00")) horarioChave = "MANHÃ";
                else if (horario.contains("18:00")) horarioChave = "NOITE";

                nivel = nivel.replaceAll("[^a-zA-ZÀ-ÿ\\s]", "").trim();
                if (ratingMap.containsKey(nivel)) {
                    float[] dados = satisfacaoPorHorario.getOrDefault(horarioChave, new float[2]);
                    dados[0] += ratingMap.get(nivel);
                    dados[1]++;
                    satisfacaoPorHorario.put(horarioChave, dados);
                }
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, float[]> entry : satisfacaoPorHorario.entrySet()) {
            float media = entry.getValue()[1] > 0 ? entry.getValue()[0] / entry.getValue()[1] : 0;
            entries.add(new BarEntry(index, media));
            labels.add(entry.getKey());
            index++;
        }

        if (entries.isEmpty()) {
            chartSatisfacaoHorario.setVisibility(View.GONE);
            return;
        }

        chartSatisfacaoHorario.setVisibility(View.VISIBLE);
        BarDataSet dataSet = new BarDataSet(entries, "Média de Satisfação");
        dataSet.setColors(new int[]{Color.rgb(76, 175, 80), Color.rgb(244, 67, 54), Color.rgb(255, 193, 7)});
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        chartSatisfacaoHorario.setData(barData);
        chartSatisfacaoHorario.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartSatisfacaoHorario.getXAxis().setTextSize(12f);
        chartSatisfacaoHorario.getAxisLeft().setAxisMinimum(0f);
        chartSatisfacaoHorario.getAxisLeft().setAxisMaximum(4f);
        chartSatisfacaoHorario.getAxisLeft().setTextSize(12f);
        chartSatisfacaoHorario.getLegend().setTextSize(12f);
        chartSatisfacaoHorario.getDescription().setEnabled(false);
        chartSatisfacaoHorario.setFitBars(true);
        chartSatisfacaoHorario.invalidate();
        Log.d("EstatisticasActivity", "Gráfico 7 (Satisfação por Horário): Exibido");
    }

    private void processarComparacaoSemanal(String dataAtual) {
        Log.d("EstatisticasActivity", "processarComparacaoSemanal - Data atual: " + dataAtual);

        database.child("avaliacoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Float> mediasPorData = new HashMap<>();
                Map<String, Integer> ratingMap = new HashMap<String, Integer>() {{
                    put("Ruim", 1);
                    put("Regular", 2);
                    put("Bom", 3);
                    put("Ótimo", 4);
                }};

                // Processar todas as avaliações
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String data = dataSnapshot.getKey();
                    float soma = 0;
                    int count = 0;

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String nivel = userSnapshot.child("nivel").getValue(String.class);
                        if (nivel != null) {
                            nivel = nivel.replaceAll("[^a-zA-ZÀ-ÿ\\s]", "").trim();
                            if (ratingMap.containsKey(nivel)) {
                                soma += ratingMap.get(nivel);
                                count++;
                            }
                        }
                    }

                    if (count > 0) {
                        mediasPorData.put(data, soma / count);
                    }
                }

                // Calcular média da semana atual (últimos 7 dias) e semana anterior (7 dias antes)
                List<String> datas = new ArrayList<>(mediasPorData.keySet());
                Collections.sort(datas);

                float mediaSemanaPosterior = 0;
                float mediaSemanaPrior = 0;
                int countPosterior = 0;
                int countPrior = 0;

                // Usar a data atual como referência
                int indexAtual = datas.indexOf(dataAtual);
                if (indexAtual >= 0) {
                    // Semana atual: últimos 7 dias antes da data atual
                    for (int i = Math.max(0, indexAtual - 6); i <= indexAtual; i++) {
                        mediaSemanaPosterior += mediasPorData.get(datas.get(i));
                        countPosterior++;
                    }
                    mediaSemanaPosterior = countPosterior > 0 ? mediaSemanaPosterior / countPosterior : 0;

                    // Semana anterior: 7 dias antes da semana atual
                    for (int i = Math.max(0, indexAtual - 13); i < Math.max(0, indexAtual - 6); i++) {
                        if (i >= 0) {
                            mediaSemanaPrior += mediasPorData.get(datas.get(i));
                            countPrior++;
                        }
                    }
                    mediaSemanaPrior = countPrior > 0 ? mediaSemanaPrior / countPrior : 0;
                }

                List<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry(0, mediaSemanaPrior));
                entries.add(new BarEntry(1, mediaSemanaPosterior));

                List<String> labels = new ArrayList<>();
                labels.add("Semana Anterior");
                labels.add("Semana Atual");

                BarDataSet dataSet = new BarDataSet(entries, "Média de Satisfação");
                dataSet.setColors(new int[]{Color.rgb(158, 158, 158), Color.rgb(33, 150, 243)});
                dataSet.setValueTextSize(12f);

                BarData barData = new BarData(dataSet);
                chartComparacaoSemanal.setData(barData);
                chartComparacaoSemanal.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                chartComparacaoSemanal.getXAxis().setTextSize(12f);
                chartComparacaoSemanal.getAxisLeft().setAxisMinimum(0f);
                chartComparacaoSemanal.getAxisLeft().setAxisMaximum(4f);
                chartComparacaoSemanal.getAxisLeft().setTextSize(12f);
                chartComparacaoSemanal.getLegend().setTextSize(12f);
                chartComparacaoSemanal.getDescription().setEnabled(false);
                chartComparacaoSemanal.setFitBars(true);
                chartComparacaoSemanal.invalidate();
                Log.d("EstatisticasActivity", "Gráfico 8 (Comparação Semanal): Semana Anterior=" + mediaSemanaPrior + ", Semana Atual=" + mediaSemanaPosterior);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EstatisticasActivity", "Erro comparação semanal: " + error.getMessage());
            }
        });
    }
}
