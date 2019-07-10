package com.sharlon.whatsapp.Autenticacao;

import android.util.Base64;

public class Criptografia {

    public static String criptografar(String texto) {

        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("\\r", "");

    }

    public static String descriptografar(String texto) {

        return new String(Base64.decode(texto, Base64.DEFAULT));

    }

}
