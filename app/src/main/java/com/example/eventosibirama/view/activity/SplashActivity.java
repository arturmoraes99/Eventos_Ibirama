package com.example.eventosibirama.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventosibirama.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

            Intent intent;
            if (usuario != null) {
                // Usuário já logado → vai direto para o MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Não logado → vai para tela de autenticação
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
