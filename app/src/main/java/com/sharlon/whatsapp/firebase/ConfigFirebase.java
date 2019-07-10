package com.sharlon.whatsapp.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sharlon.whatsapp.modelos.Usuario;

public class ConfigFirebase {

    private static FirebaseUser firebaseUser;

    public ConfigFirebase() {

    }

    public static FirebaseAuth getReference() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        return firebaseUser;
    }

    public static void setUser(FirebaseUser user) {
        firebaseUser = user;
    }

    public static DatabaseReference getReferenciaBanco() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static void updateUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(getUser().getUid());
        usuario.setNumero(getUser().getPhoneNumber());
        usuario.setNome(getUser().getDisplayName());
        salvarUsuarioBanco(usuario);
    }

    private static void salvarUsuarioBanco(Usuario u) {
        getReferenciaBanco().child(u.getNumero()).child("Dados").setValue(u);
        getReferenciaBanco().child(u.getNumero()).child("Conversas");
    }

    public static void logouf() {
        getReference().signOut();
    }

    public static Usuario getUsuarioAtual() {
        Usuario u = new Usuario();
        u.setId(getUser().getUid());
        u.setNumero(getUser().getPhoneNumber());
        u.setNome(getUser().getDisplayName());

        return u;
    }
}
