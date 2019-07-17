package com.sharlon.whatsapp.fragmentos;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sharlon.whatsapp.ConversaActivity;
import com.sharlon.whatsapp.MainActivity;
import com.sharlon.whatsapp.R;
import com.sharlon.whatsapp.firebase.ConfigFirebase;
import com.sharlon.whatsapp.modelos.Historico;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listaContatos;
    private ArrayList<Historico> listaDeHistoricos;
    private ValueEventListener valueEventListener;
    private ArrayAdapter<Historico> adapter;
    private DatabaseReference firebase;


    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        listaContatos = view.findViewById(R.id.list_Conversas);
        listaDeHistoricos = new ArrayList<>();
        adapter = new HistoricoAdapter(getActivity(), listaDeHistoricos);
        listaContatos.setAdapter(adapter);

        recuperarHistorico();

        listaContatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                Historico cont = listaDeHistoricos.get(i);

                intent.putExtra("nome", cont.getNome());
                intent.putExtra("numero", cont.getIdUsuario());
                intent.putExtra("foto", "");

                startActivity(intent);
            }
        });
        return view;
    }

    private void recuperarHistorico() {

        firebase = ConfigFirebase.getReferenciaBanco().child("Historico")
                .child(ConfigFirebase.getUser().getPhoneNumber());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listaDeHistoricos.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Historico historico = data.getValue(Historico.class);
                    listaDeHistoricos.add(historico);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                MainActivity.toast(getContext(), "ALGO DEU ERRADO");
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListener);
    }
}
