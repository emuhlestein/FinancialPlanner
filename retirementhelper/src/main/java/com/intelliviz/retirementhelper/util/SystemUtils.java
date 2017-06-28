package com.intelliviz.retirementhelper.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SummaryData;
import com.intelliviz.retirementhelper.services.PersonalDataService;
import com.intelliviz.retirementhelper.services.RetirementOptionsService;
import com.intelliviz.retirementhelper.widget.WidgetProvider;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_PERSONAL_INFO;
import static com.intelliviz.retirementhelper.util.RetirementConstants.REQUEST_RETIRE_OPTIONS;
import static java.lang.Integer.parseInt;

/**
 * Created by edm on 4/26/2017.
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

    public static void updateAppWidget(Context context) {
        PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(context);
        RetirementOptionsData rod = DataBaseUtils.getRetirementOptionsData(context);
        List<MilestoneData> milestones = BenefitHelper.getAllMilestones(context, rod, pid);

        List<SummaryData> summaryData = getSummaryData(milestones);
        DataBaseUtils.updateSummaryData(context);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName appWidget = new ComponentName(context, WidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_list_view);
    }

    private static List<SummaryData> getSummaryData(List<MilestoneData> milestoneData) {
        List<SummaryData> summaryData = new ArrayList<>();
        for(MilestoneData msd : milestoneData) {
            summaryData.add(new SummaryData(msd.getStartAge().toString(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
        }
        return summaryData;
    }

    public static boolean onActivityResultForOptionMenu (Context context, int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_RETIRE_OPTIONS:
                if (resultCode == RESULT_OK) {
                    RetirementOptionsData rod = intent.getParcelableExtra(RetirementConstants.EXTRA_RETIREOPTIONS_DATA);
                    updateROD(context, rod);
                }
                return false;
            case REQUEST_PERSONAL_INFO:
                if (resultCode == RESULT_OK) {
                    PersonalInfoData pid = intent.getParcelableExtra(RetirementConstants.EXTRA_PERSONALINFODATA);
                    updatePERID(context, pid);
                }
                return false;
            default:
                return true;
        }
    }

    public static boolean validateBirthday(String birthdate) {
        String[] tokens = birthdate.split("-");
        if(tokens.length != 3) {
            return false;
        }

        // Validate year
        if(tokens[0].length() != 4) {
            return false;
        }
        if(!tokens[0].matches("[0-9][0-9][0-9][0-9]")) {
            return false;
        }

        // Validate month
        if(tokens[1].length() != 2) {
            return false;
        }
        if(!tokens[1].matches("[0-9][0-9]")) {
            return false;
        }

        // Validate day
        if(tokens[2].length() != 2) {
            return false;
        }
        if(!tokens[2].matches("[0-9][0-9]")) {
            return false;
        }

        String date = tokens[0]+"-"+tokens[1]+"-"+tokens[2];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(date);
        } catch(ParseException e) {
            return false;
        }

        return true;
    }

    public static void setToolbarSubtitle(AppCompatActivity activity, String subtitle) {
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }
    public static boolean isValidFloatValue(String value) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        Number number;
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

    public static String getFormattedCurrency(Double value) {
        if (value == null) {
            return null;
        }
        Number number = value;
        double dvalue = number.doubleValue();
        NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        String s = nf.format(dvalue);
        return s;
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
            String s = nf.format(dvalue);
            return s;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getIncomeSourceTypeString(Context context, int incomeSourceType) {
        final String[] incomeTypes = context.getResources().getStringArray(R.array.income_types);
        return incomeTypes[incomeSourceType];
    }

    public static int getBirthYear(String birthdate) {
        String[] birthTokens = birthdate.split("-");

        int birthYear = parseInt(birthTokens[0]);
        return birthYear;
    }

    public static AgeData getAge(String birthdate) {
        String[] birthTokens = birthdate.split("-");
        if(birthTokens.length != 3) {
            return new AgeData();
        }

        int birthYear = parseInt(birthTokens[0]);
        int birthMonth = parseInt(birthTokens[1]);
        int birthDay = parseInt(birthTokens[2]);

        String today = SystemUtils.getTodaysDate();

        String[] nowTokens = today.split("-");

        int nowYear = parseInt(nowTokens[0]);
        int nowMonth = parseInt(nowTokens[1]);
        int nowDay = parseInt(nowTokens[2]);

        int years = nowYear - birthYear;

        int monthDiff = nowMonth - birthMonth;
        if(monthDiff < 0) {
            years--;
        } else if(monthDiff == 0) {
            int dayDiff = nowDay - birthDay;
            if(dayDiff < 0) {
                years--;
            }
        }

        int months = nowMonth - birthMonth;
        if(months < 0) {
            months += 12;
        }
        int dayDiff = nowDay - birthDay;
        if(dayDiff < 0) {
            months--;
        }

        return new AgeData(years, months);
    }

    /**
     * The format for age is "Y M", where Y is an integer that is the year, and M
     * is an integer that is the month.
     *
     * @param age
     * @return The AgeData;
     */
    // TODO make sure all callers chaeck for invalid age
    public static AgeData parseAgeString(String age) {
        String[] tokens = age.split(" ");
        int year = 0;
        int month = 0;
        if(tokens.length == 1) {
            try {
                year = Integer.parseInt(tokens[0]);
            } catch (NumberFormatException e) {
                return null;
            }
            return new AgeData(year, month);
        } else if(tokens.length == 2) {
            try {
                year = Integer.parseInt(tokens[0]);
                month = Integer.parseInt(tokens[1]);
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

    public  static void updateROD(Context context, RetirementOptionsData rod) {
        Intent intent = new Intent(context, RetirementOptionsService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_DATA, rod);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_UPDATE);
        context.startService(intent);
    }

    public static void updatePERID(Context context, PersonalInfoData pid) {
        RetirementInfoMgr.getInstance().setBirthdate(pid.getBirthdate());
        Intent intent = new Intent(context, PersonalDataService.class);
        intent.putExtra(RetirementConstants.EXTRA_DB_DATA, pid);
        intent.putExtra(RetirementConstants.EXTRA_DB_ACTION, RetirementConstants.SERVICE_DB_UPDATE);
        context.startService(intent);
    }
}
