package com.example.riont.googlemapsapitest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/** Clase para mostrar Dialogo estandar en un FragmentActivity
 * Created by riont on 15/02/16.
 */
public class SimpleDialogo extends DialogFragment {

    private String titulo;
    private String msj;
    private AlertDialog.Builder builder;

    /**
     * La clase SimpleDialogo implementa un una interfaz, para cuando se
     * implemente la clase SimpleDialogo esta oblige al programador utilizar
     * los dos metodos que representarian las acciones de el dialogo.
     */
    public interface SimpleDialogoListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    SimpleDialogoListener mListener;

    /**
     * Constructor por defecto inicializa sus atributos.
     */
    public SimpleDialogo() {
        titulo = "";
        msj = "";
    }


    /** Metodo que al usar la clase SimpleDialogo esta obliga a implementar
     * el contenido de mListener.
     * @param activity El activity que implementa la clase.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SimpleDialogoListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    /** Metodo para realizar aplicar las configuraciones necesarias para el dialogo.
     * @param savedInstanceState para saber si el objeto esta instanciado o no.
     * @return un dialogo ya creado con las configuraciones asignadas dentro del metodo.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(titulo)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(SimpleDialogo.this);
                    }
                });
        return builder.create();
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setMsj(String msj) {
        this.msj = msj;
    }
}
