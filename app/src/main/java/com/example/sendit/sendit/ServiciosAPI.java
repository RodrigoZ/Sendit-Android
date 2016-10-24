package com.example.sendit.sendit;

import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;

/**
 * Created by Rodri on 11/10/16.
 */
//mandarle jsons (ver si manejandome con jsons se puede)
    //no procesar respuestas
    //podria crearlo con objetos, (wrappeers de la respeusta del servicio)
public class ServiciosAPI{
    public void testAPI(final View view){

        //AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("https://api.mercadolibre.com/sites")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        TextView t = (TextView) view.findViewById(R.id.textView);
                        try {
                            t.setText(response.get(0).toString());
                        } catch(Exception e) {
                            t.append("Excepcion");
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        TextView t = (TextView) view.findViewById(R.id.textView);
                        t.append("Error");
                    }
                });
    }
}
