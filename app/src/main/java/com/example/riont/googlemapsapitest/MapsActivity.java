package com.example.riont.googlemapsapitest;

import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,SimpleDialogo.SimpleDialogoListener {

    //<editor-fold desc="ATRIBUTOS">
    private GoogleMap map;
    private LatLng puntoLlegada;
    private  LatLng puntoSalida;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private String getMapsApiDirectionsUrl() {
        String waypoints = "origin=" +puntoSalida.latitude + "," + puntoSalida.longitude
                + "&destination=" +puntoLlegada.latitude + "," + puntoLlegada.longitude;
        String key ="key=AIzaSyDHmEgoFsAAi8zA6uuoiKNqUE2Xr0HL4A8";
        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (puntoSalida == null) {
                    puntoSalida = latLng;
                    map.addMarker(new MarkerOptions().position(latLng));
                }
                else if (puntoLlegada == null && puntoSalida != null) {
                    puntoLlegada = latLng;
                    map.addMarker(new MarkerOptions().position(latLng));
                    if(isOnline()){
                        buscarRuta();
                    }else{
                        showDialog("Aparentemente actualmente no posee conexion a internet");
                    }
                }
        }
    });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();
                puntoSalida = null;
                puntoLlegada = null;
            }
        });
        map.setMyLocationEnabled(true);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    public void showDialog(String titulo) {
        // Create an instance of the dialog fragment and show it
        SimpleDialogo dialog = new SimpleDialogo();
        dialog.setTitulo(titulo);
        dialog.show(getFragmentManager(), "SimpleDialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        buscarRuta();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        MapsActivity.this.finish();
    }
    public void buscarRuta(){
        Snackbar.make(findViewById(R.id.map), "Buscando Ruta",
                Snackbar.LENGTH_SHORT).show();
        Thread task = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(Snackbar.LENGTH_LONG);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    String url = getMapsApiDirectionsUrl();
                    GoogleRuta ruta = new GoogleRuta(map, getApplication(), url);
                }
            }
        };
        task.start();
    }
}
