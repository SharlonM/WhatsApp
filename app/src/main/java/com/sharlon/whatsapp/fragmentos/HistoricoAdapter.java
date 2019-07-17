package com.sharlon.whatsapp.fragmentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sharlon.whatsapp.R;
import com.sharlon.whatsapp.modelos.Historico;

import java.util.ArrayList;

public class HistoricoAdapter extends ArrayAdapter<Historico> {

    private ArrayList<Historico> historicos;
    private Context c;

    public HistoricoAdapter(Context context, ArrayList<Historico> objects) {
        super(context, 0, objects);
        this.c = context;
        this.historicos = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (!historicos.isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lista_contatos, parent, false);

            TextView nome = view.findViewById(R.id.txt_nome);
            TextView ultimaMensagem = view.findViewById(R.id.txt_numero);

            Historico historico = historicos.get(position);
            nome.setText(historico.getNome());
            ultimaMensagem.setText(historico.getMensagem());
        }

        return view;
    }
}
