package com.alamkanak.weekview.sample;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SegundoPlanoIntent extends IntentService {

    // Variables de la notificacion
    NotificationManager nm;
    Notification notif;
    static String ns = Context.NOTIFICATION_SERVICE;

    //Defino los iconos de la notificacion en la barra de notificacion
    int icono_v = R.drawable.ic_launcher;
    int icono_r = R.drawable.ic_launcher;

    MyTask myTask;


    public SegundoPlanoIntent() {
        super("SegundoPlanoIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        runMSG();
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void runMSG(){
        // Capturo la hora del evento
        long hora = System.currentTimeMillis();
        // Definimos la accion de la pulsacion sobre la notificacion (esto es opcional)
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(this, MainActivity2.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String titulo = "Titulo";
        String texto = "texto";
        /*Lvl 23*/
        notif = new Notification.Builder(this).
                setContentTitle("Notificación SCAI " + titulo).
                setContentText(texto).
                setSmallIcon(R.drawable.ic_launcher).setContentIntent(contentIntent).
                build();

        /*Lvl 23*/
    }

    private class MyTask extends AsyncTask<String, String, String> {

        private DateFormat dateFormat;
        private String date;
        private boolean cent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dateFormat = new SimpleDateFormat("HH:mm:ss");
            cent = true;
        }

        @Override
        protected String doInBackground(String... params) {
            while (cent){
                date = dateFormat.format(new Date());
                try {
                    publishProgress(date);
                    // Stop 5s
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getApplicationContext(), "Hora actual: " + values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cent = false;
        }
    }
}
