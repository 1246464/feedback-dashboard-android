package com.example.visualizadorapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.visualizadorapp.database.AppDatabase;
import com.example.visualizadorapp.database.IngredienteDao;
import com.example.visualizadorapp.model.Ingrediente;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class IngredienteRepository {
    private IngredienteDao ingredienteDao;
    private DatabaseReference firebaseRef;
    
    public IngredienteRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        ingredienteDao = db.ingredienteDao();
        firebaseRef = FirebaseDatabase.getInstance().getReference("ingredientes");
        
        syncFromFirebase();
    }
    
    // Sincronizar do Firebase para Room
    private void syncFromFirebase() {
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ingrediente ingrediente = snapshot.getValue(Ingrediente.class);
                    if (ingrediente != null) {
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            ingredienteDao.insert(ingrediente);
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
    public LiveData<List<Ingrediente>> getIngredientesPorData(String data) {
        return ingredienteDao.getIngredientesPorData(data);
    }
    
    public LiveData<List<Ingrediente>> getIngredientesPorDataEStatus(String data, String status) {
        return ingredienteDao.getIngredientesPorDataEStatus(data, status);
    }
    
    public LiveData<List<Ingrediente>> getIngredientesPendentes() {
        return ingredienteDao.getIngredientesPendentes();
    }
    
    public LiveData<Integer> getCountIngredientesFaltando(String data) {
        return ingredienteDao.getCountIngredientesFaltando(data);
    }
    
    public LiveData<List<Ingrediente>> getIngredientesPorPeriodo(String dataInicio, String dataFim) {
        return ingredienteDao.getIngredientesPorPeriodo(dataInicio, dataFim);
    }
    
    // Inserir com sincronia Firebase
    public void insert(Ingrediente ingrediente) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ingredienteDao.insert(ingrediente);
            firebaseRef.child(String.valueOf(ingrediente.getId())).setValue(ingrediente);
        });
    }
    
    // Atualizar com sincronia Firebase
    public void update(Ingrediente ingrediente) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ingredienteDao.update(ingrediente);
            firebaseRef.child(String.valueOf(ingrediente.getId())).setValue(ingrediente);
        });
    }
    
    // Deletar com sincronia Firebase
    public void delete(Ingrediente ingrediente) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ingredienteDao.delete(ingrediente);
            firebaseRef.child(String.valueOf(ingrediente.getId())).removeValue();
        });
    }
    
    public void deleteIngredientesPorData(String data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ingredienteDao.deleteIngredientesPorData(data);
        });
    }
}
