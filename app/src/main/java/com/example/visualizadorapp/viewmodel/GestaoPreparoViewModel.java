package com.example.visualizadorapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.visualizadorapp.model.TarefaPreparo;
import com.example.visualizadorapp.repository.TarefaPreparoRepository;

import java.util.List;

public class GestaoPreparoViewModel extends AndroidViewModel {
    private TarefaPreparoRepository repository;
    
    public GestaoPreparoViewModel(@NonNull Application application) {
        super(application);
        repository = new TarefaPreparoRepository(application);
    }
    
    // Observar tarefas
    public LiveData<List<TarefaPreparo>> getTarefasPorData(String data) {
        return repository.getTarefasPorData(data);
    }
    
    public LiveData<List<TarefaPreparo>> getTarefasPorDataEStatus(String data, String status) {
        return repository.getTarefasPorDataEStatus(data, status);
    }
    
    public LiveData<List<TarefaPreparo>> getTarefasPorDataETurno(String data, String turno) {
        return repository.getTarefasPorDataETurno(data, turno);
    }
    
    public LiveData<List<TarefaPreparo>> getTarefasPendentes() {
        return repository.getTarefasPendentes();
    }
    
    public LiveData<Integer> getCountTarefasPendentes(String data) {
        return repository.getCountTarefasPendentes(data);
    }
    
    public LiveData<Integer> getCountTarefasConcluidas(String data) {
        return repository.getCountTarefasConcluidas(data);
    }
    
    public LiveData<List<TarefaPreparo>> getTarefasPorResponsavel(String nomeResponsavel) {
        return repository.getTarefasPorResponsavel(nomeResponsavel);
    }
    
    // Operações
    public void inserirTarefa(TarefaPreparo tarefa) {
        repository.insert(tarefa);
    }
    
    public void atualizarTarefa(TarefaPreparo tarefa) {
        repository.update(tarefa);
    }
    
    public void deletarTarefa(TarefaPreparo tarefa) {
        repository.delete(tarefa);
    }
    
    public void marcarTarefaComoConcluida(TarefaPreparo tarefa) {
        tarefa.setStatus("CONCLUIDA");
        tarefa.setTimestampConclusao(System.currentTimeMillis());
        repository.update(tarefa);
    }
    
    public void iniciarTarefa(TarefaPreparo tarefa, String responsavel) {
        tarefa.setStatus("EM_ANDAMENTO");
        tarefa.setResponsavel(responsavel);
        tarefa.setTimestampInicio(System.currentTimeMillis());
        repository.update(tarefa);
    }
}
