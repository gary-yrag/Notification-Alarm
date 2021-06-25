package com.alamkanak.weekview.sample;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.sample.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.spec.ECField;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agendashow extends AppCompatActivity implements Validator.ValidationListener {
    dbHandler db;
    @NotEmpty(message = "Escriba el título de la notificación.")
    EditText obt;
    @NotEmpty(message = "Escriba la descripción de la notificación.")
    EditText obc;

    CheckBox obdiaall,obnotill;

    @NotEmpty(message = "Escriba o seleccione los rangos de tiempo, para notificar.")
    EditText obfdesde,obtdesde,obfhasta,obthasta;

    ImageButton obok,obde,obca;
    String id = "",fecha="",hora="", master_id = "", persona_id = "", ad_evt = "off";
    int casov = 0,ok = 0, notificarme = 1;

    StringRequest stringRequest;
    RequestQueue requestQueue;
    JSONObject jsonObject = null;
    volley_conect Volley_obj;

    recursos format;
    Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendashow);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //....
        obt = (EditText) findViewById(R.id.obtitulo);
        obc = (EditText) findViewById(R.id.obcontenido);

        obdiaall = (CheckBox) findViewById(R.id.diaall);
        obnotill = (CheckBox) findViewById(R.id.notill);

        obfdesde = (EditText) findViewById(R.id.fedesde);
        obtdesde = (EditText) findViewById(R.id.tidesde);
        obfhasta = (EditText) findViewById(R.id.fehasta);
        obthasta = (EditText) findViewById(R.id.tihasta);

        obok = (ImageButton)findViewById(R.id.obokk);
        obde = (ImageButton)findViewById(R.id.obdel);
        obca = (ImageButton)findViewById(R.id.obcan);

        db = new dbHandler(this);
        format = new recursos();

        validator = new Validator(this);//validator
        validator.setValidationListener(this);//validator

        try {
            per_get();//get sql data user
        }catch (Exception e){
            Toast.makeText(this,"Error recover: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        //..
        obdiaall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaall();
            }
        });
        obfdesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(0);
            }
        });

        obfhasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(1);
            }
        });

        obtdesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(0);
            }
        });

        obthasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(1);
            }
        });

        obok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        obde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evde();
            }
        });

        obca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evca();
            }
        });

        String caso = getIntent().getStringExtra("caso");
        id = getIntent().getStringExtra("id");

        switch (caso){
            case "caledit":
                try {
                    String sql = "SELECT id,titulo,descripcion,strftime('%d/%m/%Y',fecha),strftime('%H:%M',hora),strftime('%d/%m/%Y',fechaf),strftime('%H:%M',horaf),allday,notificarme,tipo FROM noti WHERE id='" + id + "'";
                    Cursor query = db.onCursor(sql);
                    if (query.moveToFirst()) {
                        obt.setText(query.getString(1));
                        obc.setText(query.getString(2));
                        obfdesde.setText(query.getString(3));//times
                        obtdesde.setText(format.hour12(query.getString(4)));
                        obfhasta.setText(query.getString(5));
                        obthasta.setText(format.hour12(query.getString(6)));

                        switch(query.getInt(7)){//all day
                            case 0:
                                obdiaall.setChecked(false);
                                break;
                            case 1:
                                obdiaall.setChecked(true);
                                break;
                        }

                        switch(query.getInt(8)){ //notificarme
                            case 0:
                                obnotill.setChecked(false);
                                break;
                            case 1:
                                obnotill.setChecked(true);
                                break;
                        }

                        //
                        if(query.getInt(9) != 3){
                            obde.setVisibility(View.INVISIBLE);
                            obok.setVisibility(View.INVISIBLE);
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(this,"Advertencia: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
                break;
            case "calnew":
                fecha = getIntent().getStringExtra("fecha");
                hora = format.hour12(getIntent().getStringExtra("hora"));
                obfdesde.setText(fecha);//..Set times
                obtdesde.setText(hora);
                obfhasta.setText(fecha);
                obthasta.setText(hora);

                obde.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        evok();
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showTimePickerDialog(int caso) {
        DialogFragment newFragment = new TimePickerFragment();

        Bundle args = new Bundle();
            args.putInt("caso", caso);

        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "timePicker");
        //newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showDatePickerDialog(int caso) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
            args.putInt("caso", caso);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void evok(){//insert?
        casov = (id == "")?1:2;
        notificarme = (obnotill.isChecked())?1:0;
        final String notificarmestr = (obnotill.isChecked())?"on":"off";
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
                            voley_response(jsonObject);//response
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
                params.put("age_id",id);//cred
                params.put("caso",""+casov);
                params.put("nam_evt",obt.getText().toString());
                params.put("des_evt",obc.getText().toString());
                params.put("fi_evt",obfdesde.getText().toString());
                params.put("hi_evt",obtdesde.getText().toString());
                params.put("ff_evt",obfhasta.getText().toString());
                params.put("hf_evt",obthasta.getText().toString());
                params.put("ad_evt",ad_evt);
                params.put("not_evt",notificarmestr);
                params.put("per_not","1");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void evde(){//delete
        if(id != ""){
            stringRequest = new StringRequest(Request.Method.POST, Volley_obj.JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                jsonObject = new JSONObject(response);
                            } catch (JSONException e) {
                                Toast.makeText(Agendashow.this,"Error json: "+e.getMessage(),Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                            try {
                                voley_response_delete(jsonObject);//response delete
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Agendashow.this, "Error de conexion.."+error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("master_id",master_id);
                    params.put("persona_id",persona_id);
                    params.put("age_id",id);//cred
                    params.put("caso","3");
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }else{
            Toast.makeText(this,"No hay recordatorio para eliminar..",Toast.LENGTH_LONG).show();
        }
    }

    public void evca(){
        finish();
        Intent ijk = new Intent(this, Index.class);
        startActivity(ijk);
    }

    public void per_get(){
        try {
            String sql = "SELECT id,master_id FROM user";
            Cursor c = db.onCursor(sql);

            if (c.getCount() >= 1) {
                if (c.moveToFirst()) {
                    persona_id = c.getString(0);
                    master_id = c.getString(1);
                }
            }
        }catch(Exception e){
            Toast.makeText(this,"Error interno: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void voley_response(JSONObject json) throws JSONException {//response volley
        ok = json.getInt("ok");
        switch (ok){
            case 0:
                Toast.makeText(this,"Ocurrió un error al intentar almacenar el recordatorio, intente nuevamente",Toast.LENGTH_LONG).show();
                break;
            case 1:
                ContentValues values = new ContentValues();
                values.put("titulo",obt.getText().toString());
                values.put("descripcion",obc.getText().toString());
                values.put("tipo","3");
                values.put("fecha",obfdesde.getText().toString());
                values.put("hora",format.hour24(obtdesde.getText().toString()));
                values.put("fechaf",obfhasta.getText().toString());
                values.put("horaf", format.hour24(obthasta.getText().toString()));
                values.put("notificarme",notificarme);
                values.put("estado", 0);

                if(id.toString() == "" || id.toString() == null || id.toString() == " "){//Insert
                    values.put("id", json.getString("age_id"));
                    try {
                        db.onInsert("noti", values);
                        Toast.makeText(this, "Recordatorio almacenado satisfactoriamente", Toast.LENGTH_LONG).show();
                        finish();
                        Intent ijk = new Intent(this, Index.class);
                        startActivity(ijk);

                    }catch(Exception e){
                        Toast.makeText(this, "Ocurrió un error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else{//Update
                    try {
                        db.onUpdate("noti", "id=?", new String[]{String.valueOf(id)}, values);
                        Toast.makeText(this, "Recordatorio actualizado satisfactoriamente", Toast.LENGTH_LONG).show();
                        finish();
                        Intent ijk = new Intent(this, Index.class);
                        startActivity(ijk);
                    }catch(Exception e){
                        Toast.makeText(this, "Ocurrió un error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public void voley_response_delete(JSONObject json) throws JSONException {
        ok = json.getInt("ok");
        switch (ok) {
            case 0:
                Toast.makeText(this,"Ocurrió un error al intentar eliminar el recordatorio, intente nuevamente",Toast.LENGTH_LONG).show();
                break;
            case 1:
                String[] val = {id};
                db.onDelete("noti ","id=?",val);
                Toast.makeText(this,"Recordatorio eliminado satisfactoriamente",Toast.LENGTH_LONG).show();
                finish();
                Intent ijk = new Intent(this, Index.class);
                startActivity(ijk);
                break;
        }
    }

    public void diaall(){
        if(obdiaall.isChecked()){
            obtdesde.setEnabled(false);
            obthasta.setEnabled(false);
            ad_evt = "on";
        }else{
            obtdesde.setEnabled(true);
            obthasta.setEnabled(true);
            ad_evt = "off";
        }
    }
}
