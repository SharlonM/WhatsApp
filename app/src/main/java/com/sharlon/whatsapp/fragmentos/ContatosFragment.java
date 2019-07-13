package com.sharlon.whatsapp.fragmentos;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sharlon.whatsapp.ConversaActivity;
import com.sharlon.whatsapp.R;
import com.sharlon.whatsapp.firebase.ConfigFirebase;
import com.sharlon.whatsapp.modelos.Contatos;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ContatoAdapter adapter;
    private ListView lista;
    private ArrayList<Contatos> listaDeContatos;
    private ValueEventListener valueEventListenerContatos;


    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        lista = view.findViewById(R.id.listaContatos);
        listaDeContatos = new ArrayList<>();

        recuperarContatos();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                Contatos cont = listaDeContatos.get(i);

                intent.putExtra("nome", cont.getNome());
                intent.putExtra("numero", cont.getNumero());
                intent.putExtra("foto", "");
                intent.putExtra("historico", cont.getHistorico());

                startActivity(intent);
            }
        });


        return view;
    }

    private void recuperarContatos() {

        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listaDeContatos.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {

                    final Contatos c = dados.getValue(Contatos.class);
                    assert c != null;
                    listaDeContatos.add(c);

                }


//                adapter = new ArrayAdapter<>(
//                        Objects.requireNonNull(getActivity()),
//                        android.R.layout.simple_list_item_1,
//                        listaDeContatos
//                );

                adapter = new ContatoAdapter(getActivity(), listaDeContatos);

                lista.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        ConfigFirebase.getReferenciaBanco()
                .child(Objects.requireNonNull(ConfigFirebase.getUser().getPhoneNumber()))
                .child("Contatos")
                .addValueEventListener(valueEventListenerContatos);

    }

    @Override
    public void onStop() {
        super.onStop();
        ConfigFirebase.getReferenciaBanco()
                .child(Objects.requireNonNull(ConfigFirebase.getUser().getPhoneNumber()))
                .child("Contatos")
                .removeEventListener(valueEventListenerContatos);
    }
}
