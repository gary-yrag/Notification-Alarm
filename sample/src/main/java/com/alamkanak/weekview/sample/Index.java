package com.alamkanak.weekview.sample;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
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

public class Index extends AppCompatActivity {
    ImageButton buttonGet;
    private ListView listView;
    NotificationManager nm;
    Notification notif;
    static String ns = Context.NOTIFICATION_SERVICE;

    ArrayAdapter lista_adapter;
    ListView lista_objeto;
    TextView nombres;
    volley_conect Volley_obj;
    JSONObject jsonObject = null;
    String sesion_ok = "false", notificaciones_ok = "false",id = "", identificacion = "", nombre1 = "", nombre2 = "", apellido1 = "", apellido2 = "",persona_id = "false",master_id = "false";
    JSONArray sesion_data = null, notificaciones_data=null;
    int bandera_valida = 0;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    dbHandler db;
    recursos format;//class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        format = new recursos();

        listView = (ListView) findViewById(R.id.listView);
        db = new dbHandler(this);

        try{
            enrutar();
        }catch (Exception w){
            Toast.makeText(Index.this,"Ocurrió un error: "+w.getMessage(),Toast.LENGTH_LONG).show();
        };

        buttonGet = (ImageButton) findViewById(R.id.imageButton);
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Actualizar();
                } catch (Exception e) {
                    Toast.makeText(Index.this, "Ocurrió un error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
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
                Intent intent = new Intent(Index.this, BasicActivity.class);//Agenda
                startActivity(intent);
                return true;

            case R.id.renew://New recordatorio
                try {
                    Calendar time = Calendar.getInstance();;
                    Intent ij = new Intent(Index.this, Agendashow.class);
                    ij.putExtra("id", "");
                    ij.putExtra("caso", "calnew");
                    ij.putExtra("fecha", String.format("%02d/%02d/%d", time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.MONTH) + 1, time.get(Calendar.YEAR)));
                    ij.putExtra("hora", String.format("%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)));
                    startActivity(ij);
                }
                catch (Exception e){
                    Toast.makeText(this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void lista_del(String selected){//Eliminar de la lista
        Intent ij = new Intent(this, Agendashow.class);
        ij.putExtra("id", selected);
        ij.putExtra("caso","caledit");
        startActivity(ij);
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
                Intent i = new Intent(Index.this, MainLogueo.class);
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

        String sql = "SELECT id,titulo, descripcion, tipo, strftime('%Y/%m/%d',fecha) AS fecha, strftime('%H:%M',hora) FROM noti WHERE estado=0";
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
                Lid[contador] = c.getString(0);//Id
                LTitulo[contador] = c.getString(1);
                LDescripcion[contador] = c.getString(2);
                LFecha[contador] = c.getString(4)+", a las "+format.hour12(c.getString(5));
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

            listview_adaptador adapter;
            adapter = new listview_adaptador(this,LTitulo,LDescripcion,LFecha,LIcono,Lid);

            lista_objeto.removeAllViewsInLayout();
            lista_objeto.setAdapter(adapter);

            lista_objeto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected = ((EditText) view.findViewById(R.id.idnoti)).getText().toString();
                    lista_del(selected);
                }
            });

            lista_objeto.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    String selected = ((EditText) view.findViewById(R.id.idnoti)).getText().toString();
                    show_delete(selected);//delete
                    return false;
                }
            });
        }
    }

    public void show_delete(String idsel){
        final String[] val = {idsel};
        final String idsel2 = idsel;

        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Está se seguro que desea eliminar el ítem seleccionado?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() { //si
            public void onClick(final DialogInterface dialogo1, int id) {
                if (idsel2 != "") {

                    String sql = "SELECT id,master_id FROM user";
                    Cursor c = db.onCursor(sql);

                    if (c.getCount() >= 1) {
                        if (c.moveToFirst()) {
                            persona_id = c.getString(0);
                            master_id = c.getString(1);
                        }
                    }

                    stringRequest = new StringRequest(Request.Method.POST, Volley_obj.JSON_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        jsonObject = new JSONObject(response);

                                        int ok = jsonObject.getInt("ok");
                                        switch (ok) {
                                            case 0:
                                                Toast.makeText(Index.this, "Ocurrió un error al intentar eliminar el recordatorio, intente nuevamente", Toast.LENGTH_LONG).show();
                                                break;
                                            case 1:
                                                db.onDelete("noti ", "id=?", val);//response delete
                                                Toast.makeText(Index.this, "Recordatorio eliminado satisfactoriamente", Toast.LENGTH_LONG).show();
                                                dialogo1.cancel();

                                                finish();
                                                Intent ijk = new Intent(Index.this, Index.class); //Redirreccionar aquí mismo
                                                startActivity(ijk);
                                                break;
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(Index.this, "Error json: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(Index.this, "Error de conexion.." + error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("master_id", master_id);
                            params.put("persona_id", persona_id);
                            params.put("age_id", idsel2);//cred
                            params.put("caso", "3");
                            return params;
                        }
                    };
                    requestQueue = Volley.newRequestQueue(Index.this);
                    requestQueue.add(stringRequest);
                } else {
                    Toast.makeText(Index.this, "No hay recordatorio para eliminar..", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {//No
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.cancel();
            }
        });

        dialogo1.show();
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
        Intent notificationIntent = new Intent(this, Index.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notif = new Notification.Builder(this).//Lvl 23
                setContentTitle("KILKAI " + titulo).
                setContentText(texto).
                setSmallIcon(R.drawable.ingana).setContentIntent(contentIntent).
                build();
    }

    public void Actualizar(){
        String sql = "SELECT id,master_id FROM user LIMIT 1";
        Cursor query_persona = db.onCursor(sql);
        if (query_persona.getCount() >= 1) {
            if (query_persona.moveToFirst()) {//
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
                                            //Toast.makeText(Index.this, ""+notificaciones_data, Toast.LENGTH_SHORT).show();
                                            bandera_valida = 1;
                                            try {
                                                sql_inject(notificaciones_data);
                                            }catch(Exception e){Toast.makeText(Index.this,"Ocurrió un error : "+e.getMessage(),Toast.LENGTH_SHORT).show();}
                                        }
                                        break;
                                    case "false":
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
                            Toast.makeText(Index.this, "Error de conexión", Toast.LENGTH_LONG).show();
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
        }
        else{
            bandera_valida = 0;
            Intent ijk = new Intent(this, MainLogueo.class);
            startActivity(ijk);
        }
    }
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
            values.put("fechaf",rownot.getString("fechaf"));
            values.put("horaf",rownot.getString("horaf"));
            values.put("allday",rownot.getString("allday"));
            values.put("notificarme",rownot.getString("notificarme"));
            values.put("estado",0);

            if(sql.getCount()>=1){//update
                db.onUpdate("noti", "id=?", new String[]{String.valueOf(rownot.getString("id"))}, values);
            }else{//insert
                values.put("id",rownot.getString("id")); //id noti
                db.onInsert("noti",values);
            }
        }
        lista_inflarAvanzado();
    }//Injeccion sql
}
