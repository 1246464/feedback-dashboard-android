package com.example.visualizadorapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.visualizadorapp.model.PassagemTurno;
import com.example.visualizadorapp.repository.PassagemTurnoRepository;

import java.util.List;

public class PassagemTurnoViewModel extends AndroidViewModel {
    private PassagemTurnoRepository repository;
    
    public PassagemTurnoViewModel(@NonNull Application application) {
        super(application);
        repository = new PassagemTurnoRepository(application);
    }
    
    // Observar passagens
    public LiveData<List<PassagemTurno>> getPassagensPorData(String data) {
        return repository.getPassagensPorData(data);
    }
    
    public LiveData<List<PassagemTurno>> getPassagensPorDataETurno(String data, String turno) {
        return repository.getPassagensPorDataETurno(data, turno);
    }
    
    public LiveData<List<PassagemTurno>> getPassagensNaoLidasPorTurno(String turno) {
        return repository.getPassagensNaoLidasPorTurno(turno);
    }
    
    public LiveData<Integer> getCountPassagensNaoLidas(String turno) {
        return repository.getCountPassagensNaoLidas(turno);
    }
    
    public LiveData<List<PassagemTurno>> getPassagensUrgentes() {
        return repository.getPassagensUrgentes();
    }
    
    public LiveData<List<PassagemTurno>> getPassagensPorPeriodo(String dataInicio, String dataFim) {
        return repository.getPassagensPorPeriodo(dataInicio, dataFim);
    }
    
    // Operações
    public void inserirPassagem(PassagemTurno passagem) {
        repository.insert(passagem);
    }
    
    public void marcarComoLida(int id) {
        repository.marcarComoLida(id);
    }
    
    public void marcarTodasComoLidas(String turno) {
        repository.marcarTodasComoLidas(turno);
    }
    
    public void deletarPassagem(PassagemTurno passagem) {
        repository.delete(passagem);
    }
}
