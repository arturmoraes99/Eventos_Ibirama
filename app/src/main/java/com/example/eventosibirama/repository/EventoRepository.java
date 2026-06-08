package com.example.eventosibirama.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventosibirama.model.Evento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventoRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth      auth;
    private static final String COLECAO = "eventos";

    public EventoRepository() {
        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // ── Leitura ───────────────────────────────────────────────────────────────

    public LiveData<List<Evento>> getTodosEventos() {
        MutableLiveData<List<Evento>> liveData = new MutableLiveData<>();
        db.collection(COLECAO).get()
                .addOnSuccessListener(snap -> {
                    List<Evento> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        Evento e = doc.toObject(Evento.class);
                        e.setId(doc.getId());
                        lista.add(e);
                    }
                    liveData.setValue(lista);
                })
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public LiveData<List<Evento>> getEventosPorCategoria(String categoriaId) {
        MutableLiveData<List<Evento>> liveData = new MutableLiveData<>();
        db.collection(COLECAO)
                .whereEqualTo("categoriaId", categoriaId)
                .get()
                .addOnSuccessListener(snap -> {
                    List<Evento> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        Evento e = doc.toObject(Evento.class);
                        e.setId(doc.getId());
                        lista.add(e);
                    }
                    liveData.setValue(lista);
                })
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public LiveData<Evento> getEventoPorId(String eventoId) {
        MutableLiveData<Evento> liveData = new MutableLiveData<>();
        db.collection(COLECAO).document(eventoId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Evento e = doc.toObject(Evento.class);
                        e.setId(doc.getId());
                        liveData.setValue(e);
                    } else {
                        liveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public LiveData<List<Evento>> buscarPorNome(String nome) {
        MutableLiveData<List<Evento>> liveData = new MutableLiveData<>();
        db.collection(COLECAO).get()
                .addOnSuccessListener(snap -> {
                    List<Evento> lista = new ArrayList<>();
                    String q = nome.toLowerCase();
                    for (QueryDocumentSnapshot doc : snap) {
                        Evento e = doc.toObject(Evento.class);
                        e.setId(doc.getId());
                        if (e.getNome() != null && e.getNome().toLowerCase().contains(q))
                            lista.add(e);
                    }
                    liveData.setValue(lista);
                })
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public LiveData<String> salvarEvento(Evento evento) {
        MutableLiveData<String> liveData = new MutableLiveData<>();

        if (auth.getCurrentUser() != null) {
            evento.setCriadoPorUid(auth.getCurrentUser().getUid());
        }

        db.collection(COLECAO)
                .add(evento)
                .addOnSuccessListener(ref -> liveData.setValue(ref.getId()))
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }

    public LiveData<Boolean> atualizarEvento(Evento evento) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        db.collection(COLECAO)
                .document(evento.getId())
                .set(evento)
                .addOnSuccessListener(unused -> liveData.setValue(true))
                .addOnFailureListener(e -> liveData.setValue(false));

        return liveData;
    }

    public LiveData<Boolean> excluirEvento(String eventoId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        db.collection(COLECAO)
                .document(eventoId)
                .delete()
                .addOnSuccessListener(unused -> liveData.setValue(true))
                .addOnFailureListener(e -> liveData.setValue(false));

        return liveData;
    }
}
