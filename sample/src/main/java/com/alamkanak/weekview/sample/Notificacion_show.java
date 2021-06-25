package com.alamkanak.weekview.sample;

import android.app.NotificationManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Notificacion_show extends AppCompatActivity {

    dbHandler db;

    TextView logo_tipo, l_tipo, l_descripcion, l_titulo;
    int icono_tipoDinero = R.drawable.monetario;
    int icono_tipoAcademico = R.drawable.academico;
    int icono_tipoInformativo = R.drawable.informativo;
    int icono_tipoAgenda = R.drawable.agenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    db = new dbHandler(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Habilitas icono
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Usas el drawable que deseas
        //getSupportActionBar().setIcon(R.drawable.inesup);

        String id = getIntent().getStringExtra("id");
        String titulo = getIntent().getStringExtra("titulo");
        String fecha = getIntent().getStringExtra("fecha");
        String hora = getIntent().getStringExtra("hora");
        String descripcion = getIntent().getStringExtra("descripcion");
        String tipo = getIntent().getStringExtra("tipo");

        logo_tipo = (TextView) findViewById(R.id.obj_tipo);
        //l_tipo = (TextView) findViewById(R.id.obj_tiponotificacion);
        l_descripcion = (TextView) findViewById(R.id.obj_descripcion);
        l_titulo = (TextView) findViewById(R.id.obj_titulo);

        if(id!=""){
        //    logo_institucion.setCompoundDrawablesWithIntrinsicBounds(icono_logo, 0, 0, 0);
        //    l_notificacion.setText(titulo);
            l_titulo.setText(titulo);
            l_descripcion.setText(fecha+"\n"+descripcion);
            switch (tipo){
                case "1":
                    logo_tipo.setCompoundDrawablesWithIntrinsicBounds(icono_tipoAcademico, 0, 0, 0);
                    break;
                case "2":
                    logo_tipo.setCompoundDrawablesWithIntrinsicBounds(icono_tipoDinero, 0, 0, 0);
                    break;
                case "3":
                    logo_tipo.setCompoundDrawablesWithIntrinsicBounds(icono_tipoInformativo, 0, 0, 0);
                    break;
                case "4":
                    logo_tipo.setCompoundDrawablesWithIntrinsicBounds(icono_tipoAgenda, 0, 0, 0);
                    break;
            }
        }
        else{
            //Redirigir a la lista
        }
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(getIntent().getExtras().getInt(id));// Cancelar la Notificacion que hemos comenzado
    }
}
