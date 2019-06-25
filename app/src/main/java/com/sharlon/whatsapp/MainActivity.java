package com.sharlon.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sharlon.whatsapp.Autenticacao.LoginActivity;
import com.sharlon.whatsapp.firebase.ConfigFirebase;

public class MainActivity extends AppCompatActivity {

    public static void toast(Context context, String mensagem) {

        Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show();

    }

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item_adcionar:
                return true;

            case R.id.item_pesquisa:
                return true;

            case R.id.item_sair:
                deslogarUsuario();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void deslogarUsuario() {
        ConfigFirebase.logouf();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
