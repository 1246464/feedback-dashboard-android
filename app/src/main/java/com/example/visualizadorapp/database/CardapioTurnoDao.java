package com.example.visualizadorapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.visualizadorapp.model.CardapioTurno;

import java.util.List;

/**
 * DAO para operações de CardapioTurno no banco de dados local Room
 */
@Dao
public interface CardapioTurnoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long inserir(CardapioTurno cardapioTurno);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<CardapioTurno> cardapiosTurno);

    @Update
    void atualizar(CardapioTurno cardapioTurno);

    @Delete
    void deletar(CardapioTurno cardapioTurno);

    @Query("SELECT * FROM cardapio_turno WHERE id = :id LIMIT 1")
    CardapioTurno buscarPorId(String id);

    @Query("SELECT * FROM cardapio_turno WHERE data = :data AND turno = :turno LIMIT 1")
    CardapioTurno buscarPorDataETurno(String data, String turno);

    @Query("SELECT * FROM cardapio_turno WHERE turno = :turno ORDER BY data DESC")
    List<CardapioTurno> buscarPorTurno(String turno);

    @Query("SELECT * FROM cardapio_turno WHERE data = :data ORDER BY turno ASC")
    List<CardapioTurno> buscarPorData(String data);

    @Query("SELECT * FROM cardapio_turno WHERE data BETWEEN :dataInicio AND :dataFim AND turno = :turno ORDER BY data ASC")
    List<CardapioTurno> buscarPorPeriodoETurno(String dataInicio, String dataFim, String turno);

    @Query("SELECT * FROM cardapio_turno WHERE data BETWEEN :dataInicio AND :dataFim ORDER BY data ASC, turno ASC")
    List<CardapioTurno> buscarPorPeriodo(String dataInicio, String dataFim);

    @Query("SELECT * FROM cardapio_turno WHERE data >= :data ORDER BY data ASC, turno ASC")
    List<CardapioTurno> buscarProximos(String data);

    @Query("SELECT * FROM cardapio_turno WHERE isFavorito = 1 ORDER BY data DESC")
    List<CardapioTurno> buscarFavoritos();

    @Query("SELECT * FROM cardapio_turno WHERE isFavorito = 1 AND turno = :turno ORDER BY data DESC")
    List<CardapioTurno> buscarFavoritosPorTurno(String turno);

    @Query("SELECT DISTINCT turno FROM cardapio_turno ORDER BY turno")
    List<String> buscarTurnosComCardapio();

    @Query("SELECT COUNT(*) FROM cardapio_turno WHERE data = :data AND turno = :turno")
    int contarPorDataETurno(String data, String turno);

    @Query("SELECT COUNT(*) FROM cardapio_turno WHERE isFavorito = 1")
    int contarFavoritos();

    @Query("DELETE FROM cardapio_turno WHERE data < :data")
    void deletarAntigos(String data);

    @Query("DELETE FROM cardapio_turno")
    void deletarTodos();

    @Query("UPDATE cardapio_turno SET isFavorito = :isFavorito WHERE id = :id")
    void atualizarFavorito(String id, boolean isFavorito);
}
