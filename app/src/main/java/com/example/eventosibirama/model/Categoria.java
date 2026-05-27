package com.example.eventosibirama.model;

public class Categoria {

    private String id;
    private String nome;
    private String iconeUrl;
    private int iconeResId; // para ícones locais

    // Construtor vazio obrigatório para o Firestore
    public Categoria() {}

    public Categoria(String id, String nome, int iconeResId) {
        this.id         = id;
        this.nome       = nome;
        this.iconeResId = iconeResId;
    }

    public Categoria(String id, String nome, String iconeUrl) {
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

    public int getIconeResId() { return iconeResId; }
    public void setIconeResId(int iconeResId) { this.iconeResId = iconeResId; }
}
