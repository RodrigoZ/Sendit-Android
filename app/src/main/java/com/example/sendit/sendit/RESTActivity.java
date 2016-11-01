package com.example.sendit.sendit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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
                            t.setText(response.get(0).toString());
                        } catch(Exception e) {
                            t.setText("Excepcion");
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        TextView t = (TextView) findViewById(R.id.textView);
                        t.setText("Error. Bad URL");
                    }
                });
    }
}