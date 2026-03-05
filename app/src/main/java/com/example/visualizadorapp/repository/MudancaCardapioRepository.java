package com.example.visualizadorapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.visualizadorapp.database.AppDatabase;
import com.example.visualizadorapp.database.MudancaCardapioDao;
import com.example.visualizadorapp.model.MudancaCardapio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MudancaCardapioRepository {
    private MudancaCardapioDao mudancaDao;
    private DatabaseReference firebaseRef;
    
    public MudancaCardapioRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mudancaDao = db.mudancaCardapioDao();
        firebaseRef = FirebaseDatabase.getInstance().getReference("mudancas_cardapio");
        
        syncFromFirebase();
    }
    
    private void syncFromFirebase() {
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MudancaCardapio mudanca = snapshot.getValue(MudancaCardapio.class);
                    if (mudanca != null) {
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            mudancaDao.insert(mudanca);
                        });
                    }
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                // Log error
            }
        });
    }
    
    // Operações locais
    public LiveData<List<MudancaCardapio>> getMudancasPorData(String data) {
        return mudancaDao.getMudancasPorData(data);
    }
    
    public LiveData<List<MudancaCardapio>> getMudancasNaoNotificadas() {
        return mudancaDao.getMudancasNaoNotificadas();
    }
    
    public LiveData<List<MudancaCardapio>> getMudancasPorPeriodo(String dataInicio, String dataFim) {
        return mudancaDao.getMudancasPorPeriodo(dataInicio, dataFim);
    }
    
    public LiveData<Integer> getCountMudancasPorData(String data) {
        return mudancaDao.getCountMudancasPorData(data);
    }
    
    public LiveData<List<MudancaCardapio>> getUltimasMudancas() {
        return mudancaDao.getUltimasMudancas();
    }
    
    public void marcarComoNotificado(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mudancaDao.marcarComoNotificado(id);
        });
    }
    
    // Inserir com sincronia Firebase
    public void insert(MudancaCardapio mudanca) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mudancaDao.insert(mudanca);
            firebaseRef.child(String.valueOf(mudanca.getId())).setValue(mudanca);
        });
    }
    
    // Atualizar com sincronia Firebase
    public void update(MudancaCardapio mudanca) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mudancaDao.update(mudanca);
            firebaseRef.child(String.valueOf(mudanca.getId())).setValue(mudanca);
        });
    }
    
    // Deletar com sincronia Firebase
    public void delete(MudancaCardapio mudanca) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mudancaDao.delete(mudanca);
            firebaseRef.child(String.valueOf(mudanca.getId())).removeValue();
        });
    }
    
    public void deleteMudancasAntigas(String dataLimite) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mudancaDao.deleteMudancasAntigas(dataLimite);
        });
    }
}
