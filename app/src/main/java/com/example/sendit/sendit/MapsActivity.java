package com.example.sendit.sendit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;

import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

// TODO: 04/10/16 Cuando entro a la app, desde el boton del Main, y no tengo activada la localizacion, no me pide q tengo q activarla para poder usar la app 

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    protected GoogleMap mMap;
    protected SupportMapFragment mapFrag;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mLastLocation;
    protected String serverKey =  "AIzaSyBKRPo_18MeR5tM7MkrKOxpvkB6zpP5g10"; //es la key de Directions
    protected static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        buildGoogleApiClient();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
        //mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    //Runs when a GoogleApiClient object successfully connects.
    @Override
    public void onConnected(Bundle connectionHint) { //el onconnected se ejecuta una vez (creo) por eso no me está actualizando los datos en el onLocationChanged
        /*mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/
        startLocationUpdates();
    }

    protected void startLocationUpdates(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true); // While enabled and the location is available, the my-location layer continuously draws an indication of a user's current location and bearing
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    //TODO: deberia tener un proceso en background que le esté mandando al servidor la posicion actual del tipo (LocationSender)
    @Override
    public void onLocationChanged (Location location){
        float [] results = new float[1];
        mLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        LocationSender(mLastLocation);

        //TODO: obtener el destino del envio usando REST, por el momento uno al azar.
        LatLng destino = new LatLng(location.getLatitude() + 0.1, location.getLongitude() + 0.1);
        LatLng origen = new LatLng(location.getLatitude(), location.getLongitude());
        GoogleDirection.withServerKey(serverKey)
                .from(origen)
                .to(destino)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            //Revisar bien el tema del Context! (http://stackoverflow.com/questions/4721626/how-to-get-the-current-context) <--- ESTABA BIEN!
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED);
                            mMap.addPolyline(polylineOptions);
                        } else {
                            System.out.println("Error, Direccion invalida");
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });

        Location.distanceBetween(origen.latitude, origen.longitude, destino.latitude, destino.longitude, results);
       // Location.distanceBetween(-45.4, -63.4, -45.3, -63.5, results);
        System.out.println("Resultado: "+ results[0]); //los results están en metros

        if (results[0] < 300){
            Button myButton = new Button(getApplicationContext());
            myButton.setText("RECEPCION ENVIO");

            LinearLayout ll = (LinearLayout) findViewById(R.id.activity_maps);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //estara bien así? habrá otra forma? por lo visto no...
            lp.setMargins(30,420,30,0);
            ll.addView(myButton, lp);

            myButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){
                    Toast.makeText(getApplicationContext(), "Envio en posesion", Toast.LENGTH_LONG).show();
                }
            });
        }

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    //Clase para mandarle la localizacion al servidor
    //Por el momento, me está enviando los datos una sola vez...
    public void LocationSender(Location location){
        mLastLocation = location;
        double latitude =  mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();

        //TODO: modificar la URL. Este metodo "PUT" requiere algo más? solamente envio?
        AndroidNetworking.put("laapisendit.com/localicacion")
                .addBodyParameter("Latitud", Double.toString(latitude))
                .addBodyParameter("Longitud", Double.toString(longitude))
                .setPriority(Priority.MEDIUM)
                .build();

        Toast.makeText(getApplicationContext(), "Latitud: " + latitude, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "Longitude: " + longitude, Toast.LENGTH_LONG).show();
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //TODO:
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //(just doing it here for now, note that with this code, no explanation is shown)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
