package com.sharlon.whatsapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.sharlon.whatsapp.firebase.ConfigFirebase;
import com.sharlon.whatsapp.fragmentos.ConversaAdapter;
import com.sharlon.whatsapp.modelos.Contatos;
import com.sharlon.whatsapp.modelos.Historico;
import com.sharlon.whatsapp.modelos.Mensagens;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class ConversaActivity extends AppCompatActivity {

    public void hideKeyboard(Context c, EditText e) {
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
    }

    private EditText edtMensagem;
    private ArrayList<Mensagens> arrayMensagens;
    private ArrayAdapter<Mensagens> adapter;
    private DatabaseReference firebase;
    private ValueEventListener eventListener;
    private String nomeRemetente, edtmensagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        Bundle bundle = getIntent().getExtras();
        edtMensagem = findViewById(R.id.edt_mensagem);
        ImageButton imgEnviar = findViewById(R.id.imgSend);
        ListView listView = findViewById(R.id.lv_conversas);

        if (bundle != null) {

            final String nomeDestinatario = bundle.getString("nome");
            final String numeroDestinatario = bundle.getString("numero");
            final String numeroRemetente = ConfigFirebase.getUser().getPhoneNumber();

            //configurar a toolbar

            final Toolbar toolbar = findViewById(R.id.toolbarConversas);
            toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
            final ImageView img = toolbar.findViewById(R.id.imgConversaPerfil);
            final TextView txtNome = toolbar.findViewById(R.id.txt_nomeDestinatario);
            txtNome.setText(nomeDestinatario);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

            // recuperar foto
            try {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();

                assert numeroDestinatario != null;
                StorageReference sto = storageReference.child(numeroDestinatario);

                final File localFile = File.createTempFile("images", "png");
                sto.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Drawable fotoDestinatario = Drawable.createFromPath(localFile.getAbsolutePath());
                        img.setImageDrawable(fotoDestinatario);

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

            // configurar listview e adapter

            arrayMensagens = new ArrayList<>();
            adapter = new ConversaAdapter(this, arrayMensagens);
            listView.setAdapter(adapter);

            // recuperar historico de mensagens

            firebase = ConfigFirebase.getReferenciaBanco().child("Conversas")
                    .child(Objects.requireNonNull(numeroRemetente))
                    .child(Objects.requireNonNull(numeroDestinatario));

            eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        arrayMensagens.clear();

                        for (DataSnapshot dados : dataSnapshot.getChildren()) {
                            Mensagens mensagem = dados.getValue(Mensagens.class);
                            arrayMensagens.add(mensagem);
                        }

                        adapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            firebase.addValueEventListener(eventListener);

            imgEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    edtmensagem = edtMensagem.getText().toString();
                    hideKeyboard(getApplicationContext(), edtMensagem);

                    if (!edtmensagem.isEmpty()) {

                        // salvando mensagens que estao sendo trocadas

                        Mensagens mensagens = new Mensagens();
                        mensagens.setIdUsuario(numeroRemetente);
                        mensagens.setMensagem(edtmensagem);
                        mensagens.setHorario(MainActivity.data());

                        salvarMensagem(numeroRemetente, numeroDestinatario, mensagens);  // remetente
                        salvarMensagem(numeroDestinatario, numeroRemetente, mensagens); // destinatario

                        // salvando o ultimo historico de mensagens

                        // para o remetente

                        Historico hist = new Historico();
                        hist.setIdUsuario(numeroDestinatario);
                        hist.setNome(nomeDestinatario);
                        hist.setMensagem(edtmensagem);

                        salvarConversa(numeroRemetente, numeroDestinatario, hist);

                        // para o destinatario


                        ConfigFirebase.getReferenciaBanco().child("Usuarios").child(numeroDestinatario)
                                .child("Contatos").child(numeroRemetente).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Contatos cont;

                                if (dataSnapshot.exists()) {
                                    cont = dataSnapshot.getValue(Contatos.class);
                                    assert cont != null;
                                    nomeRemetente = cont.getNome();
                                    Log.w("nome", nomeRemetente);

                                    Historico histDestinatario = new Historico();
                                    histDestinatario.setNome(nomeRemetente);
                                    histDestinatario.setIdUsuario(numeroRemetente);
                                    histDestinatario.setMensagem(edtmensagem);

                                    salvarConversa(numeroDestinatario, numeroRemetente, histDestinatario);

                                    edtMensagem.setText("");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }
            });

        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagens mensagem) {

        try {

            ConfigFirebase.getReferenciaBanco().child("Conversas")
                    .child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.toast(getApplicationContext(), "Mensagem nao enviada");
        }
    }

    private void salvarConversa(String idRemetente, String idDestinatario, Historico historico) {
        try {

            firebase = ConfigFirebase.getReferenciaBanco()
                    .child("Historico");
            firebase.child(idRemetente).child(idDestinatario).setValue(historico);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(eventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebase.addValueEventListener(eventListener);
    }
}
