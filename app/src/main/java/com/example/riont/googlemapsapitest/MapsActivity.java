package com.example.riont.googlemapsapitest;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
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

    /** Metodo para realizar operaciones Antes
     * de que la vista del Activity sea creada.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /** Metodo donde trabaja con los puntos de salida y llega para convertir
     *  en un String url y ser usado en una peticion HTTP.
     * @return Un String la cual representara el url
     */
    private String getMapsApiDirectionsUrl() {
        String waypoints = "origin=" +puntoSalida.latitude + "," + puntoSalida.longitude
                + "&destination=" +puntoLlegada.latitude + "," + puntoLlegada.longitude;
        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    /** Metodo para realizar operaciones sobre el Mapa de Google.
     * @param googleMap Instancia del objeto googleMap ya creado por defecto,
     *                  para luego ser manipulado.
     */
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
                        SimpleDialogo dialog = new SimpleDialogo();
                        dialog.setTitulo("Aparentemente actualmente no posee conexion a internet");
                        dialog.show(getFragmentManager(), "SimpleDialog");
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

    /** Meotodo para verificar la conexion internet del dispositivo.
     * @return true si se realizo una correcta peticion de paquetes datos
     * y false si no se logro realizar la peticion.
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /** Metodo para respuesta a la pulsacion del boton positivo de un dialogo que
     * pasa por parametro.
     * @param dialog Un dialogFragment donde se manipulara el boton positivo.
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        map.clear();
        puntoSalida = null;
        puntoLlegada = null;
    }

    /** Metodo para respusta a la pulsacion del boton negativo de un dialogo que se
     * pasa por parametro.
     * @param dialog Un dialogo Fragment donde se manipulara el boton negativo.
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        MapsActivity.this.finish();
    }

    /**
     * Metodo la cual luego de que se tengan los puntos de salida y de llegada,
     * realiza la peticion por medio un Objeto GoogleRuta y muestra el total a
     * pagar.
     */
    public void buscarRuta(){
        Snackbar.make(findViewById(R.id.map), "Buscando Ruta",
                Snackbar.LENGTH_SHORT).show();
        Thread task = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    String url = getMapsApiDirectionsUrl();
                    GoogleRuta ruta = new GoogleRuta(map, url);
                    while(!ruta.getEncontrado()){
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    SimpleDialogo sp = new SimpleDialogo();
                    float totalPagar = ruta.getDistancia() *50;
                    sp.setTitulo("Total a Pagar "+ Math.round(totalPagar)+"Bsf");
                    sp.show(getFragmentManager(), "DialogPagar");
                }
            }
        };
        task.start();
    }
}
