package com.alamkanak.weekview.sample;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //public static final String JSON_URL = "http://simplifiedcoding.16mb.com/UserRegistration/json.php";
    private ImageButton buttonGet;

    private ListView listView;

    // Variables de la notificacion
    NotificationManager nm;
    Notification notif;
    static String ns = Context.NOTIFICATION_SERVICE;
    //Defino los iconos de la notificacion en la barra de notificacion
    dbHandler db;

    ArrayAdapter lista_adapter;
    ListView lista_objeto;

    TextView nombres;

    /*Actualizar init*/
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
    /*Actualizar finish*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);// Habilitar icono
        getSupportActionBar().setIcon(R.drawable.ingana);// Usar el drawable deseado

        buttonGet = (ImageButton) findViewById(R.id.buttonGet);
        buttonGet.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);

        db = new dbHandler(this);

        receiver();
        receivernubeboot();
        enrutar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notif, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.ag:
                /*Intent i = new Intent(this, Agenda.class);
                startActivity(i);*/
                Intent intent = new Intent(MainActivity.this, BasicActivity.class);//Agenda
                startActivity(intent);
                return true;
        }
            return super.onOptionsItemSelected(item);
    }

    public void lista_del(String selected){//Eliminar de la lista
        Toast.makeText(this, "" + selected, Toast.LENGTH_LONG).show();
    }

    public void enrutar() {
        if (db != null) {
            String sql = "SELECT id,identificacion,nombre1,nombre2,apellido1,apellido2 FROM user";
            Cursor c = db.onCursor(sql);

            if (c.getCount() >= 1) {
                String sql2 = "SELECT id FROM noti";
                Cursor sql_notificacion = db.onCursor(sql2);

                if(sql_notificacion.getCount() >= 1){
                    show_persona();
                    lista_inflarAvanzado();
                }
                else{
                    show_persona();
                }
            }
            else{
              Intent i = new Intent(this, MainLogueo.class);
                //i.putExtra("direccion", et1.getText().toString());
              startActivity(i);
            }
        }
    }

    public void show_persona(){
        nombres = (TextView)findViewById(R.id.Obj_nombres);
        String sql = "SELECT id,identificacion,nombre1,nombre2,apellido1,apellido2 FROM user LIMIT 1";
        Cursor c = db.onCursor(sql);

        if(c.moveToFirst()){
            String Nombres = c.getString(2)+" "+c.getString(3);
            String Apellidos = c.getString(4)+" "+c.getString(5);
            nombres.setText(Nombres+" "+Apellidos);
        }
    }

    public void lista_inflarAvanzado(){
        int IC_academico = R.drawable.academico;
        int IC_informativo = R.drawable.informativo;
        int IC_monetario = R.drawable.monetario;
        int IC_agenda = R.drawable.agenda;

        String [] LTitulo,LDescripcion,LFecha,Lid;
        Integer [] LIcono;

        String sql = "SELECT id,titulo, descripcion, tipo, strftime('%Y/%m/%d',fecha) AS fecha, hora FROM noti";
        Cursor c = db.onCursor(sql);

        LTitulo = new String[c.getCount()];
        LDescripcion = new String[c.getCount()];
        LIcono = new Integer[c.getCount()];
        LFecha = new String[c.getCount()];
        Lid = new String[c.getCount()];

        String[] lista_data = new String[c.getCount()];
        int contador = 0;

        if (c.moveToFirst()) {
            do {
                LTitulo[contador] = c.getString(1);
                LDescripcion[contador] = c.getString(2);
                LFecha[contador] = c.getString(4)+", a las "+c.getString(5);
                Lid[contador] = c.getString(0);
                switch (c.getString(3)){
                    case "1":
                        LIcono[contador] = IC_academico;
                        break;
                    case "2":
                        LIcono[contador] = IC_monetario;
                        break;
                    case "3":
                        LIcono[contador] = IC_informativo;
                        break;
                    case "4":
                        LIcono[contador] = IC_agenda;
                        break;
                }
                contador++;
            } while(c.moveToNext());

            lista_objeto = (ListView) findViewById(R.id.listView);
            listview_adaptador adapter=new listview_adaptador(this,LTitulo,LDescripcion,LFecha,LIcono,Lid);
            lista_objeto.removeAllViewsInLayout();
            lista_objeto.setAdapter(adapter);

            lista_objeto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected = ((EditText) view.findViewById(R.id.idnoti)).getText().toString();
                    lista_del(selected);
                }
            });
        }
    }

    private void showJSON(String json){
        ParseJSON pj = new ParseJSON(json);
        pj.parseJSON();
        CustomList cl = new CustomList(this, ParseJSON.ids,ParseJSON.names,ParseJSON.emails);
        listView.setAdapter(cl);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void notificacion(int icon, CharSequence textoEstado, CharSequence titulo, CharSequence texto) {
        // Capturo la hora del evento
        long hora = System.currentTimeMillis();
        // Definimos la accion de la pulsacion sobre la notificacion (esto es opcional)
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        /*Lvl 23*/
        notif = new Notification.Builder(this).
                setContentTitle("Kilkai " + titulo).
                setContentText(texto).
                setSmallIcon(R.drawable.ic_launcher).setContentIntent(contentIntent).
                build();

        /*Lvl 23*/
    }
    @Override
    public void onClick(View v) {
        //Actualizar
        Toast.makeText(this, "Actualizado..", Toast.LENGTH_LONG).show();
        Actualizar();
    }

    public void Actualizar(){
        String sql = "SELECT id,master_id FROM user";
        Cursor query_persona = db.onCursor(sql);
        if (query_persona.getCount() >= 1) {
            if (query_persona.moveToFirst()) {
                persona_id = query_persona.getString(0);
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
                                sesion_ok = jsonObject.getString("sesion_ok");
                                notificaciones_ok  = jsonObject.getString("notificaciones_ok");
                                switch (sesion_ok) {
                                    case "true":
                                        if(notificaciones_ok == "true"){
                                            notificaciones_data = jsonObject.getJSONArray("notificaciones_data");
                                            bandera_valida = 1;
                                            sql_inject(notificaciones_data);
                                        }
                                        break;
                                    case "false": //credenciales erroneas
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
                            // Toast.makeText(context.getApplicationContext(), "Error de conexion..", Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("master_id",master_id);
                    params.put("persona_id",persona_id);
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        /*Conexion nube - Sincronizacion finish*/
        }
        else{
            bandera_valida = 0;
            Intent ijk = new Intent(this, MainLogueo.class);
            startActivity(ijk);
        }
    }

    /*Injeccion sql db init*/
    public void sql_inject(JSONArray NOTIFICACIONES) throws JSONException {

        requestQueue.stop();

        for (int i = 0; i < NOTIFICACIONES.length(); i++) {
            JSONObject rownot = NOTIFICACIONES.getJSONObject(i);

            String str = "SELECT id FROM noti WHERE id='"+rownot.getString("id")+"' LIMIT 1";
            Cursor sql = db.onCursor(str);

            ContentValues values = new ContentValues();
            values.put("titulo",rownot.getString("titulo"));
            values.put("descripcion",rownot.getString("descripcion"));
            values.put("tipo",rownot.getString("tipo"));
            values.put("fecha",rownot.getString("fecha"));
            values.put("hora",rownot.getString("hora"));

            switch (sql.getCount()){
                case 1:
                    db.onUpdate("noti", "id=?", new String[]{String.valueOf(rownot.getString("id"))}, values);
                    break;
                case 0:
                    values.put("id",rownot.getString("id")); //id noti
                    db.onInsert("noti",values);
                    break;
            }
        }
        lista_inflarAvanzado();
    }//onjeccion finish

    public void receiver(){
        try {
            Long time = new GregorianCalendar().getTimeInMillis();
            Intent intentAlarm = new Intent(this, ReceiverBoot.class);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Calendar cal = Calendar.getInstance();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10000, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        }
        catch(Exception e){
            e.getMessage();
        }
    }

    public void receivernubeboot(){//record service..
        try {
            Long time2 = new GregorianCalendar().getTimeInMillis()+1000;
            Intent intentAlarm2 = new Intent(this, ReceiverBootNube.class);
            AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Calendar cal2 = Calendar.getInstance();
            alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), 10000 * 4, PendingIntent.getBroadcast(this, 1, intentAlarm2, PendingIntent.FLAG_UPDATE_CURRENT));
        }catch (Exception e){
            e.getMessage();
            //Toast.makeText(this,"Fatal error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private class BackgroundTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            return null;
        }
    }
}

