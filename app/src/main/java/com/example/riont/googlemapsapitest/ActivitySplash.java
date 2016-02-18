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

/**
 * Created by riont on 01/01/16.
 */
public class ActivitySplash  extends AppCompatActivity {
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
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

    private void mostrarDialogo() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("¿Desea Continuar?");

        // set dialog message
        alertDialogBuilder
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


