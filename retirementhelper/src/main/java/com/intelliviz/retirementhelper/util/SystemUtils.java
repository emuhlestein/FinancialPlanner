package com.intelliviz.retirementhelper.util;

import android.app.Activity;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.widget.WidgetProvider;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.intelliviz.retirementhelper.util.RetirementConstants.DATE_FORMAT;
import static java.lang.Integer.parseInt;

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

    public static boolean validateBirthday(String birthdate) {
        if(birthdate == null || birthdate.isEmpty()) {
            return false;
        }
        String[] tokens = birthdate.split("-");
        if(tokens.length != 3) {
            return false;
        }

        // Validate day
        if(tokens[0].length() == 1) {
            if(tokens[0].matches("[1-9]")) {
                return true;
            } else {
                return false;
            }
        } else if(tokens[0].length() == 2) {
            if (tokens[0].matches("[0-9]{2}")) {
                return true;
            } else {
                return false;
            }
        }

        // Validate month
        if(tokens[1].length() == 1) {
            if(tokens[1].matches("[1-9]")) {
                return true;
            } else {
                return false;
            }
        } else if(tokens[1].length() == 2) {
            if(tokens[1].matches("[0-9]{2}")) {
                return true;
            } else {
                return false;
            }
        }

        // Validate year
        if(tokens[2].length() != 4) {
            return false;
        }

        if(!tokens[2].matches("[0-9]{4}")) {
            return false;
        }

        String date = tokens[0]+"-"+tokens[1]+"-"+tokens[2];

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(date);
        } catch(ParseException e) {
            return false;
        }

        return true;
    }

    public static void setToolbarSubtitle(Activity activity, String subtitle) {
        if(activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if(actionBar != null) {
                actionBar.setSubtitle(subtitle);
            }
        }
    }

    public static String getTodaysDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return sdf.format(date);
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

    public static String getIncomeSourceTypeString(Context context, int incomeSourceType) {
        final String[] incomeTypes = context.getResources().getStringArray(R.array.income_types);
        return incomeTypes[incomeSourceType];
    }

    public static int getBirthYear(String birthdate) {
        if(birthdate == null || birthdate.isEmpty()) {
            return 0;
        }
        String[] birthTokens = birthdate.split("-");
        if(birthTokens.length != 3) {
            return 0;
        }
        return Integer.parseInt(birthTokens[2]);
    }

    public static int getBirthMonth(String birthdate) {
        String[] birthTokens = birthdate.split("-");
        return parseInt(birthTokens[1]);
    }

    public static int getBirthDay(String birthdate) {
        String[] birthTokens = birthdate.split("-");
        return parseInt(birthTokens[0]);
    }

    public static AgeData getAge(String birthdate) {
        String[] birthTokens = birthdate.split("-");
        if(birthTokens.length != 3) {
            return new AgeData();
        }

        int birthDay = parseInt(birthTokens[0]);
        int birthMonth = parseInt(birthTokens[1]);
        int birthYear = parseInt(birthTokens[2]);

        String today = SystemUtils.getTodaysDate();

        String[] nowTokens = today.split("-");

        int nowDay = parseInt(nowTokens[0]);
        int nowMonth = parseInt(nowTokens[1]);
        int nowYear = parseInt(nowTokens[2]);

        int years = nowYear - birthYear;

        int months = nowMonth - birthMonth;
        if(months < 0) {
            years--;
            months += 12;
        } else if(months == 0) {
            int dayDiff = nowDay - birthDay;
            if(dayDiff < 0) {
                years--;
                months = 11;
            }
        }
        return new AgeData(years, months);
    }

    /**
     * The format for age is "Y M", where Y is an integer that is the year, and M
     * is an integer that is the month.
     *
     * @param age The age.
     * @return The AgeData;
     */
    // TODO make sure all callers check for invalid age
    public static AgeData parseAgeString(String age) {
        if(age == null || age.isEmpty()) {
            return null;
        }

        String[] tokens = age.split(" ");
        int year;
        int month = 0;
        if(tokens.length == 1) {
            try {
                year = parseInt(tokens[0]);
            } catch (NumberFormatException e) {
                return null;
            }
            return new AgeData(year, month);
        } else if(tokens.length == 2) {
            try {
                year = parseInt(tokens[0]);
                month = parseInt(tokens[1]);
            } catch (NumberFormatException e) {
                return null;
            }
            return new AgeData(year, month);
        }
        return new AgeData();
    }

    public static AgeData parseAgeString(String year, String month) {
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append(" ");
        sb.append(month);
        return parseAgeString(sb.toString());
    }

    public static String trimAge(String age) {
        age = age.replace("y", "");
        age = age.replace("m", "");
        return age;
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

    public static String getFormattedAge(AgeData ageData) {
        String year = Integer.toString(ageData.getYear());
        String month = Integer.toString(ageData.getMonth());
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append("y ");
        sb.append(month);
        sb.append("m");
        return sb.toString();
    }

    public static void updateAppWidget(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        DataBaseUtils.updateSummaryData(db);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(application);
        ComponentName appWidget = new ComponentName(application, WidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_list_view);
    }

    /**
     * Get the age for the spouse, given the principle spouse's age.
     * @param birthdate The birthdate of the principle spouse.
     * @param spouseBirthdate The spouse's birthdate.
     * @param age The age of tghe principle spouse.
     * @return The spouse start age.
     */
    public static AgeData getSpouseAge(String birthdate, String spouseBirthdate, AgeData age) {
        AgeData currentAge = SystemUtils.getAge(birthdate);
        AgeData spouseAge =  SystemUtils.getAge(spouseBirthdate);
        int numMonths = currentAge.diff(spouseAge);

        if(spouseAge.isBefore(currentAge)) {
            // spouse is younger
            return age.subtract(numMonths);
        } else {
            // spouse is older
            return age.add(numMonths);
        }
    }

    /**
     * Get the age for the spouse, given the other spouse's age.
     * @param birthdate The birthdate.
     * @param spouseBirthdate The spouse's birthdate.
     * @param spouseAge The age.
     * @return The spouse start age.
     */
    public static AgeData getAge(String birthdate, String spouseBirthdate, AgeData spouseAge) {
        AgeData currentAge = SystemUtils.getAge(birthdate);
        AgeData spouseCurrentAge = SystemUtils.getAge(spouseBirthdate);
        int numMonths = spouseCurrentAge.diff(currentAge);

        if(currentAge.isBefore(spouseCurrentAge)) {
            // spouse is younger
            return spouseAge.subtract(numMonths);
        } else {
            // spouse is older
             return spouseAge.add(numMonths);
        }
    }
}
