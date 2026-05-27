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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroFragment extends Fragment {

    private EditText etNome, etEmail, etSenha, etConfirmarSenha;
    private Button btnCadastrar;
    private TextView tvIrLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cadastro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        etNome           = view.findViewById(R.id.et_nome);
        etEmail          = view.findViewById(R.id.et_email);
        etSenha          = view.findViewById(R.id.et_senha);
        etConfirmarSenha = view.findViewById(R.id.et_confirmar_senha);
        btnCadastrar     = view.findViewById(R.id.btn_cadastrar);
        tvIrLogin        = view.findViewById(R.id.tv_ir_login);

        btnCadastrar.setOnClickListener(v -> realizarCadastro());

        tvIrLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).carregarFragment(new LoginFragment());
            }
        });
    }

    private void realizarCadastro() {
        String nome           = etNome.getText().toString().trim();
        String email          = etEmail.getText().toString().trim();
        String senha          = etSenha.getText().toString().trim();
        String confirmarSenha = etConfirmarSenha.getText().toString().trim();

        if (TextUtils.isEmpty(nome)) {
            etNome.setError("Informe seu nome");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Informe o e-mail");
            return;
        }
        if (TextUtils.isEmpty(senha)) {
            etSenha.setError("Informe a senha");
            return;
        }
        if (senha.length() < 6) {
            etSenha.setError("Senha deve ter no mínimo 6 caracteres");
            return;
        }
        if (!senha.equals(confirmarSenha)) {
            etConfirmarSenha.setError("As senhas não coincidem");
            return;
        }

        btnCadastrar.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        salvarUsuarioNoFirestore(nome, email);
                    } else {
                        Toast.makeText(getContext(),
                                "Erro: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        btnCadastrar.setEnabled(true);
                    }
                });
    }

    private void salvarUsuarioNoFirestore(String nome, String email) {
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nome", nome);
        usuario.put("email", email);
        usuario.put("uid", uid);

        db.collection("usuarios")
                .document(uid)
                .set(usuario)
                .addOnSuccessListener(unused -> {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Erro ao salvar usuário: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    btnCadastrar.setEnabled(true);
                });
    }
}
