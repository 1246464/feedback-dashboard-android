package com.example.visualizadorapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.visualizadorapp.model.MudancaCardapio;

import java.util.List;

@Dao
public interface MudancaCardapioDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MudancaCardapio mudanca);
    
    @Update
    void update(MudancaCardapio mudanca);
    
    @Delete
    void delete(MudancaCardapio mudanca);
    
    @Query("SELECT * FROM mudancas_cardapio WHERE data = :data ORDER BY timestamp DESC")
    LiveData<List<MudancaCardapio>> getMudancasPorData(String data);
    
    @Query("SELECT * FROM mudancas_cardapio WHERE notificadoTurnos = 0")
    LiveData<List<MudancaCardapio>> getMudancasNaoNotificadas();
    
    @Query("UPDATE mudancas_cardapio SET notificadoTurnos = 1 WHERE id = :id")
    void marcarComoNotificado(int id);
    
    @Query("SELECT * FROM mudancas_cardapio WHERE data BETWEEN :dataInicio AND :dataFim ORDER BY timestamp DESC")
    LiveData<List<MudancaCardapio>> getMudancasPorPeriodo(String dataInicio, String dataFim);
    
    @Query("SELECT COUNT(*) FROM mudancas_cardapio WHERE data = :data")
    LiveData<Integer> getCountMudancasPorData(String data);
    
    @Query("DELETE FROM mudancas_cardapio WHERE data < :dataLimite")
    void deleteMudancasAntigas(String dataLimite);
    
    @Query("SELECT * FROM mudancas_cardapio ORDER BY timestamp DESC LIMIT 10")
    LiveData<List<MudancaCardapio>> getUltimasMudancas();
}
