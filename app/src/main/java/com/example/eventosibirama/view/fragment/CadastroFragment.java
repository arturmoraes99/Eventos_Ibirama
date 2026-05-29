package com.example.eventosibirama.view.fragment;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.eventosibirama.R;
import com.example.eventosibirama.view.activity.AuthActivity;
import com.example.eventosibirama.view.activity.MainActivity;
import com.example.eventosibirama.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class CadastroFragment extends Fragment {

    private TextInputLayout    tilNome, tilEmail, tilSenha, tilConfirmarSenha;
    private TextInputEditText  etNome, etEmail, etSenha, etConfirmarSenha;
    private MaterialButton     btnCadastrar, tvIrLogin;
    private ProgressBar        progressBar;

    private AuthViewModel authViewModel;

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

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        tilNome           = view.findViewById(R.id.tilNome);
        tilEmail          = view.findViewById(R.id.tilEmail);
        tilSenha          = view.findViewById(R.id.tilSenha);
        tilConfirmarSenha = view.findViewById(R.id.tilConfirmarSenha);
        etNome            = view.findViewById(R.id.et_nome);
        etEmail           = view.findViewById(R.id.et_email);
        etSenha           = view.findViewById(R.id.et_senha);
        etConfirmarSenha  = view.findViewById(R.id.et_confirmar_senha);
        btnCadastrar      = view.findViewById(R.id.btn_cadastrar);
        tvIrLogin         = view.findViewById(R.id.tv_ir_login);
        progressBar       = view.findViewById(R.id.progress_bar);

        btnCadastrar.setOnClickListener(v -> realizarCadastro());

        tvIrLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).carregarFragment(new LoginFragment());
            }
        });

        observarViewModel();
    }

    private void realizarCadastro() {
        // Limpa erros anteriores
        tilNome.setError(null);
        tilEmail.setError(null);
        tilSenha.setError(null);
        tilConfirmarSenha.setError(null);

        String nome           = etNome.getText()           != null ? etNome.getText().toString().trim()           : "";
        String email          = etEmail.getText()          != null ? etEmail.getText().toString().trim()          : "";
        String senha          = etSenha.getText()          != null ? etSenha.getText().toString().trim()          : "";
        String confirmarSenha = etConfirmarSenha.getText() != null ? etConfirmarSenha.getText().toString().trim() : "";

        if (TextUtils.isEmpty(nome)) {
            tilNome.setError(getString(R.string.erro_campos_vazios));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.erro_email_invalido));
            return;
        }
        if (TextUtils.isEmpty(senha) || senha.length() < 6) {
            tilSenha.setError(getString(R.string.erro_senha_curta));
            return;
        }
        if (!senha.equals(confirmarSenha)) {
            tilConfirmarSenha.setError(getString(R.string.erro_senhas_diferentes));
            return;
        }

        // Delega ao ViewModel — Fragment não conhece Firebase
        authViewModel.cadastrar(nome, email, senha);
    }

    private void observarViewModel() {
        authViewModel.getAuthState().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            progressBar.setVisibility(resource.isLoading() ? View.VISIBLE : View.GONE);
            btnCadastrar.setEnabled(!resource.isLoading());

            if (resource.isError()) {
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getNavegar().observe(getViewLifecycleOwner(), unused -> {
            startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });
    }
}
