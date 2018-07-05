package com.intelliviz.lowlevel.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.NumberFormat;
import java.text.ParseException;

import static android.content.ContentValues.TAG;

/**
 * Utility class.
 * Created by Ed Muhlestein on 4/26/2017.
 */

public class SystemUtils {
    public static GoogleApiClient createGoogleApiClient(Context context) {

        FragmentActivity fact;
        GoogleApiClient.OnConnectionFailedListener listener;
        try {
            fact = (FragmentActivity)context;
            listener = (GoogleApiClient.OnConnectionFailedListener)context;
        } catch(ClassCastException e) {
            Log.e(TAG, "Failed to create GoogleApiClient");
            return null;
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        return new GoogleApiClient.Builder(context)
                .enableAutoManage(fact, listener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
/*
    public static void updateAppWidget(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        DataBaseUtils.updateSummaryData(db);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(application);
        ComponentName appWidget = new ComponentName(application, WidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_list_view);
    }
    */

    public static void setToolbarSubtitle(Activity activity, String subtitle) {
        if(activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if(actionBar != null) {
                actionBar.setSubtitle(subtitle);
            }
        }
    }

    public static String getFormattedCurrency(Double value) {
        if (value == null) {
            return null;
        }
        Number number = value;
        double dvalue = number.doubleValue();
        NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        return nf.format(dvalue);
    }

    public static String getFormattedCurrency(String value) {
        if(value == null || value.isEmpty()) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance( java.util.Locale.US );
        try {
            Number number = nf.parse(value);
            double dvalue = number.doubleValue();
            nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
            return nf.format(dvalue);
        } catch (ParseException e) {
            return null;
        }
    }



    /**
     * Convert a currency value to a number. Try parsing the value as a number first.
     * If this fails, try parsing as currency. If this fails, return null.
     * @param value The currency value to convert.
     * @return A string with the number value.
     */
    public static String getFloatValue(String value) {
        if(value == null || value.isEmpty()) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance( java.util.Locale.US );
        Number number;
        try {
            // try to parse a number
            number = nf.parse(value);
            return number.toString();
        } catch (ParseException e) {
            // ignore exception
        }

        // could not parse number; parse a currency. If this fails, input is invalid.
        nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        try {
            number = nf.parse(value);
            return number.toString();
        } catch (ParseException e) {

        }
        return null; // failed to convert
    }
}
