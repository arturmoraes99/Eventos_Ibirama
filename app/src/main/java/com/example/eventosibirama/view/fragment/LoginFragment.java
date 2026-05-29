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


public class LoginFragment extends Fragment {

    private TextInputLayout    tilEmail, tilSenha;
    private TextInputEditText  etEmail, etSenha;
    private MaterialButton     btnLogin, tvIrCadastro;
    private ProgressBar        progressBar;

    private AuthViewModel authViewModel;

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

        // ViewModel compartilhado na Activity para sobreviver a transições de Fragment
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        tilEmail     = view.findViewById(R.id.tilEmail);
        tilSenha     = view.findViewById(R.id.tilSenha);
        etEmail      = view.findViewById(R.id.et_email);
        etSenha      = view.findViewById(R.id.et_senha);
        btnLogin     = view.findViewById(R.id.btn_login);
        tvIrCadastro = view.findViewById(R.id.tv_ir_cadastro);
        progressBar  = view.findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(v -> realizarLogin());

        tvIrCadastro.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).carregarFragment(new CadastroFragment());
            }
        });

        observarViewModel();
    }

    private void realizarLogin() {
        tilEmail.setError(null);
        tilSenha.setError(null);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String senha = etSenha.getText() != null ? etSenha.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.erro_email_invalido));
            return;
        }
        if (TextUtils.isEmpty(senha)) {
            tilSenha.setError(getString(R.string.erro_senha_curta));
            return;
        }

        // Delega ao ViewModel — Fragment não conhece Firebase
        authViewModel.login(email, senha);
    }

    private void observarViewModel() {
        authViewModel.getAuthState().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            // Atualiza o ProgressBar conforme o estado
            progressBar.setVisibility(resource.isLoading() ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!resource.isLoading());

            if (resource.isError()) {
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
            }
        });

        // Navega apenas uma vez quando o login tem sucesso (SingleLiveEvent)
        authViewModel.getNavegar().observe(getViewLifecycleOwner(), unused -> {
            startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });
    }
}
