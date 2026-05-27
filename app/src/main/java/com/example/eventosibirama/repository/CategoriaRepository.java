package com.example.eventosibirama.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.eventosibirama.model.Categoria;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository {

    private final FirebaseFirestore db;
    private static final String COLECAO = "categorias";

    public CategoriaRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Categoria>> getCategorias() {
        MutableLiveData<List<Categoria>> liveData = new MutableLiveData<>();

        db.collection(COLECAO)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Categoria> categorias = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Categoria categoria = doc.toObject(Categoria.class);
                        categoria.setId(doc.getId());
                        categorias.add(categoria);
                    }
                    liveData.setValue(categorias);
                })
                .addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }
}
