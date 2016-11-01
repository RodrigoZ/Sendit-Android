package com.example.sendit.sendit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void openMaps(View view){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }
    public void openREST(View view){
        Intent intent = new Intent(MainActivity.this, RESTActivity.class);
        startActivity(intent);
    }

    public void openLogin(View view){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void openRemitente(View view){
        Intent intent = new Intent(MainActivity.this, RemitenteActivity.class);
        startActivity(intent);
    }
}