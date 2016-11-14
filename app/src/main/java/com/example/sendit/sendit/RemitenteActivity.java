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
                myButton = new Button(getApplicationContext());
                myButton.setText("ACEPTAR");

                RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_remitente);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //estara bien así? habrá otra forma? por lo visto no...
                lp.setMargins(30,420,30,0);
                rl.addView(myButton, lp);

                myButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(RemitenteActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }
                });
                return false;
            }
        });
/*
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // TODO: Do your stuff
                myButton = new Button(getApplicationContext());
                myButton.setText("ACEPTAR");

                RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_remitente);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //estara bien así? habrá otra forma? por lo visto no...
                lp.setMargins(30,420,30,0);
                rl.addView(myButton, lp);

                myButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(RemitenteActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }
                });

                return false;
            }
        });
*/
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
