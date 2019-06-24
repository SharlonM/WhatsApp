package com.sharlon.whatsapp.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sharlon.whatsapp.modelos.Usuario;

public class ConfigFirebase {

    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference databaseReference;
    private static FirebaseUser firebaseUser;

    public ConfigFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        updateUsuario();
    }

    public static FirebaseAuth getReference() {
        return FirebaseAuth.getInstance();
    }

    public static void setUser(FirebaseUser user) {
        firebaseUser = user;
    }

    public static FirebaseUser getUser() {
        return firebaseUser;
    }

    public static DatabaseReference getReferenciaBanco() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference;
    }

    public static void updateUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(getUser().getUid());
        usuario.setNumero(getUser().getPhoneNumber());
        usuario.setNome(getUser().getDisplayName());
        salvarUsuarioBanco(usuario);
    }

    public static void salvarUsuarioBanco(Usuario u) {
        getReferenciaBanco().child(u.getNumero()).child("Dados").setValue(u);
        getReferenciaBanco().child(u.getNumero()).child("Conversas");
    }

    public static void logouf() {
        getReference().signOut();
    }
}
