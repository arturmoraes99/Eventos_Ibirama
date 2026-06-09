package com.example.eventosibirama.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.eventosibirama.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroCategoriaActivity extends AppCompatActivity {

    private TextInputLayout   tilNome, tilIcone, tilIconeUrl;
    private TextInputEditText etNome, etIcone, etIconeUrl;
    private MaterialButton    btnSalvar;
    private ProgressBar       progressBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_categoria);

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tilNome     = findViewById(R.id.tilNome);
        tilIcone    = findViewById(R.id.tilIcone);
        tilIconeUrl = findViewById(R.id.tilIconeUrl);
        etNome      = findViewById(R.id.et_nome);
        etIcone     = findViewById(R.id.et_icone);
        etIconeUrl  = findViewById(R.id.et_icone_url);
        btnSalvar   = findViewById(R.id.btn_salvar);
        progressBar = findViewById(R.id.progress_bar);

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {
        tilNome.setError(null);
        tilIcone.setError(null);
        tilIconeUrl.setError(null);

        String nome     = etNome.getText()     != null ? etNome.getText().toString().trim()     : "";
        String icone    = etIcone.getText()    != null ? etIcone.getText().toString().trim()    : "";
        String iconeUrl = etIconeUrl.getText() != null ? etIconeUrl.getText().toString().trim() : "";

        // Validações
        if (TextUtils.isEmpty(nome)) {
            tilNome.setError("Campo obrigatório");
            return;
        }
        if (TextUtils.isEmpty(icone) && TextUtils.isEmpty(iconeUrl)) {
            tilIcone.setError("Preencha o nome do ícone ou a URL");
            return;
        }
        if (!TextUtils.isEmpty(icone) && !TextUtils.isEmpty(iconeUrl)) {
            tilIcone.setError("Preencha apenas um dos dois");
            tilIconeUrl.setError("Preencha apenas um dos dois");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSalvar.setEnabled(false);

        Map<String, Object> categoria = new HashMap<>();
        categoria.put("nome", nome);

        // Salva o campo correto dependendo do que foi preenchido
        if (!TextUtils.isEmpty(iconeUrl)) {
            categoria.put("iconeUrl", iconeUrl);
        } else {
            categoria.put("iconeResId", icone);
        }

        db.collection("categorias")
                .add(categoria)
                .addOnSuccessListener(ref -> {
                    progressBar.setVisibility(View.GONE);
                    btnSalvar.setEnabled(true);
                    Toast.makeText(this, "Categoria salva com sucesso!", Toast.LENGTH_SHORT).show();
                    etNome.setText("");
                    etIcone.setText("");
                    etIconeUrl.setText("");
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSalvar.setEnabled(true);
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}