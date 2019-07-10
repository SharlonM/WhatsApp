package com.sharlon.whatsapp.Autenticacao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.sharlon.whatsapp.MainActivity;
import com.sharlon.whatsapp.R;
import com.sharlon.whatsapp.firebase.ConfigFirebase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ValidadorActivity extends AppCompatActivity {

    public static String nome, numero;
    String credencial;
    private EditText codigo;
    private boolean liberacao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codigo = findViewById(R.id.edtCodigo);
        verificarComFirebase(numero);
    }

    private void verificarComFirebase(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        LoginActivity.dialog.dismiss();
                        logar(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // deu falha

                        LoginActivity.dialog.dismiss();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ValidadorActivity.this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                        alertDialog.setTitle("ERRO");


                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // numero invalido ou codigo invalido


                            alertDialog.setMessage("Não foi possivel detectar esse numero");


                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // tempo de 60s excedido

                            alertDialog.setMessage("Tempo de envio excedido");

                        } else {
                            alertDialog.setMessage(e.toString());
                        }

                        alertDialog.setPositiveButton("okay",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });

                        alertDialog.setCancelable(false);
                        alertDialog.create();
                        alertDialog.show();
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        // mensagem enviada para o numero, verificar manualmente se está correto
                        LoginActivity.dialog.dismiss();
                        liberacao = true;
                        credencial = s;

                    }
                }
        );

    }

    public void logar(PhoneAuthCredential credential) {

        ConfigFirebase.getReference().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    ConfigFirebase.setUser(Objects.requireNonNull(task.getResult()).getUser());

                    UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                    builder.setDisplayName(nome);

                    ConfigFirebase.getUser().updateProfile(builder.build());

                    ConfigFirebase.updateUsuario();

                    MainActivity.toast(getApplicationContext(), "Bem vindo " + ConfigFirebase.getUser().getDisplayName());

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                } else {

                    Log.w("Falha no logar", task.getException());
                    MainActivity.toast(getApplicationContext(), "Codigo Invalido");

                }

            }
        });

    }

    public void onClickValidar(View view) {

        if (!codigo.getText().toString().isEmpty()) {

            if (liberacao) {
                String token = codigo.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(credencial, token);
                logar(credential);

            }

        }

    }
}
