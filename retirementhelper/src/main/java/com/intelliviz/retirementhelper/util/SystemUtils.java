package com.intelliviz.retirementhelper.util;

import android.content.Context;

import com.intelliviz.retirementhelper.R;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by edm on 4/26/2017.
 */

public class SystemUtils {
    public static float getFloatCurrency(String value) {
        float retval = 0;
        NumberFormat nf = NumberFormat.getCurrencyInstance( java.util.Locale.US );
        try {
            retval = nf.parse(value).floatValue();
        }catch(ParseException e) {

        }
        return retval;
    }

    public static String getFormattedCurrency(String value) {
        String retval = "0.00";
        NumberFormat nf = NumberFormat.getCurrencyInstance( java.util.Locale.US );
        float fvalue = Float.parseFloat(value);
        try{
            retval = nf.format(fvalue);
        }catch(Exception e){
        }
        return retval;
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
