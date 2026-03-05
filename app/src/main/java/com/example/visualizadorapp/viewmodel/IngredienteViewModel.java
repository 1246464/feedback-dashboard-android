package com.example.visualizadorapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.visualizadorapp.model.Ingrediente;
import com.example.visualizadorapp.repository.IngredienteRepository;

import java.util.List;

public class IngredienteViewModel extends AndroidViewModel {
    private IngredienteRepository repository;
    
    public IngredienteViewModel(@NonNull Application application) {
        super(application);
        repository = new IngredienteRepository(application);
    }
    
    // Observar ingredientes
    public LiveData<List<Ingrediente>> getIngredientesPorData(String data) {
        return repository.getIngredientesPorData(data);
    }
    
    public LiveData<List<Ingrediente>> getIngredientesPorDataEStatus(String data, String status) {
        return repository.getIngredientesPorDataEStatus(data, status);
    }
    
    public LiveData<List<Ingrediente>> getIngredientesPendentes() {
        return repository.getIngredientesPendentes();
    }
    
    public LiveData<Integer> getCountIngredientesFaltando(String data) {
        return repository.getCountIngredientesFaltando(data);
    }
    
    public LiveData<List<Ingrediente>> getIngredientesPorPeriodo(String dataInicio, String dataFim) {
        return repository.getIngredientesPorPeriodo(dataInicio, dataFim);
    }
    
    // Operações
    public void inserirIngrediente(Ingrediente ingrediente) {
        repository.insert(ingrediente);
    }
    
    public void atualizarIngrediente(Ingrediente ingrediente) {
        repository.update(ingrediente);
    }
    
    public void deletarIngrediente(Ingrediente ingrediente) {
        repository.delete(ingrediente);
    }
    
    public void marcarComoDisponivel(Ingrediente ingrediente) {
        ingrediente.setStatus("DISPONIVEL");
        ingrediente.setTimestampVerificacao(System.currentTimeMillis());
        repository.update(ingrediente);
    }
    
    public void marcarComoFaltando(Ingrediente ingrediente) {
        ingrediente.setStatus("FALTANDO");
        ingrediente.setTimestampVerificacao(System.currentTimeMillis());
        repository.update(ingrediente);
    }
    
    public void marcarComoParcial(Ingrediente ingrediente) {
        ingrediente.setStatus("PARCIAL");
        ingrediente.setTimestampVerificacao(System.currentTimeMillis());
        repository.update(ingrediente);
    }
    
    public void atualizarQuantidade(Ingrediente ingrediente, String novaQuantidade, String responsavel) {
        ingrediente.setQuantidade(novaQuantidade);
        ingrediente.setResponsavelVerificacao(responsavel);
        ingrediente.setTimestampVerificacao(System.currentTimeMillis());
        repository.update(ingrediente);
    }
    
    public void deleteIngredientesPorData(String data) {
        repository.deleteIngredientesPorData(data);
    }
}
