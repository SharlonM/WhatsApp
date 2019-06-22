package com.sharlon.whatsapp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

class Permissao {

    static void validaPermisoes(Activity activity, String[] permisoes) {

        String[] validar = new String[permisoes.length];
        int contador = 0;

        if (Build.VERSION.SDK_INT >= 23) {

            for (String permissao : permisoes) {

                if (ContextCompat.checkSelfPermission(activity, permissao) != PackageManager.PERMISSION_GRANTED) {

                    validar[contador] = permissao;
                    contador++;

                }

            }

            ActivityCompat.requestPermissions(activity, validar, 1);

        }
    }

}
