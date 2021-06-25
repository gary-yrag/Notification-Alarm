package com.alamkanak.weekview.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by user on 03/12/2015.
 */
public class HiloNotificacion extends AsyncTask<Void, String,Void> {
    SQLiteDatabase db;
    JSONArray notificaciones_data;

    HiloNotificacion(SQLiteDatabase db, JSONArray notificaciones_data) {
        this.db = db;
        this.notificaciones_data = notificaciones_data;
    }

    public void injeccion(){
        for (int i = 0; i < notificaciones_data.length(); i++) {
            JSONObject rownot = null;
            try {
                rownot = notificaciones_data.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("INSERT INTO notificacion (id, titulo, descripcion, tipo, fecha, hora) " +
                        " VALUES ('" + rownot.getString("id") + "','" + rownot.getString("tiutlo") + "', '" + rownot.getString("descripcion") + "','" + rownot.getString("tipo") + "','" + rownot.getString("fecha") + "','" + rownot.getString("hora") + "')");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onProgressUpdate(String... values) {
        /*Aqui inicia*/
    }

    protected Void doInBackground(Void... arg0) {

        //new Thread(new Runnable() {
          //  public void run() {
                injeccion();
           // }
        //}).start();

        return null;
    }
}