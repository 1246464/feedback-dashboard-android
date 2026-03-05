package com.example.visualizadorapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.visualizadorapp.model.Ingrediente;

import java.util.List;

@Dao
public interface IngredienteDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Ingrediente ingrediente);
    
    @Update
    void update(Ingrediente ingrediente);
    
    @Delete
    void delete(Ingrediente ingrediente);
    
    @Query("SELECT * FROM ingredientes WHERE dataCardapio = :data ORDER BY nomeIngrediente ASC")
    LiveData<List<Ingrediente>> getIngredientesPorData(String data);
    
    @Query("SELECT * FROM ingredientes WHERE dataCardapio = :data AND status = :status")
    LiveData<List<Ingrediente>> getIngredientesPorDataEStatus(String data, String status);
    
    @Query("SELECT * FROM ingredientes WHERE status = 'FALTANDO' OR status = 'PARCIAL'")
    LiveData<List<Ingrediente>> getIngredientesPendentes();
    
    @Query("DELETE FROM ingredientes WHERE dataCardapio = :data")
    void deleteIngredientesPorData(String data);
    
    @Query("SELECT COUNT(*) FROM ingredientes WHERE dataCardapio = :data AND status = 'FALTANDO'")
    LiveData<Integer> getCountIngredientesFaltando(String data);
    
    @Query("SELECT * FROM ingredientes WHERE dataCardapio BETWEEN :dataInicio AND :dataFim")
    LiveData<List<Ingrediente>> getIngredientesPorPeriodo(String dataInicio, String dataFim);
}
