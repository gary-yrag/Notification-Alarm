package com.alamkanak.weekview.sample;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateFormat;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by user on 04/12/2015.
 */
public class ReceiverBootNube extends BroadcastReceiver {
    dbHandler db;
    volley_conect Volley_obj;
    JSONObject jsonObject = null;
    String sesion_ok = "false", notificaciones_ok = "false";
    JSONArray sesion_data = null, notificaciones_data=null;

    String id = "", identificacion = "", nombre1 = "", nombre2 = "", apellido1 = "", apellido2 = "";
    String persona_id = "false";
    String master_id = "false";
    int bandera_valida = 0;

    StringRequest stringRequest;
    RequestQueue requestQueue;
    Context contexto;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@Override
    public void onReceive(final Context context, Intent intent)
    {
        contexto = context;
        db = new dbHandler(context);
        String str = "SELECT id,master_id FROM user";
        Cursor query_persona = db.onCursor(str);

        receiver();

        if (query_persona.getCount() >= 1) {
        /*Conexion nube - Sincronizacion init*/
            if (query_persona.moveToFirst()) {
                persona_id= query_persona.getString(0);
                master_id = query_persona.getString(1);
            }
        stringRequest = new StringRequest(Request.Method.POST, Volley_obj.JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            //Toast.makeText(context,"ReceiverBootNube: "+jsonObject,Toast.LENGTH_LONG).show();
                            sesion_ok = jsonObject.getString("sesion_ok");
                            notificaciones_ok  = jsonObject.getString("notificaciones_ok");
                            switch (sesion_ok) {
                                case "true":
                                    if(notificaciones_ok == "true"){
                                        notificaciones_data = jsonObject.getJSONArray("notificaciones_data");
                                        bandera_valida = 1;
                                        requestQueue.stop();
                                        try {
                                            sql_inject(notificaciones_data);
                                        }
                                        catch (Exception e){
                                            Toast.makeText(contexto,"Ocurrió un error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    break;
                                case "false": //Credenciales erroneas
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(context,"Verifique conectividad: Si el problema persiste, comuniquese con la institución.",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("persona_id",persona_id);
                params.put("master_id",master_id);
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        }
        else{
            bandera_valida = 0;
        }
    }

    public void sql_inject(JSONArray NOTIFICACIONES) throws JSONException { //Injeccion SQL
        try{
            db = new dbHandler(contexto);
            for (int i = 0; i < NOTIFICACIONES.length(); i++) {
                JSONObject rownot = NOTIFICACIONES.getJSONObject(i);

                String str = "SELECT id FROM noti WHERE id='" + rownot.getString("id") + "' LIMIT 1";
                Cursor sql = db.onCursor(str);

                ContentValues values = new ContentValues();
                values.put("titulo", rownot.getString("titulo"));
                values.put("descripcion", rownot.getString("descripcion"));
                values.put("tipo", rownot.getString("tipo"));
                values.put("fecha", rownot.getString("fecha"));
                values.put("hora", rownot.getString("hora"));
                values.put("fechaf",rownot.getString("fechaf"));
                values.put("horaf",rownot.getString("horaf"));
                values.put("allday",rownot.getString("allday"));
                values.put("notificarme",rownot.getString("notificarme"));
                values.put("estado", 0);

                switch (sql.getCount()) {
                    case 1:
                        db.onUpdate("noti", "id=?", new String[]{String.valueOf(rownot.getString("id"))}, values);
                        break;
                    case 0:
                        values.put("id", rownot.getString("id")); //id noti
                        db.onInsert("noti", values);
                        break;
                }
            }
        }catch(Exception e){
            Toast.makeText(contexto, "Ocurrió un error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void receiver(){
        Long time = new GregorianCalendar().getTimeInMillis()+1000;
        Intent intentAlarm = new Intent(contexto, ReceiverBoot.class);
        AlarmManager alarmManager = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10000, PendingIntent.getBroadcast(contexto, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}