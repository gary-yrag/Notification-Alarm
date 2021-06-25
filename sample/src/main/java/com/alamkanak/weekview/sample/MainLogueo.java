package com.alamkanak.weekview.sample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
/*import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;*/
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainLogueo extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty(message = "Escriba el nombre de usuario")
    EditText LOG_USUARIO;
    @NotEmpty(message = "Escriba la contraseña")
    EditText lOG_CONTRASENIA;
    Button LOG_BOTTON;
    volley_conect Volley_obj;

    dbHandler db;
    int bandera_valida = 0;

    JSONObject jsonObject = null;
    String sesion_ok = "false", notificaciones_ok = "false";
    JSONArray sesion_data = null, notificaciones_data=null;

    String id = "",master_id="", identificacion = "", nombre1 = "", nombre2 = "", apellido1 = "", apellido2 = "";

    Intent myvar;
    RequestQueue requestQueue;

    TextView TVinfo;
    WebView Wtexto;

    ImageView img;
    AnimationDrawable frameAnimation;

    Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logueo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        img = (ImageView)findViewById(R.id.animation);
        img.setBackgroundResource(R.drawable.animation);

        frameAnimation = (AnimationDrawable) img.getBackground();

        // Habilitar icono
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Usas el drawable que deseas
//        getSupportActionBar().setIcon(R.drawable.ingana);

        TVinfo = (TextView)findViewById(R.id.textView2);
        TVinfo.setText("Ingrese, el nombre de usuario y respectiva contraseña, para iniciar sesión. Recuerde que una vez vinculado, no podrá cambiar las credenciales de acceso.");

        db = new dbHandler(this);
        receiver();
        receivernubeboot();

        validator = new Validator(this);//validator
        validator.setValidationListener(this);//validator

        LOG_USUARIO =(EditText) findViewById(R.id.obj_usuario);
        lOG_CONTRASENIA =(EditText) findViewById(R.id.obj_contrasenia);

        LOG_BOTTON = (Button) findViewById(R.id.obj_botton);
        LOG_BOTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 validator.validate();
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        try {
            frameAnimation.start();
            img.setVisibility(View.VISIBLE);
            Activar_sesion();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "Datos ingresados correctamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors)
    {
        for (ValidationError error : errors)
        {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
            else
            {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void Activar_sesion() throws JSONException{
        LOG_USUARIO =(EditText) findViewById(R.id.obj_usuario);
        lOG_CONTRASENIA =(EditText) findViewById(R.id.obj_contrasenia);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Volley_obj.JSON_URL,
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
                                    //si necxistencia de notificaiones pero con sesion
                                    sesion_data = jsonObject.getJSONArray("sesion_data");
                                    master_id = jsonObject.getString("master_id");
                                    for (int i = 0; i < sesion_data.length(); i++) {
                                        JSONObject row = sesion_data.getJSONObject(i);
                                        id = row.getString("id");
                                        identificacion = row.getString("identificacion");
                                        nombre1 = row.getString("nombre1");
                                        nombre2 = row.getString("nombre2");
                                        apellido1 = row.getString("apellido1");
                                        apellido2 = row.getString("apellido2");
                                    }
                                    //db
                                    ContentValues values = new ContentValues();
                                    values.put("id",id);
                                    values.put("master_id",master_id);
                                    values.put("identificacion",identificacion);
                                    values.put("nombre1",nombre1);
                                    values.put("nombre2",nombre2);
                                    values.put("apellido1",apellido1);
                                    values.put("apellido2", apellido2);

                                    db.onInsert("user", values);
                                    //select db
                                    String str = "SELECT id, identificacion, nombre1, nombre2, apellido1, apellido2,master_id FROM user";
                                    Cursor select_persona = db.onCursor(str);

                                    if (select_persona.getCount() >= 1) {
                                        if (select_persona.moveToFirst()) {
                                            id = select_persona.getString(0);
                                            identificacion = select_persona.getString(1);
                                            nombre1 = select_persona.getString(2);
                                            nombre2 = select_persona.getString(3);
                                            apellido1 = select_persona.getString(4);
                                            apellido2 = select_persona.getString(5);
                                            master_id = select_persona.getString(6);
                                        } else {
                                            Toast.makeText(MainLogueo.this, "Ocurrió un error al intentar almacenar registros", Toast.LENGTH_LONG).show();
                                        }
                                        /*Case Notificaciones*/
                                            if(notificaciones_ok == "true") {
                                                requestQueue.stop();
                                                notificaciones_data = jsonObject.getJSONArray("notificaciones_data");
                                                bandera_valida = 1;
                                                try {
                                                    injectContent(notificaciones_data);
                                                }catch(Exception e){Toast.makeText(MainLogueo.this, "Ocurrió un error: "+e.getMessage(),Toast.LENGTH_LONG).show();}
                                             }else {
                                                requestQueue.stop();
                                                bandera_valida = 2; //Sin notificaciones
                                                injectClear();
                                            }
                                    }
                                    break;
                                case "false":
                                    frameAnimation.stop();
                                    img.setVisibility(View.INVISIBLE);
                                    /*Credenciales Erroenas*/
                                    Toast.makeText(MainLogueo.this, "Credenciales de acceso incorrectas.", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        } catch (JSONException e) {
                            frameAnimation.stop();
                            img.setVisibility(View.INVISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainLogueo.this,"¡Verifique conectividad!. Si el problema persiste, comuniquese con su proveedor de internet.",Toast.LENGTH_LONG).show();
                        //error.printStackTrace();
                        frameAnimation.stop();
                        img.setVisibility(View.INVISIBLE);
                        requestQueue.stop();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("persona_id","false");
                params.put("master_id","false");
                params.put("usuario",LOG_USUARIO.getText().toString());
                params.put("contrasenia",lOG_CONTRASENIA.getText().toString());
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void injectContent(JSONArray not) throws JSONException {
        //Toast.makeText(MainLogueo.this, "Espere..", Toast.LENGTH_LONG).show();
        if(db!=null) {
            for (int i = 0; i < not.length(); i++) {
                JSONObject rownot = not.getJSONObject(i);

                ContentValues values = new ContentValues();
                values.put("id", rownot.getString("id"));
                values.put("titulo", rownot.getString("titulo"));
                values.put("descripcion", rownot.getString("descripcion"));
                values.put("tipo", rownot.getString("tipo"));
                values.put("fecha", rownot.getString("fecha"));
                values.put("hora", rownot.getString("hora"));
                values.put("estado",0);
                db.onInsert("noti", values);
            }

            Intent ij = new Intent(MainLogueo.this, Index.class);
            startActivity(ij);

            //Toast.makeText(this, "Terminó..", Toast.LENGTH_LONG).show();
            frameAnimation.stop();
            img.setVisibility(View.INVISIBLE);
            try {
                Intent ih = new Intent(MainLogueo.this, Index.class);
                startActivity(ih);
            } catch (Exception e) {
                Toast.makeText(MainLogueo.this, "Ocurrió un error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void injectClear(){
        frameAnimation.stop();
        img.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "Sin existencia de notificaciones.", Toast.LENGTH_LONG).show();
        Intent ij = new Intent(MainLogueo.this, Index.class);
        startActivity(ij);
    }

    //services..
    public void receivernubeboot(){//nube
        try {
            Long time2 = new GregorianCalendar().getTimeInMillis()+1000;
            Intent intentAlarm2 = new Intent(this, ReceiverBootNube.class);
            AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Calendar cal2 = Calendar.getInstance();
            //50000*5
            alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), 40000 * 4, PendingIntent.getBroadcast(this, 1, intentAlarm2, PendingIntent.FLAG_UPDATE_CURRENT));
        }catch (Exception e){
            Toast.makeText(this,"Fatal error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void receiver(){//local
        Long time = new GregorianCalendar().getTimeInMillis();
        Intent intentAlarm = new Intent(this, ReceiverBoot.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10000, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }

}
