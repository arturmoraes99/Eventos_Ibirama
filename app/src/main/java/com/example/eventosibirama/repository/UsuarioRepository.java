package com.example.eventosibirama.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventosibirama.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UsuarioRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth      auth;
    private static final String COLECAO = "usuarios";

    public UsuarioRepository() {
        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<Usuario> getUsuarioLogado() {
        MutableLiveData<Usuario> liveData = new MutableLiveData<>();

        String uid = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid() : null;

        if (uid == null) { liveData.setValue(null); return liveData; }

        db.collection(COLECAO).document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) liveData.setValue(doc.toObject(Usuario.class));
                    else              liveData.setValue(null);
                })
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }

    public LiveData<Boolean> atualizarPerfil(String novoNome, String novaFotoUrl) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        String uid = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid() : null;

        if (uid == null) { liveData.setValue(false); return liveData; }

        Map<String, Object> campos = new HashMap<>();
        campos.put("nome",    novoNome);
        campos.put("fotoUrl", novaFotoUrl != null ? novaFotoUrl : "");

        db.collection(COLECAO).document(uid)
                .update(campos)
                .addOnSuccessListener(unused -> liveData.setValue(true))
                .addOnFailureListener(e -> liveData.setValue(false));

        return liveData;
    }
}
