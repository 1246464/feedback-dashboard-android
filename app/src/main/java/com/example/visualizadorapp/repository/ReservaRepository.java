package com.example.visualizadorapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.visualizadorapp.database.AppDatabase;
import com.example.visualizadorapp.database.ReservaDao;
import com.example.visualizadorapp.model.Reserva;
import com.google.firebase.database.*;
import java.util.List;

public class ReservaRepository {
    private ReservaDao reservaDao;
    private DatabaseReference firebaseRef;
    
    public ReservaRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        reservaDao = database.reservaDao();
        firebaseRef = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/")
                .getReference("reservas");
    }
    
    // LiveData operations
    public LiveData<List<Reserva>> getAllReservas() {
        syncFromFirebase();
        return reservaDao.getAllReservas();
    }
    
    public LiveData<List<Reserva>> getReservasByUser(String userId) {
        return reservaDao.getReservasByUser(userId);
    }
    
    public LiveData<List<Reserva>> getReservasByData(String data) {
        return reservaDao.getReservasByData(data);
    }
    
    public LiveData<Reserva> getReservaAtiva(String data, String userId) {
        return reservaDao.getReservaAtiva(data, userId);
    }
    
    public LiveData<List<Reserva>> getReservasByStatus(String status) {
        return reservaDao.getReservasByStatus(status);
    }
    
    public LiveData<Integer> getCountReservasAtivasByData(String data) {
        return reservaDao.getCountReservasAtivasByData(data);
    }
    
    // Insert/Update/Delete operations
    public void insert(Reserva reserva, OnSuccessListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = reservaDao.insert(reserva);
            reserva.setId((int) id);
            // Sync to Firebase
            firebaseRef.child(String.valueOf(id)).setValue(reserva)
                    .addOnSuccessListener(aVoid -> {
                        if (listener != null) listener.onSuccess();
                    });
        });
    }
    
    public void update(Reserva reserva) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            reservaDao.update(reserva);
            // Sync to Firebase
            firebaseRef.child(String.valueOf(reserva.getId())).setValue(reserva);
        });
    }
    
    public void delete(Reserva reserva) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            reservaDao.delete(reserva);
            // Delete from Firebase
            firebaseRef.child(String.valueOf(reserva.getId())).removeValue();
        });
    }
    
    public void updateStatus(int id, String status) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            reservaDao.updateStatus(id, status);
            // Sync to Firebase
            firebaseRef.child(String.valueOf(id)).child("statusReserva").setValue(status);
        });
    }
    
    // Sync operations
    private void syncFromFirebase() {
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Reserva reserva = child.getValue(Reserva.class);
                        if (reserva != null) {
                            reservaDao.insert(reserva);
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
    
    public void deleteOlderThan(String data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            reservaDao.deleteOlderThan(data);
        });
    }
    
    public interface OnSuccessListener {
        void onSuccess();
    }
}
