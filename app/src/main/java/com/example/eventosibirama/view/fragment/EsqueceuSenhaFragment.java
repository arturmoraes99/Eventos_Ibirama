package com.example.eventosibirama.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventosibirama.R;
import com.example.eventosibirama.view.activity.AuthActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class EsqueceuSenhaFragment extends Fragment {

    private TextInputLayout   tilEmail;
    private TextInputEditText etEmail;
    private MaterialButton    btnEnviar, btnVoltar;
    private ProgressBar       progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_esqueceu_senha, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilEmail    = view.findViewById(R.id.tilEmail);
        etEmail     = view.findViewById(R.id.et_email);
        btnEnviar   = view.findViewById(R.id.btn_enviar);
        btnVoltar   = view.findViewById(R.id.btn_voltar_login);
        progressBar = view.findViewById(R.id.progress_bar);

        btnEnviar.setOnClickListener(v -> enviarEmail());

        btnVoltar.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).carregarFragment(new LoginFragment());
            }
        });
    }

    private void enviarEmail() {
        tilEmail.setError(null);
        String email = etEmail.getText() != null
                ? etEmail.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.erro_email_invalido));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnEnviar.setEnabled(false);

        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(),
                            getString(R.string.email_recuperacao_enviado),
                            Toast.LENGTH_LONG).show();
                    // Volta para o login após enviar
                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).carregarFragment(new LoginFragment());
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnEnviar.setEnabled(true);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
