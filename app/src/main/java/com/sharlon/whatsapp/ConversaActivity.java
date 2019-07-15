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
import com.sharlon.whatsapp.modelos.Mensagens;

import java.util.ArrayList;


public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText edtMensagem;
    private ImageButton imgEnviar;
    private ListView listView;
    private ArrayList<Mensagens> arrayMensagens;
    private ArrayAdapter<Mensagens> adapter;
    private DatabaseReference firebase;
    private ValueEventListener eventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        Bundle bundle = getIntent().getExtras();
        edtMensagem = findViewById(R.id.edt_mensagem);
        imgEnviar = findViewById(R.id.imgSend);
        listView = findViewById(R.id.lv_conversas);

        if (bundle != null) {

            final String nomeDestinatario = bundle.getString("nome");
            final String numeroDestinatario = bundle.getString("numero");
            final String historico = bundle.getString("historico");
            String fotoDestinatario = bundle.getString("foto");
            final String numeroRemetente = ConfigFirebase.getUser().getPhoneNumber();

            //configurar a toolbar

            toolbar = findViewById(R.id.toolbarConversas);

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
                    .child(numeroRemetente)
                    .child(numeroDestinatario);

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

                        Mensagens mensagens = new Mensagens();
                        mensagens.setIdUsuario(numeroRemetente);
                        mensagens.setMensagem(edtmensagem);

                        salvarMensagem(numeroRemetente, numeroDestinatario, mensagens);

                        edtMensagem.setText("");

                    }

                }
            });

        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagens mensagem) {

        /*
            +mensagens
                +829999
                    +8297144
                        mensagem
                    +7894661
                        mensagem
            */

        try {

            ConfigFirebase.getReferenciaBanco().child("Conversas")
                    .child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);

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
