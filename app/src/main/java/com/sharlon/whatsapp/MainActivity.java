package com.sharlon.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sharlon.whatsapp.Autenticacao.LoginActivity;
import com.sharlon.whatsapp.firebase.ConfigFirebase;

public class MainActivity extends AppCompatActivity {

    public static void toast(Context context, String mensagem) {

        Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSair = findViewById(R.id.btnSair);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigFirebase.logouf();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
