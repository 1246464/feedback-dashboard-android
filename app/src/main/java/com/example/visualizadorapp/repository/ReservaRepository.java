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
            try {
                // 1. Salva no banco local (Room) primeiro
                long id = reservaDao.insert(reserva);
                reserva.setId((int) id);

                // 2. Salva no Firebase usando o caminho correto: reservas/DATA/ID
                // O "reserva.getData()" deve retornar algo como "2026-05-08"
                if (reserva.getData() != null) {
                    firebaseRef.child(reserva.getData())
                            .child(String.valueOf(id))
                            .setValue(reserva)
                            .addOnSuccessListener(aVoid -> {
                                if (listener != null) listener.onSuccess();
                            })
                            .addOnFailureListener(e -> {
                                android.util.Log.e("FirebaseError", "Erro ao sincronizar reserva: " + e.getMessage());
                            });
                } else {
                    android.util.Log.e("ReservaRepository", "Data da reserva é nula");
                    if (listener != null) listener.onSuccess();
                }
            } catch (Exception e) {
                android.util.Log.e("ReservaRepository", "Erro ao inserir reserva: " + e.getMessage(), e);
            }
        });
    }
    
    public void update(Reserva reserva) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                reservaDao.update(reserva);
                // Sync to Firebase com caminho correto
                if (reserva.getData() != null) {
                    firebaseRef.child(reserva.getData())
                            .child(String.valueOf(reserva.getId()))
                            .setValue(reserva)
                            .addOnFailureListener(e -> {
                                android.util.Log.e("FirebaseError", "Erro ao atualizar reserva: " + e.getMessage());
                            });
                }
            } catch (Exception e) {
                android.util.Log.e("ReservaRepository", "Erro ao atualizar reserva: " + e.getMessage(), e);
            }
        });
    }
    
    public void delete(Reserva reserva) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                reservaDao.delete(reserva);
                // Delete from Firebase com caminho correto
                if (reserva.getData() != null) {
                    firebaseRef.child(reserva.getData())
                            .child(String.valueOf(reserva.getId()))
                            .removeValue()
                            .addOnFailureListener(e -> {
                                android.util.Log.e("FirebaseError", "Erro ao deletar reserva: " + e.getMessage());
                            });
                }
            } catch (Exception e) {
                android.util.Log.e("ReservaRepository", "Erro ao deletar reserva: " + e.getMessage(), e);
            }
        });
    }
    
    public void updateStatus(int id, String status) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                reservaDao.updateStatus(id, status);
                // Nota: Este método não tem acesso à data. Será necessário passar a data como parâmetro
                // Por enquanto, apenas atualiza no banco local
                android.util.Log.d("ReservaRepository", "Status atualizado localmente para ID: " + id);
            } catch (Exception e) {
                android.util.Log.e("ReservaRepository", "Erro ao atualizar status: " + e.getMessage(), e);
            }
        });
    }
    
    // Sync operations
    private void syncFromFirebase() {
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    try {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            try {
                                // Verificar se o valor é um ArrayList (dados malformados) e ignorar
                                Object rawValue = child.getValue();
                                if (rawValue instanceof java.util.ArrayList) {
                                    android.util.Log.w("ReservaRepository", 
                                        "Ignorando reserva com formato incorreto (ArrayList): " + child.getKey());
                                    continue;
                                }
                                
                                Reserva reserva = child.getValue(Reserva.class);
                                // Validar dados obrigatórios antes de inserir
                                if (reserva != null && reserva.getUserId() != null && 
                                    reserva.getData() != null) {
                                    reservaDao.insert(reserva);
                                } else {
                                    android.util.Log.w("ReservaRepository", 
                                        "Ignorando reserva inválida do Firebase: " + child.getKey());
                                }
                            } catch (Exception e) {
                                android.util.Log.w("ReservaRepository", 
                                    "Erro ao processar reserva do Firebase (continuando): " + e.getMessage());
                                // Continua processando as outras
                            }
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ReservaRepository", 
                            "Erro geral ao sincronizar do Firebase: " + e.getMessage());
                    }
                });
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                android.util.Log.e("ReservaRepository", 
                    "Erro ao sincronizar Firebase: " + error.getMessage());
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
