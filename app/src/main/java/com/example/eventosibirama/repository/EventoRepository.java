package com.example.eventosibirama.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventosibirama.model.Evento;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventoRepository {

    private final FirebaseFirestore db;
    private static final String COLECAO = "eventos";

    public EventoRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /** Retorna LiveData — encapsulamento correto. */
    public LiveData<List<Evento>> getTodosEventos() {
        MutableLiveData<List<Evento>> liveData = new MutableLiveData<>();

        db.collection(COLECAO)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Evento> eventos = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Evento evento = doc.toObject(Evento.class);
                        evento.setId(doc.getId());
                        eventos.add(evento);
                    }
                    liveData.setValue(eventos);
                })
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }

    public LiveData<List<Evento>> getEventosPorCategoria(String categoriaId) {
        MutableLiveData<List<Evento>> liveData = new MutableLiveData<>();

        db.collection(COLECAO)
                .whereEqualTo("categoriaId", categoriaId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Evento> eventos = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Evento evento = doc.toObject(Evento.class);
                        evento.setId(doc.getId());
                        eventos.add(evento);
                    }
                    liveData.setValue(eventos);
                })
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }

    public LiveData<Evento> getEventoPorId(String eventoId) {
        MutableLiveData<Evento> liveData = new MutableLiveData<>();

        db.collection(COLECAO)
                .document(eventoId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Evento evento = doc.toObject(Evento.class);
                        evento.setId(doc.getId());
                        liveData.setValue(evento);
                    } else {
                        liveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }

    /**
     * Busca por nome — filtragem local após carregar.
     * Para produção, considere Algolia ou Typesense para busca server-side.
     */
    public LiveData<List<Evento>> buscarPorNome(String nome) {
        MutableLiveData<List<Evento>> liveData = new MutableLiveData<>();

        db.collection(COLECAO)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Evento> eventos = new ArrayList<>();
                    String queryLower = nome.toLowerCase();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Evento evento = doc.toObject(Evento.class);
                        evento.setId(doc.getId());
                        if (evento.getNome() != null &&
                                evento.getNome().toLowerCase().contains(queryLower)) {
                            eventos.add(evento);
                        }
                    }
                    liveData.setValue(eventos);
                })
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }
}
