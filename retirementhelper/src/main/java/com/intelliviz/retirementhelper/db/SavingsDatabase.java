package com.intelliviz.retirementhelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.IncomeTypeData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;

/**
 * Created by edm on 9/30/2017.
 */

public class SavingsDatabase extends BaseDatabase {
    private volatile static SavingsDatabase mINSTANCE;

    public static SavingsDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (SavingsDatabase.class) {
                if(mINSTANCE == null) {

                    mINSTANCE = new SavingsDatabase(context);
                }
            }
        }
        return mINSTANCE;
    }

    private SavingsDatabase(Context context) {
        super(context.getContentResolver());
    }

    public long insert(IncomeTypeData data) {
        if(!(data instanceof GovPensionIncomeData)) {
            return 0;
        }
        SavingsIncomeData sid = (SavingsIncomeData)data;

        String id = addIncomeType(sid);
        if(id == null) {
            return -1;
        }

        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD, sid.getMonthlyIncrease());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST, sid.getInterest());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_BALANCE, Double.toString(sid.getBalance()));
        Uri uri = getContentResolver().insert(RetirementContract.SavingsIncomeEntry.CONTENT_URI, values);

        if(uri == null) {
            return -1;
        } else {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }

    public IncomeTypeData get(long incomeId) {
        IncomeData idh = getData(incomeId);
        if(idh == null) {
            return null;
        }

        Cursor cursor = query(incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        int interestIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST);
        int monthAddIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD);
        int balanceIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_BALANCE);
        double interest = Double.parseDouble(cursor.getString(interestIndex));
        double monthAdd = Double.parseDouble(cursor.getString(monthAddIndex));
        double balance = Double.parseDouble(cursor.getString(balanceIndex));
        return new SavingsIncomeData(incomeId, idh.name, idh.type, interest, monthAdd, balance);
    }

    public int update(IncomeTypeData data) {
        if(!(data instanceof SavingsIncomeData)) {
            return 0;
        }
        SavingsIncomeData sid = (SavingsIncomeData)data;

        updateIncomeTypeName(sid);
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD, sid.getMonthlyIncrease());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST, sid.getInterest());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_BALANCE, Double.toString(sid.getBalance()));

        String id = String.valueOf(sid.getId());
        Uri uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        return getContentResolver().update(uri, values, null, null);
    }

    public int delete(long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        getContentResolver().delete(uri, null, null);
        uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return getContentResolver().delete(uri, null, null);
    }

    private Cursor query(long incomeId) {
        String id = String.valueOf(incomeId);
        Uri uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        return getContentResolver().query(uri, null, null, null, null);
    }
}
