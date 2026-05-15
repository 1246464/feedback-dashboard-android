package com.example.visualizadorapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.visualizadorapp.database.AppDatabase;
import com.example.visualizadorapp.database.CardapioDao;
import com.example.visualizadorapp.model.Cardapio;
import com.google.firebase.database.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardapioRepository {
    private CardapioDao cardapioDao;
    private DatabaseReference firebaseRef;
    private ExecutorService executorService;
    
    public CardapioRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        cardapioDao = database.cardapioDao();
        firebaseRef = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/")
                .getReference("cardapios");
        executorService = Executors.newSingleThreadExecutor();
    }
    
    // LiveData operations
    public LiveData<List<Cardapio>> getAllCardapios() {
        syncFromFirebase();
        return cardapioDao.getAllCardapios();
    }
    
    public LiveData<Cardapio> getCardapioByData(String data) {
        // Sincronizar do Firebase com timeout
        syncCardapioFromFirebaseWithTimeout(data, 3000); // 3 segundos de timeout
        return cardapioDao.getCardapioByData(data);
    }
    
    /**
     * Sincroniza um cardápio do Firebase com timeout.
     * Se não encontrar em 3s, continua mesmo assim (pode sincronizar depois).
     */
    private void syncCardapioFromFirebaseWithTimeout(String data, long timeoutMs) {
        firebaseRef.child(data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        Cardapio cardapio = snapshot.getValue(Cardapio.class);
                        if (cardapio != null) {
                            cardapio.setData(data);
                            // Execute insert em background thread
                            AppDatabase.databaseWriteExecutor.execute(() -> {
                                try {
                                    cardapioDao.insert(cardapio);
                                    android.util.Log.d("CardapioRepository", 
                                        "Cardápio " + data + " sincronizado com sucesso");
                                } catch (Exception e) {
                                    android.util.Log.e("CardapioRepository", 
                                        "Erro ao inserir cardápio " + data + ": " + e.getMessage());
                                }
                            });
                        }
                    } catch (Exception e) {
                        android.util.Log.e("CardapioRepository", 
                            "Erro ao processar cardápio " + data + ": " + e.getMessage());
                    }
                } else {
                    android.util.Log.d("CardapioRepository", 
                        "Nenhum cardápio encontrado no Firebase para: " + data);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                android.util.Log.e("CardapioRepository", 
                    "Erro ao sincronizar cardápio " + data + ": " + error.getMessage());
            }
        });
    }
    
    public LiveData<List<Cardapio>> getFavoritos() {
        return cardapioDao.getFavoritos();
    }
    
    public LiveData<List<Cardapio>> searchCardapios(String query) {
        return cardapioDao.searchCardapios(query);
    }
    
    public LiveData<List<Cardapio>> getCardapiosByPeriodo(String dataInicio, String dataFim) {
        return cardapioDao.getCardapiosByPeriodo(dataInicio, dataFim);
    }
    
    // Insert/Update/Delete operations
    public void insert(Cardapio cardapio) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cardapioDao.insert(cardapio);
            // Sync to Firebase
            firebaseRef.child(cardapio.getData()).setValue(cardapio);
        });
    }
    
    public void update(Cardapio cardapio) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cardapioDao.update(cardapio);
            // Sync to Firebase
            firebaseRef.child(cardapio.getData()).setValue(cardapio);
        });
    }
    
    public void delete(Cardapio cardapio) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cardapioDao.delete(cardapio);
            // Delete from Firebase
            firebaseRef.child(cardapio.getData()).removeValue();
        });
    }
    
    public void updateFavorito(String data, boolean isFavorito) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cardapioDao.updateFavorito(data, isFavorito);
        });
    }
    
    // Sync operations
    public void syncFromFirebase() {
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Cardapio cardapio = child.getValue(Cardapio.class);
                        if (cardapio != null) {
                            cardapio.setData(child.getKey());
                            cardapioDao.insert(cardapio);
                        }
                    }
                });
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }
    
    private void syncCardapioFromFirebase(String data) {
        firebaseRef.child(data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        Cardapio cardapio = snapshot.getValue(Cardapio.class);
                        if (cardapio != null) {
                            cardapio.setData(data);
                            cardapioDao.insert(cardapio);
                        }
                    });
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }
    
    public void deleteOlderThan(String data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cardapioDao.deleteOlderThan(data);
        });
    }
}
