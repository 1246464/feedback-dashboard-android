package com.example.visualizadorapp.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.visualizadorapp.database.AppDatabase;
import com.example.visualizadorapp.database.CardapioTurnoDao;
import com.example.visualizadorapp.model.CardapioTurno;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositório para gerenciar dados de Cardápio por Turno
 * Sincroniza dados do Firebase com o banco local Room
 */
public class CardapioTurnoRepository {
    private static final String TAG = "CardapioTurnoRepository";
    private final CardapioTurnoDao cardapioTurnoDao;
    private final DatabaseReference firebaseDb;
    private final ExecutorService executorService;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CardapioTurnoRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.cardapioTurnoDao = db.cardapioTurnoDao();
        this.firebaseDb = FirebaseDatabase.getInstance().getReference("cardapios_turno");
        this.executorService = Executors.newFixedThreadPool(2);
    }

    // ===== OPERAÇÕES LOCAIS =====

    /**
     * Busca cardápio de um turno específico em uma data
     */
    public CardapioTurno buscarPorDataETurno(String data, String turno) {
        try {
            return cardapioTurnoDao.buscarPorDataETurno(data, turno);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar cardápio: " + data + "_" + turno, e);
            errorMessage.postValue("Erro ao buscar cardápio: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca todos os cardápios de um turno
     */
    public LiveData<List<CardapioTurno>> buscarPorTurnoAsync(String turno) {
        MutableLiveData<List<CardapioTurno>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                List<CardapioTurno> cardapios = cardapioTurnoDao.buscarPorTurno(turno);
                liveData.postValue(cardapios);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar cardápios do turno: " + turno, e);
                errorMessage.postValue("Erro ao carregar cardápios: " + e.getMessage());
            }
        });
        return liveData;
    }

    /**
     * Busca cardápios de um período específico para um turno
     */
    public LiveData<List<CardapioTurno>> buscarPorPeriodoETurnoAsync(String dataInicio, String dataFim, String turno) {
        MutableLiveData<List<CardapioTurno>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                List<CardapioTurno> cardapios = cardapioTurnoDao.buscarPorPeriodoETurno(dataInicio, dataFim, turno);
                liveData.postValue(cardapios);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar cardápios por período", e);
                errorMessage.postValue("Erro ao carregar cardápios: " + e.getMessage());
            }
        });
        return liveData;
    }

    /**
     * Busca cardápios de um período específico (todos os turnos)
     */
    public LiveData<List<CardapioTurno>> buscarPorPeriodoAsync(String dataInicio, String dataFim) {
        MutableLiveData<List<CardapioTurno>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                List<CardapioTurno> cardapios = cardapioTurnoDao.buscarPorPeriodo(dataInicio, dataFim);
                liveData.postValue(cardapios);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar cardápios por período", e);
                errorMessage.postValue("Erro ao carregar cardápios: " + e.getMessage());
            }
        });
        return liveData;
    }

    /**
     * Busca cardápios de uma data específica (todos os turnos)
     */
    public LiveData<List<CardapioTurno>> buscarPorDataAsync(String data) {
        MutableLiveData<List<CardapioTurno>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                List<CardapioTurno> cardapios = cardapioTurnoDao.buscarPorData(data);
                liveData.postValue(cardapios);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar cardápios da data: " + data, e);
                errorMessage.postValue("Erro ao carregar cardápios: " + e.getMessage());
            }
        });
        return liveData;
    }

    /**
     * Busca próximos cardápios a partir de uma data
     */
    public LiveData<List<CardapioTurno>> buscarProximosAsync(String data) {
        MutableLiveData<List<CardapioTurno>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                List<CardapioTurno> cardapios = cardapioTurnoDao.buscarProximos(data);
                liveData.postValue(cardapios);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar próximos cardápios", e);
                errorMessage.postValue("Erro ao carregar cardápios: " + e.getMessage());
            }
        });
        return liveData;
    }

    /**
     * Busca cardápios favoritos
     */
    public LiveData<List<CardapioTurno>> buscarFavoritosAsync() {
        MutableLiveData<List<CardapioTurno>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                List<CardapioTurno> cardapios = cardapioTurnoDao.buscarFavoritos();
                liveData.postValue(cardapios);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar favoritos", e);
                errorMessage.postValue("Erro ao carregar favoritos: " + e.getMessage());
            }
        });
        return liveData;
    }

    /**
     * Busca cardápios favoritos de um turno
     */
    public LiveData<List<CardapioTurno>> buscarFavoritosPorTurnoAsync(String turno) {
        MutableLiveData<List<CardapioTurno>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                List<CardapioTurno> cardapios = cardapioTurnoDao.buscarFavoritosPorTurno(turno);
                liveData.postValue(cardapios);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar favoritos do turno: " + turno, e);
                errorMessage.postValue("Erro ao carregar favoritos: " + e.getMessage());
            }
        });
        return liveData;
    }

    /**
     * Insere um novo cardápio localmente
     */
    public void inserirLocal(CardapioTurno cardapioTurno) {
        executorService.execute(() -> {
            try {
                cardapioTurnoDao.inserir(cardapioTurno);
                Log.d(TAG, "Cardápio inserido: " + cardapioTurno.getId());
            } catch (Exception e) {
                Log.e(TAG, "Erro ao inserir cardápio", e);
                errorMessage.postValue("Erro ao salvar cardápio: " + e.getMessage());
            }
        });
    }

    /**
     * Atualiza um cardápio como favorito
     */
    public void atualizarFavorito(String id, boolean isFavorito) {
        executorService.execute(() -> {
            try {
                cardapioTurnoDao.atualizarFavorito(id, isFavorito);
                Log.d(TAG, "Cardápio atualizado como favorito: " + id);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao atualizar favorito", e);
                errorMessage.postValue("Erro ao atualizar: " + e.getMessage());
            }
        });
    }

    // ===== OPERAÇÕES FIREBASE =====

    /**
     * Sincroniza cardápios do Firebase com o banco local
     */
    public void sincronizarComFirebase() {
        firebaseDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                executorService.execute(() -> {
                    try {
                        cardapioTurnoDao.deletarTodos();
                        List<CardapioTurno> cardapios = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CardapioTurno cardapio = snapshot.getValue(CardapioTurno.class);
                            if (cardapio != null) {
                                cardapios.add(cardapio);
                            }
                        }
                        if (!cardapios.isEmpty()) {
                            cardapioTurnoDao.inserirTodos(cardapios);
                        }
                        Log.d(TAG, "Sincronização com Firebase concluída");
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao sincronizar com Firebase", e);
                        errorMessage.postValue("Erro ao sincronizar: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Erro ao ouvir Firebase: " + error.getMessage());
                errorMessage.postValue("Erro de conexão: " + error.getMessage());
            }
        });
    }

    /**
     * Salva um cardápio no Firebase
     */
    public void salvarNoFirebase(CardapioTurno cardapioTurno) {
        firebaseDb.child(cardapioTurno.getId()).setValue(cardapioTurno)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cardápio salvo no Firebase: " + cardapioTurno.getId());
                    inserirLocal(cardapioTurno);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao salvar no Firebase", e);
                    errorMessage.postValue("Erro ao salvar: " + e.getMessage());
                });
    }

    /**
     * Obtém a observação de erro
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Limpa os recursos
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
