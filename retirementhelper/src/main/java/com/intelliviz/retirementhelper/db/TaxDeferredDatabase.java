package com.intelliviz.retirementhelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.IncomeTypeData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;

/**
 * Created by edm on 9/30/2017.
 */

public class TaxDeferredDatabase extends BaseDatabase {
    private volatile static TaxDeferredDatabase mINSTANCE;

    public static TaxDeferredDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (TaxDeferredDatabase.class) {
                if(mINSTANCE == null) {

                    mINSTANCE = new TaxDeferredDatabase(context);
                }
            }
        }
        return mINSTANCE;
    }

    private TaxDeferredDatabase(Context context) {
        super(context.getContentResolver());
    }

    public long insert(IncomeTypeData data) {
        if(!(data instanceof TaxDeferredIncomeData)) {
            return 0;
        }
        TaxDeferredIncomeData tdid = (TaxDeferredIncomeData)data;

        String id = addIncomeType(tdid);
        if(id == null) {
            return -1;
        }

        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE, tdid.getMinimumAge());
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST, Double.toString(tdid.getInterestRate()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD, Double.toString(tdid.getMonthAddition()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY, Double.toString(tdid.getPenalty()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE, Double.toString(tdid.getBalance()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, tdid.getIs401k());
        Uri uri = getContentResolver().insert(RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI, values);

        if(uri == null) {
            return -1;
        } else {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }

    public IncomeTypeData get(long incomeId) {
        IncomeData id = getData(incomeId);
        if(id == null) {
            return null;
        }

        Cursor cursor = query(incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int minAgeIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE);
        int interestIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST);
        int monthAddIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD);
        int penaltyIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY);
        int balanceIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE);
        int is401kIndex = cursor.getColumnIndex(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K);
        String minAge = cursor.getString(minAgeIndex);
        double interest = Double.parseDouble(cursor.getString(interestIndex));
        double monthAdd = Double.parseDouble(cursor.getString(monthAddIndex));
        double penalty = Double.parseDouble(cursor.getString(penaltyIndex));
        double balance = Double.parseDouble(cursor.getString(balanceIndex));
        int is401k = cursor.getInt(is401kIndex);
        cursor.close();
        return new TaxDeferredIncomeData(incomeId, id.type, id.name, minAge, interest, monthAdd, penalty, balance, is401k);
    }

    public int update(IncomeTypeData data) {
        if(!(data instanceof TaxDeferredIncomeData)) {
            return 0;
        }
        TaxDeferredIncomeData tdid = (TaxDeferredIncomeData)data;

        updateIncomeTypeName(tdid);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MIN_AGE, tdid.getMinimumAge());
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_INTEREST, Double.toString(tdid.getInterestRate()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_MONTH_ADD, Double.toString(tdid.getMonthAddition()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_PENALTY, Double.toString(tdid.getPenalty()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_BALANCE, Double.toString(tdid.getBalance()));
        values.put(RetirementContract.TaxDeferredIncomeEntry.COLUMN_IS_401K, tdid.getIs401k());

        String id = String.valueOf(tdid.getId());
        Uri uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        return getContentResolver().update(uri, values, null, null);
    }

    public int delete(long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        getContentResolver().delete(uri, null, null);
        uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return getContentResolver().delete(uri, null, null);
    }

    private Cursor query(long incomeId) {
        Uri uri = RetirementContract.TaxDeferredIncomeEntry.CONTENT_URI;
        String sid = String.valueOf(incomeId);
        uri = uri.withAppendedPath(uri, sid);
        return getContentResolver().query(uri, null, null, null, null);
    }
}
