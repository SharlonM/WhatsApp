package com.sharlon.whatsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sharlon.whatsapp.firebase.ConfigFirebase;
import com.sharlon.whatsapp.modelos.Usuario;

import java.io.File;
import java.io.IOException;

public class ConfigActivity extends AppCompatActivity {

    private ImageView imgPerfil;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private Usuario user;
    private Uri uriImagem;
    private EditText edtNome, edtNumero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Toolbar toolbar = findViewById(R.id.toolbarConfig);
        toolbar.setTitle("Configuraçoes");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        edtNome = findViewById(R.id.edtNome);
        edtNumero = findViewById(R.id.edtNumero);

        user = ConfigFirebase.getUsuarioAtual();

        recuperarFotoAtual();
        recuperarDadosAtuais();

        imgPerfil = findViewById(R.id.imgContatoPerfil);
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carregarFoto();
            }
        });
    }

    private void recuperarDadosAtuais() {
        final DatabaseReference database = ConfigFirebase.getReferenciaBanco()
                .child("Usuarios")
                .child(user.getNumero())
                .child("Dados");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Usuario userAtual = dataSnapshot.getValue(Usuario.class);
                    assert userAtual != null;
                    edtNome.setText(userAtual.getNome());
                    edtNumero.setText(userAtual.getNumero());

                    MainActivity.toast(ConfigActivity.this, "AS INFORMAÇOES PODEM DEMORAR ALGUNS SEGUNDOS A CARREGAR");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Falha", "recuperarDadosAtuais");
            }
        });

    }

    private void recuperarFotoAtual() {
        try {

            StorageReference sto = storageReference.child(user.getNumero());

            final File localFile = File.createTempFile("images", "png");
            sto.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imgPerfil.setImageBitmap(bitmap);
                    imgPerfil.setBackground(null);
                    uriImagem = Uri.fromFile(localFile);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Falha", "recuperar foto");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void salvarConfig(View view) {

        String novoNome = edtNome.getText().toString();

        if (!novoNome.isEmpty()) {
            user.setNome(novoNome);
            ConfigFirebase.salvarUsuarioBanco(user);
            MainActivity.toast(this, "Dados Atualizados");
            startActivity(new Intent(this, MainActivity.class));
        } else {
            MainActivity.toast(this, "Preencha seu nome");
        }

    }

    private void carregarFoto() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
        MainActivity.toast(ConfigActivity.this, "APOS IMAGEM ESCOLHIDA AGUARDE ALGUNS SEGUNDOS");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            uriImagem = data.getData();
            StorageReference stor = storageReference.child(user.getNumero());
            stor.putFile(uriImagem).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Log.w("SUCESSO", "upload feito");
                    imgPerfil.setImageURI(uriImagem);
                    imgPerfil.setBackground(null);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("FALHOU", "onFailure");
                    MainActivity.toast(ConfigActivity.this, "Erro ao fazer upload da imagem");
                }
            });

        }
    }
}
