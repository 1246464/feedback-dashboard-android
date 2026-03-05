package com.example.visualizadorapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.visualizadorapp.model.TarefaPreparo;

import java.util.List;

@Dao
public interface TarefaPreparoDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TarefaPreparo tarefa);
    
    @Update
    void update(TarefaPreparo tarefa);
    
    @Delete
    void delete(TarefaPreparo tarefa);
    
    @Query("SELECT * FROM tarefas_preparo WHERE dataCardapio = :data ORDER BY prioridade DESC, timestampInicio ASC")
    LiveData<List<TarefaPreparo>> getTarefasPorData(String data);
    
    @Query("SELECT * FROM tarefas_preparo WHERE dataCardapio = :data AND status = :status")
    LiveData<List<TarefaPreparo>> getTarefasPorDataEStatus(String data, String status);
    
    @Query("SELECT * FROM tarefas_preparo WHERE dataCardapio = :data AND turnoResponsavel = :turno")
    LiveData<List<TarefaPreparo>> getTarefasPorDataETurno(String data, String turno);
    
    @Query("SELECT * FROM tarefas_preparo WHERE status != 'CONCLUIDA' ORDER BY prioridade DESC, dataCardapio ASC")
    LiveData<List<TarefaPreparo>> getTarefasPendentes();
    
    @Query("SELECT COUNT(*) FROM tarefas_preparo WHERE dataCardapio = :data AND status = 'PENDENTE'")
    LiveData<Integer> getCountTarefasPendentes(String data);
    
    @Query("SELECT COUNT(*) FROM tarefas_preparo WHERE dataCardapio = :data AND status = 'CONCLUIDA'")
    LiveData<Integer> getCountTarefasConcluidas(String data);
    
    @Query("DELETE FROM tarefas_preparo WHERE dataCardapio = :data")
    void deleteTarefasPorData(String data);
    
    @Query("SELECT * FROM tarefas_preparo WHERE responsavel = :nomeResponsavel AND status != 'CONCLUIDA'")
    LiveData<List<TarefaPreparo>> getTarefasPorResponsavel(String nomeResponsavel);
}
