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
 * Created by riont on 08/01/16.
 */
public class GoogleRuta {
    //<editor-fold desc="ATRIBUTOS">
    private GoogleMap map;
    private Context app;
    private float distancia;
    //</editor-fold>
    //<editor-fold desc="CONSTRUCTORES">
    public GoogleRuta(GoogleMap map, Context app, String url) {
        this.map = map;
        this.app = app;
        this.distancia = 0.0f;
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
    }
    //</editor-fold>
    //<editor-fold desc="METODOS">
    private class ReadTask extends AsyncTask<String, Void, String> {
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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

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

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points;
            PolylineOptions polyLineOptions = null;
            // traversing through routes
            LatLng prePosition;
            float[] d = new float[1];
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);
                HashMap<String, String> point = path.get(0);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                prePosition = new LatLng(lat,lng);
                points.add(prePosition);
                for (int j = 1; j < path.size(); j++) {
                    point = path.get(j);
                    lat = Double.parseDouble(point.get("lat"));
                    lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                    Location.distanceBetween(prePosition.latitude,prePosition.longitude,
                            position.latitude,position.longitude,d);
                    distancia = distancia + d[0];
                    prePosition = position;
                }
                polyLineOptions.addAll(points);
                polyLineOptions.width(4);
                polyLineOptions.color(Color.BLUE);
            }
            map.addPolyline(polyLineOptions);
            //Google nos proporciona las distacias en metros por ello la pasamos a KM
            distancia = distancia / 1000;
        }
    }
    //</editor-fold>
    //<editor-fold desc="GETTERS Y SETTERS">
    public float getDistancia(){
        return distancia;
    }
    //</editor-fold>
}
