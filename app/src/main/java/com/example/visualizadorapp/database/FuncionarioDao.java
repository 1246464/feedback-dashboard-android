package com.example.visualizadorapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.visualizadorapp.model.Funcionario;

import java.util.List;

/**
 * DAO para operações de Funcionário no banco de dados local Room
 */
@Dao
public interface FuncionarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long inserir(Funcionario funcionario);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Funcionario> funcionarios);

    @Update
    void atualizar(Funcionario funcionario);

    @Delete
    void deletar(Funcionario funcionario);

    @Query("DELETE FROM funcionarios WHERE cpf = :cpf")
    void deletarPorCPF(String cpf);

    @Query("SELECT * FROM funcionarios WHERE cpf = :cpf LIMIT 1")
    Funcionario buscarPorCPF(String cpf);

    @Query("SELECT * FROM funcionarios WHERE email = :email LIMIT 1")
    Funcionario buscarPorEmail(String email);

    @Query("SELECT * FROM funcionarios WHERE telefone = :telefone LIMIT 1")
    Funcionario buscarPorTelefone(String telefone);

    @Query("SELECT * FROM funcionarios WHERE turno = :turno AND ativo = 1")
    List<Funcionario> buscarPorTurno(String turno);

    @Query("SELECT * FROM funcionarios WHERE setor = :setor AND ativo = 1")
    List<Funcionario> buscarPorSetor(String setor);

    @Query("SELECT * FROM funcionarios WHERE cargo = :cargo AND ativo = 1")
    List<Funcionario> buscarPorCargo(String cargo);

    @Query("SELECT * FROM funcionarios WHERE ativo = 1 ORDER BY nome ASC")
    List<Funcionario> buscarTodosAtivos();

    @Query("SELECT * FROM funcionarios ORDER BY nome ASC")
    List<Funcionario> buscarTodos();

    @Query("SELECT COUNT(*) FROM funcionarios WHERE email = :email AND ativo = 1")
    int contarPorEmail(String email);

    @Query("SELECT COUNT(*) FROM funcionarios WHERE cpf = :cpf AND ativo = 1")
    int contarPorCPF(String cpf);

    @Query("SELECT COUNT(*) FROM funcionarios WHERE ativo = 1")
    int contarAtivos();

    @Query("SELECT DISTINCT turno FROM funcionarios WHERE ativo = 1 ORDER BY turno")
    List<String> buscarTurnosUnicos();

    @Query("SELECT DISTINCT setor FROM funcionarios WHERE ativo = 1 ORDER BY setor")
    List<String> buscarSetoresUnicos();

    @Query("SELECT DISTINCT cargo FROM funcionarios WHERE ativo = 1 ORDER BY cargo")
    List<String> buscarCargosUnicos();

    @Query("DELETE FROM funcionarios")
    void deletarTodos();
}
