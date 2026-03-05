package com.example.visualizadorapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.visualizadorapp.database.AppDatabase;
import com.example.visualizadorapp.database.ComentarioDao;
import com.example.visualizadorapp.model.Comentario;
import com.google.firebase.database.*;
import java.util.List;

public class ComentarioRepository {
    private ComentarioDao comentarioDao;
    private DatabaseReference firebaseRef;
    
    public ComentarioRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        comentarioDao = database.comentarioDao();
        firebaseRef = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/")
                .getReference("comentarios");
    }
    
    // LiveData operations
    public LiveData<List<Comentario>> getAllComentarios() {
        syncFromFirebase();
        return comentarioDao.getAllComentarios();
    }
    
    public LiveData<List<Comentario>> getComentariosByUser(String userId) {
        return comentarioDao.getComentariosByUser(userId);
    }
    
    public LiveData<List<Comentario>> getComentariosByData(String data) {
        return comentarioDao.getComentariosByData(data);
    }
    
    public LiveData<List<Comentario>> getComentariosByRating(float minRating) {
        return comentarioDao.getComentariosByRating(minRating);
    }
    
    public LiveData<Float> getAverageRatingByData(String data) {
        return comentarioDao.getAverageRatingByData(data);
    }
    
    public LiveData<Integer> getCountComentariosByData(String data) {
        return comentarioDao.getCountComentariosByData(data);
    }
    
    // Insert/Update/Delete operations
    public void insert(Comentario comentario) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            comentarioDao.insert(comentario);
            // Sync to Firebase
            firebaseRef.child(comentario.getId()).setValue(comentario);
        });
    }
    
    public void update(Comentario comentario) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            comentarioDao.update(comentario);
            // Sync to Firebase
            firebaseRef.child(comentario.getId()).setValue(comentario);
        });
    }
    
    public void delete(Comentario comentario) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            comentarioDao.delete(comentario);
            // Delete from Firebase
            firebaseRef.child(comentario.getId()).removeValue();
        });
    }
    
    // Sync operations
    private void syncFromFirebase() {
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Comentario comentario = child.getValue(Comentario.class);
                        if (comentario != null) {
                            comentarioDao.insert(comentario);
                        }
                    }
                });
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }
    
    public void deleteOlderThan(long timestamp) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            comentarioDao.deleteOlderThan(timestamp);
        });
    }
}
