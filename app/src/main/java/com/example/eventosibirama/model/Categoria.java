package com.example.eventosibirama.model;

public class Categoria {

    private String id;
    private String nome;
    private String iconeUrl;
    private String iconeResId;

    // Construtor vazio obrigatório para o Firestore
    public Categoria() {}

    public Categoria(String id, String nome, String iconeResId) {
        this.id         = id;
        this.nome       = nome;
        this.iconeResId = iconeResId;
    }

    public Categoria(String id, String nome, String iconeUrl, boolean isUrl) {
        this.id       = id;
        this.nome     = nome;
        this.iconeUrl = iconeUrl;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIconeUrl() { return iconeUrl; }
    public void setIconeUrl(String iconeUrl) { this.iconeUrl = iconeUrl; }

    public String getIconeResId() { return iconeResId; }
    public void setIconeResId(String iconeResId) { this.iconeResId = iconeResId; }
}