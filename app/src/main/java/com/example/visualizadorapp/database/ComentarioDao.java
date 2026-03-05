package com.example.visualizadorapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.visualizadorapp.model.Comentario;
import java.util.List;

@Dao
public interface ComentarioDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Comentario comentario);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Comentario> comentarios);
    
    @Update
    void update(Comentario comentario);
    
    @Delete
    void delete(Comentario comentario);
    
    @Query("DELETE FROM comentarios")
    void deleteAll();
    
    @Query("SELECT * FROM comentarios ORDER BY timestamp DESC")
    LiveData<List<Comentario>> getAllComentarios();
    
    @Query("SELECT * FROM comentarios WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<Comentario>> getComentariosByUser(String userId);
    
    @Query("SELECT * FROM comentarios WHERE data = :data ORDER BY timestamp DESC")
    LiveData<List<Comentario>> getComentariosByData(String data);
    
    @Query("SELECT * FROM comentarios WHERE avaliacao >= :minRating ORDER BY timestamp DESC")
    LiveData<List<Comentario>> getComentariosByRating(float minRating);
    
    @Query("SELECT AVG(avaliacao) FROM comentarios WHERE data = :data")
    LiveData<Float> getAverageRatingByData(String data);
    
    @Query("SELECT COUNT(*) FROM comentarios WHERE data = :data")
    LiveData<Integer> getCountComentariosByData(String data);
    
    @Query("DELETE FROM comentarios WHERE timestamp < :timestamp")
    void deleteOlderThan(long timestamp);
}
