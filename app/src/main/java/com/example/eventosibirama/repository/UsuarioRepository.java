package com.example.eventosibirama.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.eventosibirama.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsuarioRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private static final String COLECAO = "usuarios";

    public UsuarioRepository() {
        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public MutableLiveData<Usuario> getUsuarioLogado() {
        MutableLiveData<Usuario> liveData = new MutableLiveData<>();

        String uid = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid()
                : null;

        if (uid == null) {
            liveData.setValue(null);
            return liveData;
        }

        db.collection(COLECAO)
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Usuario usuario = doc.toObject(Usuario.class);
                        liveData.setValue(usuario);
                    } else {
                        liveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }
}
