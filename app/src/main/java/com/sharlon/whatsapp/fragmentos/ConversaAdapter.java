package com.sharlon.whatsapp.fragmentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sharlon.whatsapp.R;
import com.sharlon.whatsapp.firebase.ConfigFirebase;
import com.sharlon.whatsapp.modelos.Mensagens;

import java.util.ArrayList;

public class ConversaAdapter extends ArrayAdapter<Mensagens> {

    private Context c;
    private ArrayList<Mensagens> mensagens;

    public ConversaAdapter(Context context, ArrayList<Mensagens> objects) {
        super(context, 0, objects);
        this.c = context;
        this.mensagens = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = new View(getContext());

        if (!mensagens.isEmpty()) {

            // recuperar dados do remetente

            String numeroRementente = ConfigFirebase.getUser().getPhoneNumber();

            // inicializa o objeto
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // recupera o elemento para exibiçao
            Mensagens msg = mensagens.get(position);

            assert numeroRementente != null;
            if (numeroRementente.equals(msg.getIdUsuario())) {

                assert inflater != null;
                view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);

            } else {
                assert inflater != null;
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
            }

            TextView textoMensagem = view.findViewById(R.id.txt_mensagem);
            textoMensagem.setText(msg.getMensagem());
            TextView horario = view.findViewById(R.id.txt_horario);
            horario.setText(msg.getHorario());

        }

        return view;
    }
}
