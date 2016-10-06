package com.example.sendit.sendit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;

public class RESTActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        AndroidNetworking.initialize(getApplicationContext());
    }

    //Definir estos metodos en una clase aparte

    public void testAPI(View view){

        AndroidNetworking.get("https://api.mercadolibre.com/sites")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        TextView t = (TextView) findViewById(R.id.textView);
                        try {
                            t.append(response.get(0).toString());
                        } catch(Exception e) {
                            t.append("Excepcion");
                        }
                        
                        //print
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        TextView t = (TextView) findViewById(R.id.textView);
                        t.append("Error");
                    }
                });

    }
}
