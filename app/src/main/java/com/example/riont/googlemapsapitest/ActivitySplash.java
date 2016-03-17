package com.example.riont.googlemapsapitest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/** Clase ActivitySplash interfaz grafica encargada de la presentacion inicial
 * de la app, este comprueba si el dispositivo posee internet al momento de inicial la app
 * en dado caso de que no tenga, es notificado el usario de esto, y caso de que si
 * muestra una imagen cargada en el proyecto con una duracion de 3 seg. Luego pasa
 * a la interfaz de Mapa.
 * Created by JAD on 01/01/16.
 */
public class ActivitySplash  extends AppCompatActivity {
    final Context context = this;

    /** Metodo para realizar operaciones antes de que la vista
     * del activity se muestre en pantalla.
     * @param savedInstanceState objeto que para verificar si el activity
     * a sido instanciado.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                ,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        if(savedInstanceState == null){
            cargarMapa();
        }
    }

    /** Metodo cargarMapa, encardado de verificar si tiene conexion en dispositivo
     * con IsOnline dependiendo de las respuesta de este metodo notifica al usuario
     * si el dispositivo tienen conexion espera 3 segundo y se instancia la clase
     * MapsActivity.
     */
    private void cargarMapa(){
        if(isOnline()){
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Intent intent = new Intent(ActivitySplash.this, MapsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
            timerThread.start();
        }else{
            mostrarDialogo();
        }
    }

    /** Metodo para mostrar dialogo con botones y mensaje predefinido.
     *  este notifica al usuario la falta de conexion a internet y dependiendo que responda
     *  el usuario este puede cerrar la app o continuar, Ademas dialogo no puede ser Cancelado.
     */
    private void mostrarDialogo() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("¿Desea Continuar?");

        // set dialog message
        alertDialogBuilder
                .setTitle("¿Desea Salir?")
                .setMessage("Es necesario que este conectado a internet para continuar")
                .setIcon(R.drawable.cast_ic_notification_0)
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Si es precionado el dialogo se cierra y continua el proceso
                        // the dialog box and do nothing
                        dialog.cancel();
                        Intent intent = new Intent(ActivitySplash.this, MapsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        ActivitySplash.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // Mostrar el Dialogo.
        alertDialog.show();

    }

    /** Metodo para verificar si el dispositivo posee conexion a internet,
     * sea por wifi o megas.
     * @return boolean representando si posee conexion o no en el instante que fue llamado
     * el metodo.
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
}


