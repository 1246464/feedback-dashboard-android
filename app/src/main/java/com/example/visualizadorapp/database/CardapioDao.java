package com.example.visualizadorapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.visualizadorapp.model.Cardapio;
import java.util.List;

@Dao
public interface CardapioDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Cardapio cardapio);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Cardapio> cardapios);
    
    @Update
    void update(Cardapio cardapio);
    
    @Delete
    void delete(Cardapio cardapio);
    
    @Query("DELETE FROM cardapios")
    void deleteAll();
    
    @Query("SELECT * FROM cardapios ORDER BY data DESC")
    LiveData<List<Cardapio>> getAllCardapios();
    
    @Query("SELECT * FROM cardapios WHERE data = :data LIMIT 1")
    LiveData<Cardapio> getCardapioByData(String data);
    
    @Query("SELECT * FROM cardapios WHERE data = :data LIMIT 1")
    Cardapio getCardapioByDataSync(String data);
    
    @Query("SELECT * FROM cardapios WHERE isFavorito = 1 ORDER BY data DESC")
    LiveData<List<Cardapio>> getFavoritos();
    
    @Query("SELECT * FROM cardapios WHERE " +
           "pratoPrincipal LIKE '%' || :query || '%' OR " +
           "guarnicao LIKE '%' || :query || '%' OR " +
           "acompanhamento LIKE '%' || :query || '%' OR " +
           "salada LIKE '%' || :query || '%' OR " +
           "sobremesa LIKE '%' || :query || '%' " +
           "ORDER BY data DESC")
    LiveData<List<Cardapio>> searchCardapios(String query);
    
    @Query("SELECT * FROM cardapios WHERE data BETWEEN :dataInicio AND :dataFim ORDER BY data DESC")
    LiveData<List<Cardapio>> getCardapiosByPeriodo(String dataInicio, String dataFim);
    
    @Query("UPDATE cardapios SET isFavorito = :isFavorito WHERE data = :data")
    void updateFavorito(String data, boolean isFavorito);
    
    @Query("DELETE FROM cardapios WHERE data < :data")
    void deleteOlderThan(String data);
}
