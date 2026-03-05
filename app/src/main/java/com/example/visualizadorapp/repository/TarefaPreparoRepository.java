package com.example.visualizadorapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.visualizadorapp.database.AppDatabase;
import com.example.visualizadorapp.database.TarefaPreparoDao;
import com.example.visualizadorapp.model.TarefaPreparo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TarefaPreparoRepository {
    private TarefaPreparoDao tarefaDao;
    private DatabaseReference firebaseRef;
    
    public TarefaPreparoRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        tarefaDao = db.tarefaPreparoDao();
        firebaseRef = FirebaseDatabase.getInstance().getReference("tarefas_preparo");
        
        syncFromFirebase();
    }
    
    private void syncFromFirebase() {
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TarefaPreparo tarefa = snapshot.getValue(TarefaPreparo.class);
                    if (tarefa != null) {
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            tarefaDao.insert(tarefa);
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
    public LiveData<List<TarefaPreparo>> getTarefasPorData(String data) {
        return tarefaDao.getTarefasPorData(data);
    }
    
    public LiveData<List<TarefaPreparo>> getTarefasPorDataEStatus(String data, String status) {
        return tarefaDao.getTarefasPorDataEStatus(data, status);
    }
    
    public LiveData<List<TarefaPreparo>> getTarefasPorDataETurno(String data, String turno) {
        return tarefaDao.getTarefasPorDataETurno(data, turno);
    }
    
    public LiveData<List<TarefaPreparo>> getTarefasPendentes() {
        return tarefaDao.getTarefasPendentes();
    }
    
    public LiveData<Integer> getCountTarefasPendentes(String data) {
        return tarefaDao.getCountTarefasPendentes(data);
    }
    
    public LiveData<Integer> getCountTarefasConcluidas(String data) {
        return tarefaDao.getCountTarefasConcluidas(data);
    }
    
    public LiveData<List<TarefaPreparo>> getTarefasPorResponsavel(String nomeResponsavel) {
        return tarefaDao.getTarefasPorResponsavel(nomeResponsavel);
    }
    
    // Inserir com sincronia Firebase
    public void insert(TarefaPreparo tarefa) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            tarefaDao.insert(tarefa);
            firebaseRef.child(String.valueOf(tarefa.getId())).setValue(tarefa);
        });
    }
    
    // Atualizar com sincronia Firebase
    public void update(TarefaPreparo tarefa) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            tarefaDao.update(tarefa);
            firebaseRef.child(String.valueOf(tarefa.getId())).setValue(tarefa);
        });
    }
    
    // Deletar com sincronia Firebase
    public void delete(TarefaPreparo tarefa) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            tarefaDao.delete(tarefa);
            firebaseRef.child(String.valueOf(tarefa.getId())).removeValue();
        });
    }
    
    public void deleteTarefasPorData(String data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            tarefaDao.deleteTarefasPorData(data);
        });
    }
}
