package com.alamkanak.weekview.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This is a base activity which contains week view and all the codes necessary to initialize the
 * week view.
 * Created by Raquib-ul-Alam Kanak on 1/3/2014.
 * Website: http://alamkanak.github.io
 */
public abstract class BaseActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    dbHandler db;
    String persona_id = "";
    String master_id = "";

    StringRequest stringRequest;
    volley_conect Volley_obj;
    JSONObject jsonObject = null;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);
        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);
        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);
        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);
        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);
        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        db = new dbHandler(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(String id, String titulo, String descripcion, Calendar time) {
        //return String.format(titulo+" %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
        return String.format(titulo+"\n"+descripcion);
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Intent ij = new Intent(this, Agendashow.class);
        ij.putExtra("id",event.getIdbd());
        ij.putExtra("caso","caledit");
        startActivity(ij);
        //Toast.makeText(this, "idbase:"+event.getIdbd()+" - String: Clic " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

        final String[] val = {event.getIdbd()};
        final String idsel2 = event.getIdbd();

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
                    try {
                        stringRequest = new StringRequest(Request.Method.POST, Volley_obj.JSON_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            jsonObject = new JSONObject(response);

                                            int ok = jsonObject.getInt("ok");
                                            //Toast.makeText(BaseActivity.this,"E:"+ok,Toast.LENGTH_LONG).show();
                                            switch (ok) {
                                                case 0:
                                                    Toast.makeText(BaseActivity.this, "Ocurrió un error al intentar eliminar el recordatorio, intente nuevamente", Toast.LENGTH_LONG).show();
                                                    break;
                                                case 1:
                                                    db.onDelete("noti ", "id=?", val);//response delete
                                                    Toast.makeText(BaseActivity.this, "Recordatorio eliminado satisfactoriamente", Toast.LENGTH_LONG).show();
                                                    dialogo1.cancel();

                                                    finish();
                                                    Intent ijk = new Intent(BaseActivity.this, BasicActivity.class); //Redirreccionar aquí mismo
                                                    startActivity(ijk);
                                                    break;
                                            }

                                        } catch (JSONException e) {
                                            Toast.makeText(BaseActivity.this, "Error json: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(BaseActivity.this, "Error de conexion.." + error.getMessage(), Toast.LENGTH_LONG).show();
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
                        requestQueue = Volley.newRequestQueue(BaseActivity.this);
                        requestQueue.add(stringRequest);
                    } catch (Exception e) {
                        Toast.makeText(BaseActivity.this, "Error volley.."+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(BaseActivity.this, "No hay recordatorio para eliminar..", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {//No
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.cancel();
            }
        });

        dialogo1.show();
        //Toast.makeText(this, "Eliminar: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Intent ij = new Intent(this, Agendashow.class);
        ij.putExtra("id", "");
        ij.putExtra("caso", "calnew");
        ij.putExtra("fecha", String.format("%02d/%02d/%d", time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.MONTH) + 1, time.get(Calendar.YEAR)));
        ij.putExtra("hora",String.format("%02d:%02d",time.get(Calendar.HOUR_OF_DAY),time.get(Calendar.MINUTE)));
        startActivity(ij);
        //Toast.makeText(this, "Empty view long pressed: " + getEventTitle("","","",time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }
}
