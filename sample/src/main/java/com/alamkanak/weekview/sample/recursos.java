package com.alamkanak.weekview.sample;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrador on 02/06/2016.
 */
public class recursos {

    final Calendar c = Calendar.getInstance();

    public String hour12(String input){
        //Input date in String format
        //String input = "15/02/2014 22:22:12";
        //Date/time pattern of input date
        //DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//Date/time pattern of desired output date
        //DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        input = (input=="")?c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE):input;
        DateFormat df = new SimpleDateFormat("HH:mm");//Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
        Date date = null;
        String output = null;
        try {//Conversion of input String to date
            date = df.parse(input);//old date format to new date format
            output = outputformat.format(date);
            //System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output; //10:22:12
    }

    public String hour24(String input){
        //input = "23/12/2014 10:22:12 PM";//Format of the date defined in the input String
        //DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");//Desired format: 24 hour format: Change the pattern as per the need
        //DateFormat outputformat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        input = (input=="")?c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE):input;

        DateFormat df = new SimpleDateFormat("hh:mm aa");//Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("HH:mm");
        Date date = null;
        String output = null;
        try {//Converting the input String to Date
            date = df.parse(input);//Changing the format of date and storing it in String
            output = outputformat.format(date);//Displaying the date
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;//22:22:12
    }
}
