package com.example.visualizadorapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.visualizadorapp.database.AppDatabase;
import com.example.visualizadorapp.database.PassagemTurnoDao;
import com.example.visualizadorapp.model.PassagemTurno;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PassagemTurnoRepository {
    private PassagemTurnoDao passagemDao;
    private DatabaseReference firebaseRef;
    
    public PassagemTurnoRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        passagemDao = db.passagemTurnoDao();
        firebaseRef = FirebaseDatabase.getInstance().getReference("passagem_turno");
        
        syncFromFirebase();
    }
    
    private void syncFromFirebase() {
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PassagemTurno passagem = snapshot.getValue(PassagemTurno.class);
                    if (passagem != null) {
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            passagemDao.insert(passagem);
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
    public LiveData<List<PassagemTurno>> getPassagensPorData(String data) {
        return passagemDao.getPassagensPorData(data);
    }
    
    public LiveData<List<PassagemTurno>> getPassagensPorDataETurno(String data, String turno) {
        return passagemDao.getPassagensPorDataETurno(data, turno);
    }
    
    public LiveData<List<PassagemTurno>> getPassagensNaoLidasPorTurno(String turno) {
        return passagemDao.getPassagensNaoLidasPorTurno(turno);
    }
    
    public LiveData<Integer> getCountPassagensNaoLidas(String turno) {
        return passagemDao.getCountPassagensNaoLidas(turno);
    }
    
    public LiveData<List<PassagemTurno>> getPassagensUrgentes() {
        return passagemDao.getPassagensUrgentes();
    }
    
    public LiveData<List<PassagemTurno>> getPassagensPorPeriodo(String dataInicio, String dataFim) {
        return passagemDao.getPassagensPorPeriodo(dataInicio, dataFim);
    }
    
    public void marcarComoLida(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            passagemDao.marcarComoLida(id);
        });
    }
    
    public void marcarTodasComoLidas(String turno) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            passagemDao.marcarTodasComoLidas(turno);
        });
    }
    
    // Inserir com sincronia Firebase
    public void insert(PassagemTurno passagem) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            passagemDao.insert(passagem);
            firebaseRef.child(String.valueOf(passagem.getId())).setValue(passagem);
        });
    }
    
    // Atualizar com sincronia Firebase
    public void update(PassagemTurno passagem) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            passagemDao.update(passagem);
            firebaseRef.child(String.valueOf(passagem.getId())).setValue(passagem);
        });
    }
    
    // Deletar com sincronia Firebase
    public void delete(PassagemTurno passagem) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            passagemDao.delete(passagem);
            firebaseRef.child(String.valueOf(passagem.getId())).removeValue();
        });
    }
    
    public void deletePassagensAntigas(String dataLimite) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            passagemDao.deletePassagensAntigas(dataLimite);
        });
    }
}
