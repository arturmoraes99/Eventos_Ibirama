package com.example.eventosibirama.model;

public class Evento {

    private String id;
    private String nome;
    private String descricao;
    private String data;
    private String hora;
    private String local;
    private String imagemUrl;
    private String categoriaId;
    private double latitude;
    private double longitude;
    private boolean favorito;

    // Construtor vazio obrigatório para o Firestore
    public Evento() {}

    public Evento(String id, String nome, String descricao, String data,
                  String hora, String local, String imagemUrl,
                  String categoriaId, double latitude, double longitude) {
        this.id          = id;
        this.nome        = nome;
        this.descricao   = descricao;
        this.data        = data;
        this.hora        = hora;
        this.local       = local;
        this.imagemUrl   = imagemUrl;
        this.categoriaId = categoriaId;
        this.latitude    = latitude;
        this.longitude   = longitude;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public String getCategoriaId() { return categoriaId; }
    public void setCategoriaId(String categoriaId) { this.categoriaId = categoriaId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public boolean isFavorito() { return favorito; }
    public void setFavorito(boolean favorito) { this.favorito = favorito; }
}
