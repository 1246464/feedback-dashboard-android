package com.example.visualizadorapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.visualizadorapp.model.MudancaCardapio;
import com.example.visualizadorapp.repository.MudancaCardapioRepository;

import java.util.List;

public class MudancaCardapioViewModel extends AndroidViewModel {
    private MudancaCardapioRepository repository;
    
    public MudancaCardapioViewModel(@NonNull Application application) {
        super(application);
        repository = new MudancaCardapioRepository(application);
    }
    
    // Observar mudanças
    public LiveData<List<MudancaCardapio>> getMudancasPorData(String data) {
        return repository.getMudancasPorData(data);
    }
    
    public LiveData<List<MudancaCardapio>> getMudancasNaoNotificadas() {
        return repository.getMudancasNaoNotificadas();
    }
    
    public LiveData<List<MudancaCardapio>> getMudancasPorPeriodo(String dataInicio, String dataFim) {
        return repository.getMudancasPorPeriodo(dataInicio, dataFim);
    }
    
    public LiveData<Integer> getCountMudancasPorData(String data) {
        return repository.getCountMudancasPorData(data);
    }
    
    public LiveData<List<MudancaCardapio>> getUltimasMudancas() {
        return repository.getUltimasMudancas();
    }
    
    // Operações
    public void inserirMudanca(MudancaCardapio mudanca) {
        repository.insert(mudanca);
    }
    
    public void atualizarMudanca(MudancaCardapio mudanca) {
        repository.update(mudanca);
    }
    
    public void deletarMudanca(MudancaCardapio mudanca) {
        repository.delete(mudanca);
    }
    
    public void marcarComoNotificado(int id) {
        repository.marcarComoNotificado(id);
    }
    
    public void deleteMudancasAntigas(String dataLimite) {
        repository.deleteMudancasAntigas(dataLimite);
    }
}
