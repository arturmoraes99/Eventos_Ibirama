package com.example.eventosibirama.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventosibirama.R;
import com.example.eventosibirama.view.fragment.LoginFragment;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Carrega o LoginFragment por padrão
        if (savedInstanceState == null) {
            carregarFragment(new LoginFragment());
        }
    }

    public void carregarFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

