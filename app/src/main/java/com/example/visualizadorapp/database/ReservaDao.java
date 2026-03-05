package com.example.visualizadorapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.visualizadorapp.model.Reserva;
import java.util.List;

@Dao
public interface ReservaDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Reserva reserva);
    
    @Update
    void update(Reserva reserva);
    
    @Delete
    void delete(Reserva reserva);
    
    @Query("DELETE FROM reservas")
    void deleteAll();
    
    @Query("SELECT * FROM reservas ORDER BY timestampReserva DESC")
    LiveData<List<Reserva>> getAllReservas();
    
    @Query("SELECT * FROM reservas WHERE userId = :userId ORDER BY timestampReserva DESC")
    LiveData<List<Reserva>> getReservasByUser(String userId);
    
    @Query("SELECT * FROM reservas WHERE data = :data ORDER BY timestampReserva DESC")
    LiveData<List<Reserva>> getReservasByData(String data);
    
    @Query("SELECT * FROM reservas WHERE data = :data AND userId = :userId AND statusReserva = 'ATIVA' LIMIT 1")
    LiveData<Reserva> getReservaAtiva(String data, String userId);
    
    @Query("SELECT * FROM reservas WHERE data = :data AND userId = :userId AND statusReserva = 'ATIVA' LIMIT 1")
    Reserva getReservaAtivaSync(String data, String userId);
    
    @Query("SELECT * FROM reservas WHERE statusReserva = :status ORDER BY timestampReserva DESC")
    LiveData<List<Reserva>> getReservasByStatus(String status);
    
    @Query("UPDATE reservas SET statusReserva = :status WHERE id = :id")
    void updateStatus(int id, String status);
    
    @Query("SELECT COUNT(*) FROM reservas WHERE data = :data AND statusReserva = 'ATIVA'")
    LiveData<Integer> getCountReservasAtivasByData(String data);
    
    @Query("DELETE FROM reservas WHERE data < :data")
    void deleteOlderThan(String data);
}
