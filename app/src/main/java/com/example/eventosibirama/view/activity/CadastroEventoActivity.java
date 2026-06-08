package com.example.eventosibirama.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventosibirama.R;
import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.viewmodel.CadastroEventoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class CadastroEventoActivity extends AppCompatActivity {

    public static final String EXTRA_EVENTO_ID = "evento_id";

    private TextInputLayout   tilNome, tilDescricao, tilData, tilHora,
            tilLocal, tilImagem, tilLat, tilLng, tilCategoria;
    private TextInputEditText etNome, etDescricao, etData, etHora,
            etLocal, etImagem, etLat, etLng, etCategoria;
    private MaterialButton    btnSalvar;
    private ProgressBar       progressBar;

    private CadastroEventoViewModel viewModel;
    private String eventoId = null; // null = modo criar
    private Evento eventoEditando = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_evento);

        eventoId  = getIntent().getStringExtra(EXTRA_EVENTO_ID);
        viewModel = new ViewModelProvider(this).get(CadastroEventoViewModel.class);

        configurarToolbar();
        inicializarViews();
        observarViewModel();

        if (eventoId != null) {
            // Modo edição: carrega dados existentes
            viewModel.carregarEvento(eventoId);
        }

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    eventoId != null
                            ? getString(R.string.editar_evento)
                            : getString(R.string.cadastrar_evento));
        }
    }

    private void inicializarViews() {
        tilNome      = findViewById(R.id.tilNome);
        tilDescricao = findViewById(R.id.tilDescricao);
        tilData      = findViewById(R.id.tilData);
        tilHora      = findViewById(R.id.tilHora);
        tilLocal     = findViewById(R.id.tilLocal);
        tilImagem    = findViewById(R.id.tilImagem);
        tilLat       = findViewById(R.id.tilLat);
        tilLng       = findViewById(R.id.tilLng);
        tilCategoria = findViewById(R.id.tilCategoria);

        etNome      = findViewById(R.id.et_nome);
        etDescricao = findViewById(R.id.et_descricao);
        etData      = findViewById(R.id.et_data);
        etHora      = findViewById(R.id.et_hora);
        etLocal     = findViewById(R.id.et_local);
        etImagem    = findViewById(R.id.et_imagem);
        etLat       = findViewById(R.id.et_lat);
        etLng       = findViewById(R.id.et_lng);
        etCategoria = findViewById(R.id.et_categoria);

        btnSalvar   = findViewById(R.id.btn_salvar);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void observarViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading == null) return;
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSalvar.setEnabled(!isLoading);
        });

        viewModel.getErro().observe(this, erro -> {
            if (erro != null) Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
        });

        viewModel.getSucesso().observe(this, ok -> {
            if (Boolean.TRUE.equals(ok)) {
                Toast.makeText(this,
                        eventoId != null
                                ? getString(R.string.evento_atualizado)
                                : getString(R.string.evento_cadastrado),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Modo edição: preenche campos com dados do evento
        viewModel.getEvento().observe(this, evento -> {
            if (evento != null) {
                eventoEditando = evento;
                preencherCampos(evento);
            }
        });
    }

    private void preencherCampos(Evento e) {
        etNome.setText(e.getNome());
        etDescricao.setText(e.getDescricao());
        etData.setText(e.getData());
        etHora.setText(e.getHora());
        etLocal.setText(e.getLocal());
        etImagem.setText(e.getImagemUrl());
        etLat.setText(String.valueOf(e.getLatitude()));
        etLng.setText(String.valueOf(e.getLongitude()));
        etCategoria.setText(e.getCategoriaId());
    }

    private void salvar() {
        // Limpa erros
        tilNome.setError(null);
        tilData.setError(null);
        tilHora.setError(null);
        tilLocal.setError(null);

        String nome       = getText(etNome);
        String descricao  = getText(etDescricao);
        String data       = getText(etData);
        String hora       = getText(etHora);
        String local      = getText(etLocal);
        String imagem     = getText(etImagem);
        String latStr     = getText(etLat);
        String lngStr     = getText(etLng);
        String categoriaId = getText(etCategoria);

        // Validações obrigatórias
        if (TextUtils.isEmpty(nome)) {
            tilNome.setError(getString(R.string.erro_campo_obrigatorio));
            return;
        }
        if (TextUtils.isEmpty(data)) {
            tilData.setError(getString(R.string.erro_campo_obrigatorio));
            return;
        }
        if (TextUtils.isEmpty(hora)) {
            tilHora.setError(getString(R.string.erro_campo_obrigatorio));
            return;
        }
        if (TextUtils.isEmpty(local)) {
            tilLocal.setError(getString(R.string.erro_campo_obrigatorio));
            return;
        }

        double lat = 0, lng = 0;
        try {
            if (!TextUtils.isEmpty(latStr)) lat = Double.parseDouble(latStr);
            if (!TextUtils.isEmpty(lngStr)) lng = Double.parseDouble(lngStr);
        } catch (NumberFormatException e) {
            tilLat.setError(getString(R.string.erro_coordenada_invalida));
            return;
        }

        if (eventoId != null && eventoEditando != null) {
            // Modo edição: preserva o criadoPorUid original
            eventoEditando.setNome(nome);
            eventoEditando.setDescricao(descricao);
            eventoEditando.setData(data);
            eventoEditando.setHora(hora);
            eventoEditando.setLocal(local);
            eventoEditando.setImagemUrl(imagem);
            eventoEditando.setLatitude(lat);
            eventoEditando.setLongitude(lng);
            eventoEditando.setCategoriaId(categoriaId);
            viewModel.atualizarEvento(eventoEditando);
        } else {
            // Modo criar
            Evento novo = new Evento();
            novo.setNome(nome);
            novo.setDescricao(descricao);
            novo.setData(data);
            novo.setHora(hora);
            novo.setLocal(local);
            novo.setImagemUrl(imagem);
            novo.setLatitude(lat);
            novo.setLongitude(lng);
            novo.setCategoriaId(categoriaId);
            viewModel.salvarEvento(novo);
        }
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}

