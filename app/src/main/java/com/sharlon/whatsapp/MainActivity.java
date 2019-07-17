package com.sharlon.whatsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sharlon.whatsapp.Autenticacao.LoginActivity;
import com.sharlon.whatsapp.firebase.ConfigFirebase;
import com.sharlon.whatsapp.fragmentos.TabAdapter;
import com.sharlon.whatsapp.modelos.Contatos;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static void toast(Context context, String mensagem) {

        Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tabMain);
        ViewPager viewPager = findViewById(R.id.viewPager);

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        tabLayout.setupWithViewPager(viewPager);

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
                abrirCadastroDeContato();
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

    private void abrirCadastroDeContato() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);

        final EditText editText = new EditText(this);
        final EditText editNome = new EditText(this);

        // editText para o numero do novo usuario

        SimpleMaskFormatter formato = new SimpleMaskFormatter("+NNNNNNNNNNNNN");
        MaskTextWatcher mascaraNumero = new MaskTextWatcher(editText, formato);
        editText.addTextChangedListener(mascaraNumero);
        editText.setHint("+55 (82) 93333-3333");
        editText.setTextColor(Color.WHITE);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);

        //EditText para o nome do contato

        editNome.setHint("Nome do Contato");
        editNome.setTextColor(Color.WHITE);
        editNome.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);


        // Configurar alertDialog com o layout

        alertDialog.setTitle("Novo Contato");
        alertDialog.setMessage("Dados do contato");
        alertDialog.setCancelable(false);


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText);
        layout.addView(editNome);

        alertDialog.setView(layout);


        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String numeroContato = editText.getText().toString().trim();
                final String nomeContato = editNome.getText().toString().trim();

                if (numeroContato.isEmpty() || nomeContato.isEmpty()) {
                    toast(MainActivity.this, "Dados invalidos");
                } else {

                    ConfigFirebase.getReferenciaBanco().child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(numeroContato).exists()) {

                                Contatos contato = new Contatos();
                                contato.setNome(nomeContato);
                                contato.setNumero(numeroContato);


                                ConfigFirebase.getReferenciaBanco()
                                        .child("Usuarios")
                                        .child(Objects.requireNonNull(ConfigFirebase.getUser().getPhoneNumber()))
                                        .child("Contatos")
                                        .child(numeroContato)
                                        .setValue(contato);

                                toast(MainActivity.this, "Usuario Adcionado");


                            } else {
                                toast(MainActivity.this, "Usuario não encontrado");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            toast(MainActivity.this, "Usuario não encontrado");
                        }
                    });
                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // apenas fechar
            }
        });

        alertDialog.create().show();

    }

    private void deslogarUsuario() {
        ConfigFirebase.logouf();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
