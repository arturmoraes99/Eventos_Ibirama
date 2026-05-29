package com.example.eventosibirama.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventosibirama.model.Evento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritoRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private static final String COLECAO_USUARIOS  = "usuarios";
    private static final String COLECAO_FAVORITOS = "favoritos";
    private static final String COLECAO_EVENTOS   = "eventos";

    public FavoritoRepository() {
        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private String getUid() {
        return auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid()
                : null;
    }

    public LiveData<Boolean> adicionarFavorito(String eventoId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        String uid = getUid();
        if (uid == null) { liveData.setValue(false); return liveData; }

        Map<String, Object> favorito = new HashMap<>();
        favorito.put("eventoId", eventoId);

        db.collection(COLECAO_USUARIOS)
                .document(uid)
                .collection(COLECAO_FAVORITOS)
                .document(eventoId)
                .set(favorito)
                .addOnSuccessListener(unused -> liveData.setValue(true))
                .addOnFailureListener(e -> liveData.setValue(false));

        return liveData;
    }

    public LiveData<Boolean> removerFavorito(String eventoId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        String uid = getUid();
        if (uid == null) { liveData.setValue(false); return liveData; }

        db.collection(COLECAO_USUARIOS)
                .document(uid)
                .collection(COLECAO_FAVORITOS)
                .document(eventoId)
                .delete()
                .addOnSuccessListener(unused -> liveData.setValue(true))
                .addOnFailureListener(e -> liveData.setValue(false));

        return liveData;
    }

    public LiveData<Boolean> isFavorito(String eventoId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        String uid = getUid();
        if (uid == null) { liveData.setValue(false); return liveData; }

        db.collection(COLECAO_USUARIOS)
                .document(uid)
                .collection(COLECAO_FAVORITOS)
                .document(eventoId)
                .get()
                .addOnSuccessListener(doc -> liveData.setValue(doc.exists()))
                .addOnFailureListener(e -> liveData.setValue(false));

        return liveData;
    }

    public LiveData<List<Evento>> getFavoritos() {
        MutableLiveData<List<Evento>> liveData = new MutableLiveData<>();
        String uid = getUid();
        if (uid == null) { liveData.setValue(new ArrayList<>()); return liveData; }

        db.collection(COLECAO_USUARIOS)
                .document(uid)
                .collection(COLECAO_FAVORITOS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> ids = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        ids.add(doc.getId());
                    }
                    buscarEventosPorIds(ids, liveData);
                })
                .addOnFailureListener(e -> liveData.setValue(new ArrayList<>()));

        return liveData;
    }

    private void buscarEventosPorIds(List<String> ids,
                                     MutableLiveData<List<Evento>> liveData) {
        if (ids.isEmpty()) {
            liveData.setValue(new ArrayList<>());
            return;
        }

        List<Evento> eventos = new ArrayList<>();
        final int[] contador = {0};

        for (String id : ids) {
            db.collection(COLECAO_EVENTOS)
                    .document(id)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            Evento evento = doc.toObject(Evento.class);
                            evento.setId(doc.getId());
                            eventos.add(evento);
                        }
                        contador[0]++;
                        if (contador[0] == ids.size()) {
                            liveData.setValue(eventos);
                        }
                    })
                    .addOnFailureListener(e -> {
                        contador[0]++;
                        if (contador[0] == ids.size()) {
                            liveData.setValue(eventos);
                        }
                    });
        }
    }
}
