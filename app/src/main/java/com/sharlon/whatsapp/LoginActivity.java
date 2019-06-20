package com.sharlon.whatsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText edtNome, edtNumero;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtNome = findViewById(R.id.edtNome);
        edtNumero = findViewById(R.id.edtNumero);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("br");

        // gerar mascaras de numero

        SimpleMaskFormatter formato = new SimpleMaskFormatter("+NN (NN) NNNNN-NNNN");
        MaskTextWatcher mascaraNumero = new MaskTextWatcher(edtNumero, formato);
        edtNumero.addTextChangedListener(mascaraNumero);

    }

    public void onClickCadastrar(View view) {

        String nome = edtNome.getText().toString().trim();
        String numero = edtNumero.getText().toString().trim();

        numero = numero.replace("+", "");
        numero = numero.replace("-", "");
        numero = numero.replace("(", "");
        numero = numero.replace(")", "");
        numero = numero.replaceAll(" ", "");

        Toast.makeText(getApplicationContext(), numero, Toast.LENGTH_LONG).show();

        int numeroRandomico = new Random().nextInt(9999 - 1000) + 1000;

        String token = String.valueOf(numeroRandomico);

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
                        logar(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // deu falha

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // numero invalido ou codigo invalido
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // tempo de 60s excedido
                        }
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        // mensagem enviada para o numero, verificar manualmente se está correto

                        // PhoneAuthCredential credential = PhoneAuthProvider.getCredential(s,numero que o usuario digitou);

                    }
                }
        );

    }

    public void logar(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = task.getResult().getUser();

                } else {

                    Log.w("Falha no logar", task.getException());

                }

            }
        });

    }

}
