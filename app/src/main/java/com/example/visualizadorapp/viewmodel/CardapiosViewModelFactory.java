package com.example.visualizadorapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.visualizadorapp.repository.CardapioTurnoRepository;
import com.example.visualizadorapp.repository.FuncionarioRepository;

/**
 * Factory para criação do CardapiosViewModel (plural) com repositórios
 */
public class CardapiosViewModelFactory implements ViewModelProvider.Factory {
    private final FuncionarioRepository funcionarioRepository;
    private final CardapioTurnoRepository cardapioRepository;

    public CardapiosViewModelFactory(FuncionarioRepository funcionarioRepository,
                                     CardapioTurnoRepository cardapioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.cardapioRepository = cardapioRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CardapiosViewModel.class)) {
            return (T) new CardapiosViewModel(funcionarioRepository, cardapioRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
