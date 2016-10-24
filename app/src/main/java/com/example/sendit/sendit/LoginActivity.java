package com.example.sendit.sendit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
//Nico me envia el token!

    public void Login(){ //Deberia enviar los datos por REST?
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createAnUser")
                .addBodyParameter("firstname", "Amit")
                .addBodyParameter("lastname", "Shekhar")
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
}
