package com.sharlon.whatsapp.fragmentos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharlon.whatsapp.R;
import com.sharlon.whatsapp.modelos.Contatos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ContatoAdapter extends ArrayAdapter<Contatos> {

    private ArrayList<Contatos> contatos;
    private Context context;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    ContatoAdapter(Context context, ArrayList<Contatos> objects) {
        super(context, 0, objects);

        this.contatos = objects;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {


        View view = new View(getContext());

        // verificar se a lista esta vazia
        if (!contatos.isEmpty()) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // montar a view apartir do xml
            assert inflater != null;
            view = inflater.inflate(R.layout.lista_contatos, parent, false);

            // recuperar o elemento para exibiçao
            TextView nomeContato = view.findViewById(R.id.txt_nome);
            TextView emailContato = view.findViewById(R.id.txt_numero);
            nomeContato.setText(contatos.get(position).getNome());
            emailContato.setText(contatos.get(position).getNumero());
            final ImageView imgPerfil = view.findViewById(R.id.imgContatoPerfil);

            try {

                StorageReference sto = storageReference.child(contatos.get(position).getNumero());

                final File localFile = File.createTempFile("images", "png");
                sto.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        imgPerfil.setImageBitmap(bitmap);

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

        return view;
    }

}
