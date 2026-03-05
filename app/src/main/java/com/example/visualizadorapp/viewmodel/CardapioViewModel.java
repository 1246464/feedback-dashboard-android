package com.example.visualizadorapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.visualizadorapp.model.Cardapio;
import com.example.visualizadorapp.repository.CardapioRepository;
import java.util.List;

public class CardapioViewModel extends AndroidViewModel {
    private CardapioRepository repository;
    private LiveData<List<Cardapio>> allCardapios;
    
    public CardapioViewModel(@NonNull Application application) {
        super(application);
        repository = new CardapioRepository(application);
        allCardapios = repository.getAllCardapios();
    }
    
    public LiveData<List<Cardapio>> getAllCardapios() {
        return allCardapios;
    }
    
    public LiveData<Cardapio> getCardapioByData(String data) {
        return repository.getCardapioByData(data);
    }
    
    public LiveData<List<Cardapio>> getFavoritos() {
        return repository.getFavoritos();
    }
    
    public LiveData<List<Cardapio>> searchCardapios(String query) {
        return repository.searchCardapios(query);
    }
    
    public LiveData<List<Cardapio>> getCardapiosByPeriodo(String dataInicio, String dataFim) {
        return repository.getCardapiosByPeriodo(dataInicio, dataFim);
    }
    
    public void insert(Cardapio cardapio) {
        repository.insert(cardapio);
    }
    
    public void update(Cardapio cardapio) {
        repository.update(cardapio);
    }
    
    public void delete(Cardapio cardapio) {
        repository.delete(cardapio);
    }
    
    public void updateFavorito(String data, boolean isFavorito) {
        repository.updateFavorito(data, isFavorito);
    }
    
    public void deleteOlderThan(String data) {
        repository.deleteOlderThan(data);
    }
}
