package com.example.visualizadorapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.visualizadorapp.model.CardapioTurno;
import com.example.visualizadorapp.model.Funcionario;
import com.example.visualizadorapp.repository.CardapioTurnoRepository;
import com.example.visualizadorapp.repository.FuncionarioRepository;
import com.example.visualizadorapp.utils.FiltroVisibilidadeCardapio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel para Cardápios Filtrados por Turno
 * 
 * Centraliza a lógica de filtro de cardápios baseado em turno do usuário
 * Facilita integração com UI (Activities e Fragments)
 */
public class CardapiosViewModel extends ViewModel {
    
    private FuncionarioRepository funcionarioRepository;
    private CardapioTurnoRepository cardapioRepository;
    private FirebaseAuth mAuth;
    
    private final MutableLiveData<List<CardapioTurno>> cardapiosVisiveis = new MutableLiveData<>();
    private final MutableLiveData<String> descricaoRegras = new MutableLiveData<>();
    private final MutableLiveData<Boolean> carregando = new MutableLiveData<>(false);
    private final MutableLiveData<String> erro = new MutableLiveData<>();
    
    private Funcionario funcionarioAtual;
    
    public CardapiosViewModel(FuncionarioRepository funcionarioRepository,
                            CardapioTurnoRepository cardapioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.cardapioRepository = cardapioRepository;
        this.mAuth = FirebaseAuth.getInstance();
    }
    
    /**
     * Carrega cardápios filtrados para o usuário logado
     * Busca automaticamente turno do usuário e aplica filtros
     */
    public void carregarCardapiosParaUsuario() {
        carregando.setValue(true);
        erro.setValue(null);
        
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            erro.setValue("Usuário não autenticado");
            carregando.setValue(false);
            return;
        }
        
        // 1. Buscar dados do funcionário (turno, horário) - Corrigido nome do método
        funcionarioRepository.buscarPorEmailAsync(user.getEmail()).observeForever(funcionario -> {
            if (funcionario == null) {
                erro.setValue("Usuário não encontrado no banco de funcionários");
                carregando.setValue(false);
                return;
            }
            
            this.funcionarioAtual = funcionario;
            String horario = funcionario.getHorario();
            String plantao = funcionario.getPlantao();
            
            // 2. Exibir descrição das regras
            descricaoRegras.setValue(
                "Suas regras de visibilidade:\n" +
                FiltroVisibilidadeCardapio.getDescricaoRegras(horario)
            );
            
            // 3. Buscar cardápios da próxima semana
            String dataInicio = FiltroVisibilidadeCardapio.getDataHoje();
            String dataFim = FiltroVisibilidadeCardapio.getDataFutura(7);
            
            // Corrigido nome do método
            cardapioRepository.buscarPorPeriodoAsync(dataInicio, dataFim)
                .observeForever(todosCardapios -> {
                    
                    // 4. Filtrar cardápios visíveis
                    List<CardapioTurno> cardapiosVis = filtrarCardapios(
                        todosCardapios,
                        horario,
                        plantao
                    );
                    
                    cardapiosVisiveis.setValue(cardapiosVis);
                    carregando.setValue(false);
                });
        });
    }
    
    /**
     * Carrega cardápios de um período (Admin)
     */
    public void carregarCardapiosAdmin(String dataInicio, String dataFim) {
        carregando.setValue(true);
        
        // Corrigido nome do método
        cardapioRepository.buscarPorPeriodoAsync(dataInicio, dataFim)
            .observeForever(cardapios -> {
                cardapiosVisiveis.setValue(cardapios != null ? cardapios : new ArrayList<>());
                carregando.setValue(false);
            });
    }
    
    /**
     * Carrega cardápios apenas para um turno específico
     */
    public void carregarCardapiosPorTurno(String turno, String dataInicio, String dataFim) {
        carregando.setValue(true);
        
        // Corrigido nome do método
        cardapioRepository.buscarPorPeriodoAsync(dataInicio, dataFim)
            .observeForever(todosCardapios -> {
                List<CardapioTurno> resultado = new ArrayList<>();
                
                if (todosCardapios != null) {
                    for (CardapioTurno cardapio : (List<CardapioTurno>) todosCardapios) {
                        if (cardapio.getTurno().equalsIgnoreCase(turno)) {
                            resultado.add(cardapio);
                        }
                    }
                }
                
                cardapiosVisiveis.setValue(resultado);
                carregando.setValue(false);
            });
    }
    
    /**
     * Filtra cardápios baseado em horário e regras de visibilidade
     * 
     * @param cardapios Lista de cardápios
     * @param horario Horário de trabalho (06:00-18:00, 18:00-06:00, 07:00-17:00)
     * @param plantao Plantão (A ou B) - usado para 5X2 para determinar dias
     */
    private List<CardapioTurno> filtrarCardapios(List<CardapioTurno> cardapios,
                                                  String horario,
                                                  String plantao) {
        List<CardapioTurno> resultado = new ArrayList<>();
        
        if (cardapios == null || cardapios.isEmpty()) {
            return resultado;
        }
        
        // Obter dias de trabalho (leva em conta horario + plantao)
        int[] diasTrabalho = obterDiasTrabalho(horario, plantao);
        
        for (CardapioTurno cardapio : cardapios) {
            boolean podeVer = FiltroVisibilidadeCardapio.podeVerCardapio(
                horario,
                cardapio.getTurno(),
                cardapio.getData(),
                diasTrabalho
            );
            
            if (podeVer) {
                resultado.add(cardapio);
            }
        }
        
        return resultado;
    }
    
    /**
     * Obtém array de dias de trabalho baseado no horário e plantão
     * MANHA/NOITE: retorna dias do plantão específico (A ou B)
     * 5X2: retorna segunda-sexta (plantão não aplicável)
     */
    private int[] obterDiasTrabalho(String horario, String plantao) {
        if (horario == null) {
            return null;
        }
        
        String horarioNormalizado = horario.trim();
        
        // 5X2 trabalha segunda-sexta (ignora plantão)
        if (horarioNormalizado.contains("07:00-17:00")) {
            return FiltroVisibilidadeCardapio.getDias5X2Padrao();
        }
        
        // MANHA/NOITE: retorna dias do plantão (A ou B)
        if (plantao != null) {
            String plantaoNormalizado = plantao.trim();
            if (plantaoNormalizado.contains("A")) {
                return FiltroVisibilidadeCardapio.getDiasPlantaoA();
            } else if (plantaoNormalizado.contains("B")) {
                return FiltroVisibilidadeCardapio.getDiasPlantaoB();
            }
        }
        
        // Padrão: se não conseguir determinar, retorna null (vê todos os dias)
        return null;
    }
    
    // ===== GETTERS (LiveData) =====
    
    public LiveData<List<CardapioTurno>> getCardapiosVisiveis() {
        return cardapiosVisiveis;
    }
    
    public LiveData<String> getDescricaoRegras() {
        return descricaoRegras;
    }
    
    public LiveData<Boolean> isCarregando() {
        return carregando;
    }
    
    public LiveData<String> getErro() {
        return erro;
    }
    
    public Funcionario getFuncionarioAtual() {
        return funcionarioAtual;
    }
    
    public void setRepositories(FuncionarioRepository funcRepo, 
                                CardapioTurnoRepository cardapioRepo) {
        this.funcionarioRepository = funcRepo;
        this.cardapioRepository = cardapioRepo;
    }
}
