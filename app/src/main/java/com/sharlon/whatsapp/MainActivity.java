package com.sharlon.whatsapp;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static void toast(Context context, String mensagem) {

        Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show();

    }
}
