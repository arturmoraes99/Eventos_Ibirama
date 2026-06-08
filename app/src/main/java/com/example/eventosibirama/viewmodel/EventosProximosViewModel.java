package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.repository.EventoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventosProximosViewModel extends ViewModel {

    private final EventoRepository repository;

    private final MediatorLiveData<List<Evento>> _eventos   = new MediatorLiveData<>();
    private final MutableLiveData<Boolean>        _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String>         _erro      = new MutableLiveData<>();

    private LiveData<List<Evento>> eventosSource;


    private static final double RAIO_KM = 20.0;

    public EventosProximosViewModel() {
        repository = new EventoRepository();
    }

    public LiveData<List<Evento>> getEventos()   { return _eventos;   }
    public LiveData<Boolean>      getIsLoading() { return _isLoading; }
    public LiveData<String>       getErro()      { return _erro;      }


    public void carregarEventosProximos(double userLat, double userLng) {
        _isLoading.setValue(true);
        _erro.setValue(null);

        if (eventosSource != null) _eventos.removeSource(eventosSource);
        eventosSource = repository.getTodosEventos();

        _eventos.addSource(eventosSource, lista -> {
            _isLoading.setValue(false);

            if (lista == null) {
                _erro.setValue("Erro ao carregar eventos.");
                return;
            }

            List<Evento> proximos = new ArrayList<>();
            for (Evento evento : lista) {
                if (evento.getLatitude() == 0 && evento.getLongitude() == 0) continue;

                double dist = calcularDistanciaKm(
                        userLat, userLng,
                        evento.getLatitude(), evento.getLongitude());

                if (dist <= RAIO_KM) {
                    evento.setDistancia(dist);
                    proximos.add(evento);
                }
            }


            Collections.sort(proximos,
                    (a, b) -> Double.compare(a.getDistancia(), b.getDistancia()));

            _eventos.setValue(proximos);
        });
    }


    private double calcularDistanciaKm(double lat1, double lon1,
                                       double lat2, double lon2) {
        final double R = 6371.0; // raio da Terra em km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
