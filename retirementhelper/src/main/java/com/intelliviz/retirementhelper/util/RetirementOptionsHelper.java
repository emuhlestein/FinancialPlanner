package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.db.RetirementContract;

/**
 * Helper class for retirement options.
 * Created by Ed Muhlestein on 6/28/2017.
 */

public class RetirementOptionsHelper {
    public static String addAge(Context context, AgeData age) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.MilestoneEntry.COLUMN_AGE, age.getUnformattedString());
        Uri uri = context.getContentResolver().insert(RetirementContract.MilestoneEntry.CONTENT_URI, values);
        if(uri == null) {
            return null;
        } else {
            return uri.getLastPathSegment();
        }
    }

    public static int deleteAge(Context context, long id) {
        String sid = String.valueOf(id);
        Uri uri = RetirementContract.MilestoneEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().delete(uri, null, null);
    }

    public static RetirementOptionsData getRetirementOptionsData(Context context) {
        PersonalInfoData perid = getPersonalInfoData(context);
        String birthdate = "01-01-1970";
        if(perid != null) {
            birthdate = perid.getBirthdate();
        }

        Cursor cursor = getRetirementOptions(context);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int endAgeIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_END_AGE);
        int withdrawModeIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE);
        int withdrawAmountIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT);


        String endAge = cursor.getString(endAgeIndex);
        int withdrawMode = cursor.getInt(withdrawModeIndex);
        String withdrawAmount = cursor.getString(withdrawAmountIndex);
        if(withdrawAmount.equals("")) {
            withdrawAmount = "4";
        }
        cursor.close();
        return new RetirementOptionsData(birthdate, endAge, withdrawMode, withdrawAmount);
    }

    public static int saveBirthdate(Context context, String birthdate) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.PersonalInfoEntry.COLUMN_BIRTHDATE, birthdate);
        Uri uri = RetirementContract.PersonalInfoEntry.CONTENT_URI;
        return context.getContentResolver().update(uri, values, null, null);
    }

    private static PersonalInfoData getPersonalInfoData(Context context) {
        Cursor cursor = getPersonalInfo(context);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int birthdateIndex = cursor.getColumnIndex(RetirementContract.PersonalInfoEntry.COLUMN_BIRTHDATE);

        String birthdate = cursor.getString(birthdateIndex);
        return new PersonalInfoData(birthdate);
    }

    private static Cursor getPersonalInfo(Context context) {
        Uri uri = RetirementContract.PersonalInfoEntry.CONTENT_URI;
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    //
    // Methods for retirement options table
    //
    public static int saveRetirementOptions(Context context, RetirementOptionsData rod) {
        saveBirthdate(context, rod.getBirthdate());
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_END_AGE, rod.getEndAge());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE, rod.getWithdrawMode());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT, rod.getWithdrawAmount());
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return context.getContentResolver().update(uri, values, null, null);
    }

    private static Cursor getRetirementOptions(Context context) {
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return context.getContentResolver().query(uri, null, null, null, null);
    }
}
