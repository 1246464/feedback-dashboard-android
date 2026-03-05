package com.example.visualizadorapp.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class FirebaseQueryOptimizer {
    
    // Cache para listeners ativos
    private static final Map<String, ValueEventListener> activeListeners = new HashMap<>();
    
    /**
     * Cria uma query otimizada com paginação
     */
    public static Query createPaginatedQuery(DatabaseReference ref, int pageSize) {
        return ref.limitToFirst(pageSize);
    }
    
    /**
     * Cria uma query otimizada por data
     */
    public static Query createDateRangeQuery(DatabaseReference ref, String startDate, String endDate) {
        return ref.orderByKey()
                  .startAt(startDate)
                  .endAt(endDate);
    }
    
    /**
     * Adiciona listener com cache
     * Previne múltiplos listeners na mesma referência
     */
    public static void addCachedListener(String key, DatabaseReference ref, ValueEventListener listener) {
        // Remove listener antigo se existir
        removeCachedListener(key, ref);
        
        // Adiciona novo listener
        ref.addValueEventListener(listener);
        activeListeners.put(key, listener);
    }
    
    /**
     * Remove listener do cache
     */
    public static void removeCachedListener(String key, DatabaseReference ref) {
        ValueEventListener listener = activeListeners.get(key);
        if (listener != null) {
            ref.removeEventListener(listener);
            activeListeners.remove(key);
        }
    }
    
    /**
     * Remove todos os listeners ativos
     */
    public static void removeAllListeners(DatabaseReference ref) {
        for (ValueEventListener listener : activeListeners.values()) {
            ref.removeEventListener(listener);
        }
        activeListeners.clear();
    }
    
    /**
     * Listener otimizado que só executa uma vez
     */
    public static void addSingleValueListener(DatabaseReference ref, OnDataLoadedListener listener) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (listener != null) {
                    listener.onDataLoaded(snapshot);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                if (listener != null) {
                    listener.onError(error.getMessage());
                }
            }
        });
    }
    
    /**
     * Listener com debounce
     * Útil para evitar múltiplas chamadas em sequência rápida
     */
    public static void addDebouncedListener(DatabaseReference ref, ValueEventListener listener, long delayMillis) {
        final long[] lastCallTime = {0};
        
        ValueEventListener debouncedListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCallTime[0] >= delayMillis) {
                    lastCallTime[0] = currentTime;
                    listener.onDataChange(snapshot);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                listener.onCancelled(error);
            }
        };
        
        ref.addValueEventListener(debouncedListener);
    }
    
    /**
     * Otimiza escrita em batch
     */
    public static void batchWrite(DatabaseReference ref, Map<String, Object> updates, OnCompleteListener listener) {
        ref.updateChildren(updates)
           .addOnSuccessListener(aVoid -> {
               if (listener != null) listener.onSuccess();
           })
           .addOnFailureListener(e -> {
               if (listener != null) listener.onFailure(e.getMessage());
           });
    }
    
    /**
     * Query com índice otimizado
     * Usa orderByChild para queries mais rápidas
     */
    public static Query createIndexedQuery(DatabaseReference ref, String childKey, Object value) {
        if (value instanceof String) {
            return ref.orderByChild(childKey).equalTo((String) value);
        } else if (value instanceof Double) {
            return ref.orderByChild(childKey).equalTo((Double) value);
        } else if (value instanceof Boolean) {
            return ref.orderByChild(childKey).equalTo((Boolean) value);
        } else if (value instanceof Long) {
            return ref.orderByChild(childKey).equalTo((Long) value);
        }
        return ref.orderByChild(childKey).equalTo(value.toString());
    }
    
    /**
     * Pré-carrega dados em cache
     */
    public static void preloadData(DatabaseReference ref) {
        ref.keepSynced(true);
    }
    
    /**
     * Desabilita sincronização automática
     */
    public static void disableSync(DatabaseReference ref) {
        ref.keepSynced(false);
    }
    
    // Interfaces de callback
    public interface OnDataLoadedListener {
        void onDataLoaded(DataSnapshot snapshot);
        void onError(String error);
    }
    
    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }
    
    /**
     * Configurações de otimização para o aplicativo
     */
    public static class CacheConfig {
        // Tempo de cache em memória (10 MB)
        public static final long CACHE_SIZE_BYTES = 10 * 1024 * 1024;
        
        // Tempo de expiração do cache (1 hora)
        public static final long CACHE_EXPIRATION_MS = 60 * 60 * 1000;
        
        // Número máximo de queries simultâneas
        public static final int MAX_CONCURRENT_QUERIES = 5;
        
        // Tamanho de página para paginação
        public static final int PAGE_SIZE = 20;
    }
}
