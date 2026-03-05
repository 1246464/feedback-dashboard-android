package com.example.visualizadorapp.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.visualizadorapp.model.Cardapio;
import com.example.visualizadorapp.model.Reserva;
import com.example.visualizadorapp.model.Comentario;
import com.example.visualizadorapp.model.Ingrediente;
import com.example.visualizadorapp.model.TarefaPreparo;
import com.example.visualizadorapp.model.PassagemTurno;
import com.example.visualizadorapp.model.MudancaCardapio;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
    Cardapio.class, 
    Reserva.class, 
    Comentario.class,
    Ingrediente.class,
    TarefaPreparo.class,
    PassagemTurno.class,
    MudancaCardapio.class
}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract CardapioDao cardapioDao();
    public abstract ReservaDao reservaDao();
    public abstract ComentarioDao comentarioDao();
    public abstract IngredienteDao ingredienteDao();
    public abstract TarefaPreparoDao tarefaPreparoDao();
    public abstract PassagemTurnoDao passagemTurnoDao();
    public abstract MudancaCardapioDao mudancaCardapioDao();
    
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "visualizador_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
