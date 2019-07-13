package com.sharlon.whatsapp.fragmentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sharlon.whatsapp.R;
import com.sharlon.whatsapp.modelos.Contatos;

import java.util.ArrayList;

public class ContatoAdapter extends ArrayAdapter<Contatos> {

    private ArrayList<Contatos> contatos;
    private Context context;

    public ContatoAdapter(Context context, ArrayList<Contatos> objects) {
        super(context, 0, objects);

        this.contatos = objects;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View view = null;

        // verificar se a lista esta vazia
        if (!contatos.isEmpty()) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // montar a view apartir do xml
            view = inflater.inflate(R.layout.lista_contatos, parent, false);

            // recuperar o elemento para exibiçao
            TextView nomeContato = view.findViewById(R.id.txt_nome);
            TextView emailContato = view.findViewById(R.id.txt_numero);
            nomeContato.setText(contatos.get(position).getNome());
            emailContato.setText(contatos.get(position).getNumero());

        }

        return view;
    }
}
