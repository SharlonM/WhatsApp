package com.sharlon.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;


public class LoginActivity extends AppCompatActivity {

    private EditText edtNome, edtNumero;
    public static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtNome = findViewById(R.id.edtNome);
        edtNumero = findViewById(R.id.edtNumero);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);

        // gerar mascaras de numero

        SimpleMaskFormatter formato = new SimpleMaskFormatter("+NN (NN) NNNNN-NNNN");
        MaskTextWatcher mascaraNumero = new MaskTextWatcher(edtNumero, formato);
        edtNumero.addTextChangedListener(mascaraNumero);

    }

    public void onClickCadastrar(View view) {

        if (edtNome.getText().toString().isEmpty()) {

            edtNome.setError("Campo obrigatorio");

        } else if (edtNumero.getText().toString().isEmpty()) {

            edtNumero.setError("Campo obrigatorio");

        } else {

            String nome = edtNome.getText().toString().trim();
            String numero = edtNumero.getText().toString().trim();

            //numero = numero.replace("+", "");
            numero = numero.replace("-", "");
            numero = numero.replace("(", "");
            numero = numero.replace(")", "");
            numero = numero.replaceAll(" ", "");

            ValidadorActivity.numero = numero;
            ValidadorActivity.nome = nome;

            dialog.show();

            Intent i = new Intent(this, ValidadorActivity.class);
            startActivity(i);

        }

    }



}
