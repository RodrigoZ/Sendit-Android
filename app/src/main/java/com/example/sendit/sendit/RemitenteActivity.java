package com.example.sendit.sendit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RemitenteActivity extends AppCompatActivity {

    private ExpandableListView expListView;
    private ListAdapterExpandable adapter;
    public String child;
    public ArrayList<String> enviosArrayList;
    public Button myButton;

    // declare array List for all headers in list
    ArrayList<String> headersArrayList = new ArrayList<String>();

    // Declare Hash map for all headers and their corresponding values
    HashMap<String, ArrayList<String>> childArrayList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remitente);

        expListView = (ExpandableListView) findViewById(R.id.expListView);

        //add headers values
        //acá voy a agregar los headers, q serán "Envio x"
        headersArrayList.add("Envio");

        //add child content
        //acá agrego toda la info de ese envio
        enviosArrayList  = new ArrayList<>();

        enviosArrayList.add("Hola");
        childArrayList.put("Envio", enviosArrayList);

        //TODO: una clase q maneje esto!
        //hashmap, key, en el map pongo el objeto (Envio) con un millon de datos, key envio.get.nosequenoseqe
        //cuando hagan click, child.envio
        AndroidNetworking.get("https://api.mercadolibre.com/sites")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        try {
                            int length = response.length();
                            System.out.println("On response");
                            for (int i = 0; i < length; i++)
                            {
                                child = response.get(i).toString();
                                addChild(child);
                            }
                        } catch(Exception e) {
                            child = "Excepcion";
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        child = "Error";
                    }
                });
        System.out.println("Fuera" + childArrayList.toString());
        System.out.println("Child" + child);
        System.out.println("Envios Array" + enviosArrayList);

        // declare adapter
        adapter = new ListAdapterExpandable(this, headersArrayList, childArrayList);
        expListView.setAdapter(adapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id){

                //TODO
                //obtendria la direccion y la guardaria en una variable global para luego mostrarla en la siguiente activity
                //o en la siguiente activity podría pedir la direccion de "x" envio y q me la devuelvan... VER!!
                final String selected = (String) adapter.getChild(groupPosition, childPosition);
                System.out.println("Selected" + selected);

                Intent intent = new Intent(RemitenteActivity.this, EnvioActivity.class);
                startActivity(intent);

                return false;
            }
        });

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // TODO: Do your stuff
                return false;
            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                // TODO: Do your stuff
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                // TODO: Do your stuff
            }
        });
    }

    public void addChild(String child)
    {
        enviosArrayList.add(child);
        childArrayList.put("Envio", enviosArrayList);
    }
}
