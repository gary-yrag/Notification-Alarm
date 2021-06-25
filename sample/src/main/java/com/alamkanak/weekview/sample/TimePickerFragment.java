package com.alamkanak.weekview.sample;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Administrador on 01/06/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    int caso;
    EditText hora;
    recursos format;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        format = new recursos();
        caso = getArguments().getInt("caso");
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        switch (caso){
            case 0:
                hora = (EditText) getActivity().findViewById(R.id.tidesde);
                break;
            case 1:
                hora = (EditText) getActivity().findViewById(R.id.tihasta);
                break;
        }

        try {
            String horatmp = format.hour24(hora.getText().toString());

            String[] horat = horatmp.split(":");
            if ((horat.length) > 1) {
                hour = Integer.parseInt(horat[0]);
                minute = Integer.parseInt(horat[1]);
            }
        }catch(Exception e){
            e.getMessage();
        }
        // Create a new instance of TimePickerDialog and return it
        //return new TimePickerDialog(getActivity(), this, hour, minute,DateFormat.is24HourFormat(getActivity()));
        return new TimePickerDialog(getActivity(), this, hour, minute,DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        format = new recursos();
        // Do something with the time chosen by the user
        EditText tv1 = null;
        switch (caso){
            case 0:
                tv1 = (EditText) getActivity().findViewById(R.id.tidesde);
                break;
            case 1:
                tv1 = (EditText) getActivity().findViewById(R.id.tihasta);
                break;
        }
        tv1.setText(format.hour12(""+view.getCurrentHour()+":"+view.getCurrentMinute()));
    }
}
