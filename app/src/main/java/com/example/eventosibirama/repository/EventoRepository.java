package com.example.eventosibirama.repository;

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

    // Busca todos os eventos
    public MutableLiveData<List<Evento>> getTodosEventos() {
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

    // Busca eventos por categoria
    public MutableLiveData<List<Evento>> getEventosPorCategoria(String categoriaId) {
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

    // Busca evento por ID
    public MutableLiveData<Evento> getEventoPorId(String eventoId) {
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

    // Busca eventos por nome (busca local após carregar)
    public MutableLiveData<List<Evento>> buscarPorNome(String nome) {
        MutableLiveData<List<Evento>> liveData = new MutableLiveData<>();

        db.collection(COLECAO)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Evento> eventos = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Evento evento = doc.toObject(Evento.class);
                        evento.setId(doc.getId());
                        if (evento.getNome() != null &&
                                evento.getNome().toLowerCase()
                                        .contains(nome.toLowerCase())) {
                            eventos.add(evento);
                        }
                    }
                    liveData.setValue(eventos);
                })
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }
}
