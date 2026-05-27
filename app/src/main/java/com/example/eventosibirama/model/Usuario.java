package com.example.eventosibirama.model;

public class Usuario {

    private String uid;
    private String nome;
    private String email;
    private String fotoUrl;

    // Construtor vazio obrigatório para o Firestore
    public Usuario() {}

    public Usuario(String uid, String nome, String email) {
        this.uid   = uid;
        this.nome  = nome;
        this.email = email;
    }

    // Getters e Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
