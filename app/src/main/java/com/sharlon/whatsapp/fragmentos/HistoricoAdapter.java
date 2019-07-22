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
import com.sharlon.whatsapp.modelos.Historico;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HistoricoAdapter extends ArrayAdapter<Historico> {

    private ArrayList<Historico> historicos;
    private Context c;

    HistoricoAdapter(Context context, ArrayList<Historico> objects) {
        super(context, 0, objects);
        this.c = context;
        this.historicos = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = new View(getContext());

        if (!historicos.isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.lista_contatos, parent, false);

            TextView nome = view.findViewById(R.id.txt_nome);
            TextView ultimaMensagem = view.findViewById(R.id.txt_numero);

            Historico historico = historicos.get(position);
            nome.setText(historico.getNome());
            ultimaMensagem.setText(historico.getMensagem());

            final ImageView img = view.findViewById(R.id.imgContatoPerfil);

            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference sto = storage.getReference().child(historico.getIdUsuario());

                final File localFile = File.createTempFile("images", "png");
                sto.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        img.setImageBitmap(bitmap);

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
