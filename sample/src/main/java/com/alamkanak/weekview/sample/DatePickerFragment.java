package com.alamkanak.weekview.sample;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Administrador on 01/06/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    int caso;
    EditText fecha;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        caso = getArguments().getInt("caso");
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        switch (caso){
            case 0:
                fecha = (EditText) getActivity().findViewById(R.id.fedesde);
                break;
            case 1:
                fecha = (EditText) getActivity().findViewById(R.id.fehasta);
                break;
        }

        String[] fechat = fecha.getText().toString().split("/");
        if((fechat.length)>1){
            year = Integer.parseInt(fechat[2]);
            month = Integer.parseInt(fechat[1]);
            day = Integer.parseInt(fechat[0]);
        }
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        EditText tv1 = null;
        switch (caso){
            case 0:
                tv1 = (EditText) getActivity().findViewById(R.id.fedesde);
                break;
            case 1:
                tv1 = (EditText) getActivity().findViewById(R.id.fehasta);
                break;
        }
        tv1.setText(String.format("%02d/%02d/%d",view.getDayOfMonth(),view.getMonth(),view.getYear()));
        //tv1.setText(view.getYear()+"/"+view.getMonth()+"/"+view.getDayOfMonth());
    }
}
