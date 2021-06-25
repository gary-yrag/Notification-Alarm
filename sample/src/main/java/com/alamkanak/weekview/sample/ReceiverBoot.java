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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by user on 04/12/2015.
 */
public class ReceiverBoot extends BroadcastReceiver {
    dbHandler db;

    NotificationManager nm;
    Notification notif;
    String ns = Context.NOTIFICATION_SERVICE;

    Bitmap bm_academico,bm_monetario,bm_informativo,bm_agenda;

    int icono_tipoDinero = R.drawable.monetario;
    int icono_tipoAcademico = R.drawable.academico;
    int icono_tipoInformativo = R.drawable.informativo;
    int icono_tipoAgenda = R.drawable.agenda;

    Uri not_sonido;
    long[] not_vibracion;
    Context contexto;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@Override
    public void onReceive(Context context, Intent intent)
    {
        contexto = context;
        db = new dbHandler(context);
        receivernubeboot();
        if(db!=null){
            Cursor c=null;
            String fecha = "",hora = "", minuto = "";
            fecha = (DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString());
            //hora = (DateFormat.format("hh:mm:ss", new java.util.Date()).toString());
            Calendar cal = Calendar.getInstance();
            hora = DateFormat.format("HH",cal.getTime()).toString();
            minuto = DateFormat.format("mm",cal.getTime()).toString();

            try {
                String str = "SELECT id, titulo, descripcion, tipo, strftime('%d/%m/%Y',fecha) AS fecha, hora, strftime('%H',hora) as Ghora, strftime('%M',hora) as Gminuto " +
                        " FROM noti WHERE fecha='" + fecha + "' AND strftime('%H',hora) = '" + hora + "' AND strftime('%M',hora) = '" + minuto + "' AND estado=0 LIMIT 1";
                c = db.onCursor(str);
            }catch(Exception e){
                Toast.makeText(context,"Error interno: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }

            if(c.getCount()>=1){
                if (c.moveToFirst()) {
                    String id = "",titulo = "",descripcion = "",tipo = "",bdfecha = "",bdhora = "";
                    id = c.getString(0);
                    titulo = c.getString(1);
                    descripcion = c.getString(2);
                    tipo = c.getString(3);
                    bdfecha = c.getString(4);
                    bdhora = c.getString(5);

                    //Toast.makeText(context,"ReceiverBoot db id:"+id,Toast.LENGTH_LONG).show();

                    ContentValues values = new ContentValues();
                    values.put("estado",1);
                    db.onUpdate("noti", "id=?", new String[]{String.valueOf(id)}, values);//update

                    not_sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//Lanzar notificación
                    not_vibracion = new long[]{1000,500,1000};

                    nm = (NotificationManager) context.getSystemService(ns);
                    // Notificacion

                        Intent notificationIntent = new Intent(context, Notificacion_show.class);
                        notificationIntent.putExtra("id", id);//Mandar id de la notificación en 'notificacion_id'
                        notificationIntent.putExtra("titulo", titulo);
                        notificationIntent.putExtra("fecha", bdfecha);
                        notificationIntent.putExtra("hora", bdhora);
                        notificationIntent.putExtra("descripcion", descripcion);
                        notificationIntent.putExtra("tipo", tipo);

                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    /*Armar icono init */
                    bm_academico = BitmapFactory.decodeResource(context.getResources(),R.drawable.academico);
                    bm_monetario = BitmapFactory.decodeResource(context.getResources(),R.drawable.monetario);
                    bm_informativo = BitmapFactory.decodeResource(context.getResources(),R.drawable.informativo);
                    bm_agenda = BitmapFactory.decodeResource(context.getResources(),R.drawable.agenda);

                    Bitmap iconoRes = null;
                    int icono_result = 0;
                    switch (tipo){
                        case "1":
                            iconoRes = bm_academico;
                            icono_result = icono_tipoAcademico;
                            break;
                        case "2":
                            iconoRes = bm_monetario;
                            icono_result = icono_tipoDinero;
                            break;
                        case "3":
                            iconoRes = bm_informativo;
                            icono_result = icono_tipoInformativo;
                            break;
                        case "4":
                            iconoRes = bm_agenda;
                            icono_result = icono_tipoAgenda;
                            break;
                    }
                    notif = new Notification.Builder(context).
                            setContentTitle("SCAI " + titulo).
                            setContentText(descripcion).
                            setSmallIcon(icono_result).
                            //setSmallIcon(R.drawable.inesup_notificacion).
                            //setLargeIcon(iconoRes).
                            setContentIntent(contentIntent).
                            setWhen(System.currentTimeMillis()).
                            setSound(not_sonido).setVibrate(not_vibracion).
                            setAutoCancel(true).
                            setTicker("Notificación").
                            build();

                    nm.cancelAll();
                    nm.notify(Integer.parseInt(id), notif);
                }
            }
        }
    }

    public void receivernubeboot(){
        try {
            Long time2 = new GregorianCalendar().getTimeInMillis()+1000;
            Intent intentAlarm2 = new Intent(contexto, ReceiverBootNube.class);
            AlarmManager alarmManager2 = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
            Calendar cal2 = Calendar.getInstance();
            //50000*5
            alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), 40000 * 4, PendingIntent.getBroadcast(contexto, 1, intentAlarm2, PendingIntent.FLAG_UPDATE_CURRENT));
        }catch (Exception e){
            Toast.makeText(contexto,"Fatal error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}