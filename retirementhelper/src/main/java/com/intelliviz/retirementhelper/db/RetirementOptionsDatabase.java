package com.intelliviz.retirementhelper.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;

/**
 * Created by edm on 9/30/2017.
 */

public class RetirementOptionsDatabase {
    private volatile static RetirementOptionsDatabase mINSTANCE;
    private ContentResolver mCR;

    public static RetirementOptionsDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (RetirementOptionsDatabase.class) {
                if(mINSTANCE == null) {

                    mINSTANCE = new RetirementOptionsDatabase(context);
                }
            }
        }
        return mINSTANCE;
    }

    private RetirementOptionsDatabase(Context context) {
        mCR = context.getContentResolver();
    }

    public RetirementOptionsData get() {
        Cursor cursor = query();
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int birthdateIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE);
        int endAgeIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_END_AGE);
        int withdrawModeIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE);
        int withdrawAmountIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT);

        String birthdate = "01-01-1970";
        if(birthdateIndex != -1) {
            birthdate = cursor.getString(birthdateIndex);
        };
        String endAge = cursor.getString(endAgeIndex);
        int withdrawMode = cursor.getInt(withdrawModeIndex);
        String withdrawAmount = cursor.getString(withdrawAmountIndex);
        if(withdrawAmount.equals("")) {
            withdrawAmount = "4";
        }
        cursor.close();
        return new RetirementOptionsData(birthdate, endAge, withdrawMode, withdrawAmount);
    }

    public int update(RetirementOptionsData rod) {
        saveBirthdate(rod.getBirthdate());
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE, rod.getBirthdate());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_END_AGE, rod.getEndAge());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE, rod.getWithdrawMode());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT, rod.getWithdrawAmount());
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return mCR.update(uri, values, null, null);
    }

    public int saveBirthdate(String birthdate) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE, birthdate);
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return mCR.update(uri, values, null, null);
    }

    public String addAge(AgeData age) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.MilestoneEntry.COLUMN_AGE, age.getUnformattedString());
        Uri uri = mCR.insert(RetirementContract.MilestoneEntry.CONTENT_URI, values);
        if(uri == null) {
            return null;
        } else {
            return uri.getLastPathSegment();
        }
    }

    public int deleteAge(long id) {
        if(id == -1) {
            return 0;
        }
        String sid = String.valueOf(id);
        Uri uri = RetirementContract.MilestoneEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return mCR.delete(uri, null, null);
    }

    private Cursor query() {
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return mCR.query(uri, null, null, null, null);
    }
}
