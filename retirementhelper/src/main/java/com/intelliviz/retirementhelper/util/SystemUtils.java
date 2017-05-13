package com.intelliviz.retirementhelper.util;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.intelliviz.retirementhelper.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by edm on 4/26/2017.
 */

public class SystemUtils {
    public static void setToolbarSubtitle(AppCompatActivity activity, String subtitle) {
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }
    public static boolean isValidFloatValue(String value) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        Number number = null;
        try {
            number = nf.parse(value);
            if(number instanceof Float || number instanceof Double || number instanceof Long || number instanceof Integer) {
                return true;
            }
        } catch (ParseException e) {
            return false;
        }
        return false;
    }

    public static String getCurrencyValue(EditText view) {
        String value = view.getText().toString();
        value = value.replace("$", "");
        return value;
    }

    public static String getTodaysDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(RetirementConstants.DATE_FORMAT);
        return sdf.format(date);
    }

    public static float getFloatCurrency(String value) {
        Number number1 = 0;
        Number number2 = 0;
        float retval = 0;
        NumberFormat nf = NumberFormat.getCurrencyInstance( java.util.Locale.US );
        NumberFormat numberf = NumberFormat.getNumberInstance(Locale.US);
        Number num1;
        try {
            num1 = numberf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance(java.util.Locale.US);
        try {

            number1 = nf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            number2 = df.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //try {

            //BigDecimal bd = new BigDecimal(value);
            //value = "$1,000.00";
            String fmt = nf.format(value);
            Log.d("SystemUtils", fmt);
            /*
            if(number instanceof Integer) {
                retval = (float)number.intValue();
            } else if(number instanceof Float) {
                retval = number.floatValue();
            } else if(number instanceof Long) {
                retval = (float)number.longValue();
            } else {
                retval = 0;
            }
            */
        //}catch(ParseException e) {
         //   Log.d("SystemUtils", e.toString());
       // }
        return retval;
    }

    public static String getFormattedCurrency(String value) {
        String retval = "0.00";
        NumberFormat nf = NumberFormat.getInstance( java.util.Locale.US );
        try {
            Number number = nf.parse(value);
            double dvalue = number.doubleValue();
            nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
            String s = nf.format(dvalue);
            return s;
        } catch (ParseException e) {
            return null;
        }
        /*
        float fvalue = Float.parseFloat(value);
        try{
            retval = nf.format(fvalue);
        }catch(Exception e){
        }
        return retval;
        */
    }

    public static String getFormattedCurrency(float value) {
        String retval = "0.00";
        NumberFormat nf = NumberFormat.getCurrencyInstance( java.util.Locale.US );
        try{
            retval = nf.format(value);
        }catch(Exception e){
        }
        return retval;
    }

    public static String getIncomeSourceTypeString(Context context, int incomeSourceType) {
        final String[] incomeTypes = context.getResources().getStringArray(R.array.income_types);
        return incomeTypes[incomeSourceType];
    }
}
