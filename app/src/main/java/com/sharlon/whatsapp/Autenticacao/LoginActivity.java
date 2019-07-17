package com.sharlon.whatsapp.Autenticacao;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.sharlon.whatsapp.MainActivity;
import com.sharlon.whatsapp.Permissao;
import com.sharlon.whatsapp.R;
import com.sharlon.whatsapp.firebase.ConfigFirebase;


public class LoginActivity extends AppCompatActivity {

    public static ProgressDialog dialog;
    private EditText edtNome, edtNumero;
    private String[] permisoes = {
            Manifest.permission.SEND_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermisoes(this, permisoes);


        if (ConfigFirebase.getReference().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {

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

            dialog.show();

            Intent i = new Intent(this, ValidadorActivity.class);

            i.putExtra("nome", nome);
            i.putExtra("numero", numero);

            startActivity(i);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                alertaValidacao();
            }
        }

    }

    private void alertaValidacao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setTitle("Permissao negada");
        builder.setMessage("Todas as permissoes precisam ser aceitas para usar o app");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.create();
        builder.show();
    }
}
