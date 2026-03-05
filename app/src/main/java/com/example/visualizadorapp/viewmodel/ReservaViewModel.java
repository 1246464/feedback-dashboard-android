package com.example.visualizadorapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.visualizadorapp.model.Reserva;
import com.example.visualizadorapp.repository.ReservaRepository;
import java.util.List;

public class ReservaViewModel extends AndroidViewModel {
    private ReservaRepository repository;
    private LiveData<List<Reserva>> allReservas;
    
    public ReservaViewModel(@NonNull Application application) {
        super(application);
        repository = new ReservaRepository(application);
        allReservas = repository.getAllReservas();
    }
    
    public LiveData<List<Reserva>> getAllReservas() {
        return allReservas;
    }
    
    public LiveData<List<Reserva>> getReservasByUser(String userId) {
        return repository.getReservasByUser(userId);
    }
    
    public LiveData<List<Reserva>> getReservasByData(String data) {
        return repository.getReservasByData(data);
    }
    
    public LiveData<Reserva> getReservaAtiva(String data, String userId) {
        return repository.getReservaAtiva(data, userId);
    }
    
    public LiveData<List<Reserva>> getReservasByStatus(String status) {
        return repository.getReservasByStatus(status);
    }
    
    public LiveData<Integer> getCountReservasAtivasByData(String data) {
        return repository.getCountReservasAtivasByData(data);
    }
    
    public void insert(Reserva reserva, ReservaRepository.OnSuccessListener listener) {
        repository.insert(reserva, listener);
    }
    
    public void update(Reserva reserva) {
        repository.update(reserva);
    }
    
    public void delete(Reserva reserva) {
        repository.delete(reserva);
    }
    
    public void updateStatus(int id, String status) {
        repository.updateStatus(id, status);
    }
    
    public void deleteOlderThan(String data) {
        repository.deleteOlderThan(data);
    }
}
