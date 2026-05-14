package com.example.visualizadorapp.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.visualizadorapp.database.AppDatabase;
import com.example.visualizadorapp.database.FuncionarioDao;
import com.example.visualizadorapp.model.Funcionario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositório para gerenciar dados de Funcionários
 * Sincroniza dados do Firebase com o banco local Room
 */
public class FuncionarioRepository {
    private static final String TAG = "FuncionarioRepository";
    private final FuncionarioDao funcionarioDao;
    private final DatabaseReference firebaseDb;
    private final ExecutorService executorService;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public FuncionarioRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.funcionarioDao = db.funcionarioDao();
        this.firebaseDb = FirebaseDatabase.getInstance().getReference("funcionarios");
        this.executorService = Executors.newFixedThreadPool(2);
    }

    // ===== OPERAÇÕES LOCAIS =====

    /**
     * Busca um funcionário pelo email (Síncrono - Utilizado no Cadastro)
     */
    public Funcionario buscarPorEmail(String email) {
        try {
            return funcionarioDao.buscarPorEmail(email);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar por email: " + email, e);
            errorMessage.postValue("Erro ao buscar funcionário: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca um funcionário pelo email de forma assíncrona (Utilizado no ViewModel)
     */
    public LiveData<Funcionario> buscarPorEmailAsync(String email) {
        MutableLiveData<Funcionario> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                Funcionario funcionario = funcionarioDao.buscarPorEmail(email);
                liveData.postValue(funcionario);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar por email async: " + email, e);
                errorMessage.postValue("Erro ao carregar funcionário");
            }
        });
        return liveData;
    }

    /**
     * Busca um funcionário pelo CPF
     */
    public Funcionario buscarPorCPF(String cpf) {
        try {
            return funcionarioDao.buscarPorCPF(cpf);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar por CPF: " + cpf, e);
            return null;
        }
    }

    /**
     * Verifica se um email existe
     */
    public boolean emailJaExiste(String email) {
        try {
            return funcionarioDao.contarPorEmail(email) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ===== OPERAÇÕES FIREBASE =====

    /**
     * Sincroniza funcionários do Firebase com o banco local
     */
    public void sincronizarComFirebase() {
        firebaseDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                executorService.execute(() -> {
                    try {
                        funcionarioDao.deletarTodos();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Funcionario funcionario = snapshot.getValue(Funcionario.class);
                            if (funcionario != null) {
                                funcionarioDao.inserir(funcionario);
                            }
                        }
                        Log.d(TAG, "Sincronização com Firebase concluída");
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao sincronizar com Firebase", e);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Erro Firebase: " + error.getMessage());
            }
        });
    }

    /**
     * Obtém a observação de erro
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Limpa os recursos e encerra as threads
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
