package com.example.riont.googlemapsapitest;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** Clase HttpConnection, la cual se encarga de hacer una conexion HTTP por medio
 * del metodo readUrl este necesita un String que reprsentaria un URL, google Maps
 * proporciona una direccion URL predefinida para las peticiones HTTP para la app,
 * la cual en ella debe estar debe agregarse el punto LatLng de salida
 * y de destino para que este pueda proporcionar un JSONObject.
 * Created by riont on 07/01/16.
 */
public class HttpConnection {

    public HttpConnection(){

    }

    public String readUrl(String mapsApiDirectionsUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(mapsApiDirectionsUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception reading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}