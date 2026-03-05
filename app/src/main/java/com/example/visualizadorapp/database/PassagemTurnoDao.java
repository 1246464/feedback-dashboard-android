package com.example.visualizadorapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.visualizadorapp.model.PassagemTurno;

import java.util.List;

@Dao
public interface PassagemTurnoDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PassagemTurno passagem);
    
    @Update
    void update(PassagemTurno passagem);
    
    @Delete
    void delete(PassagemTurno passagem);
    
    @Query("SELECT * FROM passagem_turno WHERE data = :data ORDER BY timestamp DESC")
    LiveData<List<PassagemTurno>> getPassagensPorData(String data);
    
    @Query("SELECT * FROM passagem_turno WHERE data = :data AND turnoDestino = :turno ORDER BY timestamp DESC")
    LiveData<List<PassagemTurno>> getPassagensPorDataETurno(String data, String turno);
    
    @Query("SELECT * FROM passagem_turno WHERE turnoDestino = :turno AND lida = 0 ORDER BY timestamp DESC")
    LiveData<List<PassagemTurno>> getPassagensNaoLidasPorTurno(String turno);
    
    @Query("SELECT COUNT(*) FROM passagem_turno WHERE turnoDestino = :turno AND lida = 0")
    LiveData<Integer> getCountPassagensNaoLidas(String turno);
    
    @Query("SELECT * FROM passagem_turno WHERE tipoMensagem = 'URGENTE' AND lida = 0")
    LiveData<List<PassagemTurno>> getPassagensUrgentes();
    
    @Query("UPDATE passagem_turno SET lida = 1 WHERE id = :id")
    void marcarComoLida(int id);
    
    @Query("UPDATE passagem_turno SET lida = 1 WHERE turnoDestino = :turno")
    void marcarTodasComoLidas(String turno);
    
    @Query("DELETE FROM passagem_turno WHERE data < :dataLimite")
    void deletePassagensAntigas(String dataLimite);
    
    @Query("SELECT * FROM passagem_turno WHERE data BETWEEN :dataInicio AND :dataFim ORDER BY timestamp DESC")
    LiveData<List<PassagemTurno>> getPassagensPorPeriodo(String dataInicio, String dataFim);
}
