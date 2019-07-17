package com.sharlon.whatsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sharlon.whatsapp.firebase.ConfigFirebase;
import com.sharlon.whatsapp.fragmentos.ConversaAdapter;
import com.sharlon.whatsapp.modelos.Contatos;
import com.sharlon.whatsapp.modelos.Historico;
import com.sharlon.whatsapp.modelos.Mensagens;

import java.util.ArrayList;
import java.util.Objects;


public class ConversaActivity extends AppCompatActivity {

    private EditText edtMensagem;
    private ArrayList<Mensagens> arrayMensagens;
    private ArrayAdapter<Mensagens> adapter;
    private DatabaseReference firebase;
    private ValueEventListener eventListener;
    private String nomeRemetente;


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
            String fotoDestinatario = bundle.getString("foto");
            final String numeroRemetente = ConfigFirebase.getUser().getPhoneNumber();

            //configurar a toolbar

            Toolbar toolbar = findViewById(R.id.toolbarConversas);

            toolbar.setTitle(nomeDestinatario);
            toolbar.setTitleMarginStart(100);
            toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
            toolbar.setLogo(R.drawable.ic_account_circle_white_36dp);
            setSupportActionBar(toolbar);

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

                    String edtmensagem = edtMensagem.getText().toString();

                    if (!edtmensagem.isEmpty()) {

                        // salvando mensagens que estao sendo trocadas

                        Mensagens mensagens = new Mensagens();
                        mensagens.setIdUsuario(numeroRemetente);
                        mensagens.setMensagem(edtmensagem);

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

                                Contatos contato = new Contatos();

                                if (dataSnapshot.exists()) {
                                    contato = dataSnapshot.getValue(Contatos.class);
                                    nomeRemetente = contato.getNome();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Historico histDestinatario = new Historico();
                        histDestinatario.setNome(nomeRemetente);
                        histDestinatario.setIdUsuario(numeroRemetente);
                        histDestinatario.setMensagem(edtmensagem);

                        salvarConversa(numeroDestinatario, numeroRemetente, histDestinatario);

                        edtMensagem.setText("");

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
