package com.example.riont.googlemapsapitest;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Clase GoogleRuta la cual realiza operaciones en subprocesos para obtener la ruta optima
 * y calcular la distancia de la misma.
 * Created by JAD on 08/01/16.
 */
public class GoogleRuta {
    //<editor-fold desc="ATRIBUTOS">
    private float distancia;
    private GoogleMap map;
    private Boolean encontrado;

    //</editor-fold>
    //<editor-fold desc="CONSTRUCTORES">
    public GoogleRuta(GoogleMap map,String url) {
        this.map = map;
        this.distancia = 0.0f;
        this.encontrado = false;
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
    }
    //</editor-fold>
    //<editor-fold desc="CLASE PARA SUBPROCESOS">
    /**
     * Clase ReadTask que extiende de AsyncTask<...> para realizar el proceso peticion
     * HTTP de la Clase Ruta, En Cuanto el proceso termine,
     * se procede a decodificar el JSON obtenido por googleMaps.
     */
    private class ReadTask extends AsyncTask<String, Void, String> {
        /** Metodo que realiza la peticion HTTP en un subproceso(BackGround)
         * @param url String que contiene el url para la peticion HTTP.
         * @return un String con el resultado de la peticion HTTP.
         */
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        /** Metodo que es llamado luego que doInBackground termina sus operaciones
         * y este crea un objeto ParserTask para decodificar la data obtenida de la
         * peticion HTTP de la ruta.
         * @param result Un string con el resultado de la peticion HTTP.
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    /**
     * Clase ParserTask que extiende de AsyncTask<...> para realizar los procesos de
     * decodificacion del JSON obtenido por googleMaps, la cual se obtiene una Lista con
     * los datos obtenidos de GoogleMaps de la ruta solicitada y luego hacer los calculos
     * Correspodiente a la ruta.
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        /** Metodo que realiza realiza operaciones en un subproceso(BackGround) la cual,
         *  al decodificar el String jsonData lo convierte en una Lista para una posterior
         *  manipulacion.
         * @param jsonData un string obtenido por la peticion HTTP de googleMaps.
         * @return Una lista los datos de la ruta obtenidos por googleMaps.
         */
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        /** Metodo que es llamado luego de que el backGround termine sus operaciones, la cual
         * de la lista obtenida en background, se obtiene los puntos y se arma una lista
         * que representara la ruta total obtenida; ademas calcula la distancia total de la ruta
         * obtenida.
         * @param routes Lista de datos de la ruta.
         */
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            encontrado = false;
            distancia = 0.0f;
            ArrayList<LatLng> puntos = new ArrayList<>();
            PolylineOptions linea = new PolylineOptions();
            LatLng prePosition;
            float[] d = new float[1];
            List<HashMap<String, String>> path = routes.get(0);
            prePosition = obtenerPunto(path,0);
            puntos.add(prePosition);
            for (int j = 1; j < path.size(); j++) {
                LatLng position = obtenerPunto(path,j);
                puntos.add(position);
                Location.distanceBetween(prePosition.latitude,prePosition.longitude,
                        position.latitude,position.longitude,d);
                distancia = distancia + d[0];
                prePosition = position;
            }
            linea.addAll(puntos);
            linea.width(4);
            linea.color(Color.RED);
            distancia = distancia / 1000;
            map.addPolyline(linea);
            encontrado = true;
        }

        /** Metodo para obtener la ruta de la estructura principal de la clase ruta
         * @param path Una lista que contiene hashing de Strings
         * @param i la posicion a la cual se obtendra el punto
         * @return Retorna el punto en la posicion j de el Hashing que contiene la lista.
         */
        private LatLng obtenerPunto(List<HashMap<String, String>> path ,int i){
            HashMap<String, String> punto = path.get(i);
            double lat = Double.parseDouble(punto.get("lat"));
            double lng = Double.parseDouble(punto.get("lng"));
            return new LatLng(lat,lng);
        }
    }
    //</editor-fold>
    //<editor-fold desc="GETTERS Y SETTERS">
    public Boolean getEncontrado() {
        return encontrado;
    }

    public float getDistancia() {
        return distancia;
    }
    //</editor-fold>
}
