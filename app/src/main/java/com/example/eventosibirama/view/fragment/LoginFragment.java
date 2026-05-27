package com.example.eventosibirama.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventosibirama.R;
import com.example.eventosibirama.view.activity.AuthActivity;
import com.example.eventosibirama.view.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private EditText etEmail, etSenha;
    private Button btnLogin;
    private TextView tvIrCadastro;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        etEmail    = view.findViewById(R.id.et_email);
        etSenha    = view.findViewById(R.id.et_senha);
        btnLogin   = view.findViewById(R.id.btn_login);
        tvIrCadastro = view.findViewById(R.id.tv_ir_cadastro);

        btnLogin.setOnClickListener(v -> realizarLogin());

        tvIrCadastro.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).carregarFragment(new CadastroFragment());
            }
        });
    }

    private void realizarLogin() {
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Informe o e-mail");
            return;
        }
        if (TextUtils.isEmpty(senha)) {
            etSenha.setError("Informe a senha");
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(getContext(),
                                "Erro: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        btnLogin.setEnabled(true);
                    }
                });
    }
}
