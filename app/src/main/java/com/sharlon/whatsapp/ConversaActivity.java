package com.sharlon.whatsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sharlon.whatsapp.firebase.ConfigFirebase;
import com.sharlon.whatsapp.modelos.Mensagens;


public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText edtMensagem;
    private ImageButton imgEnviar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        Bundle bundle = getIntent().getExtras();
        edtMensagem = findViewById(R.id.edt_mensagem);
        imgEnviar = findViewById(R.id.imgSend);

        if (bundle != null) {

            final String nomeDestinatario = bundle.getString("nome");
            final String numeroDestinatario = bundle.getString("numero");
            final String historico = bundle.getString("historico");
            String fotoDestinatario = bundle.getString("foto");

            toolbar = findViewById(R.id.toolbarConversas);

            toolbar.setTitle(nomeDestinatario);
            toolbar.setTitleMarginStart(100);
            toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
            toolbar.setLogo(R.drawable.ic_account_circle_white_36dp);
            setSupportActionBar(toolbar);

            imgEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String edtmensagem = edtMensagem.getText().toString();

                    if (!edtmensagem.isEmpty()) {

                        Mensagens mensagens = new Mensagens();
                        mensagens.setIdUsuario(ConfigFirebase.getUser().getPhoneNumber());
                        mensagens.setMensagem(edtmensagem);

                        salvarMensagem(ConfigFirebase.getUser().getPhoneNumber(), numeroDestinatario, mensagens);

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
}
