package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.BalanceData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

import java.util.List;

import static com.intelliviz.retirementhelper.util.DataBaseUtils.addBalanceData;
import static com.intelliviz.retirementhelper.util.DataBaseUtils.addIncomeType;
import static com.intelliviz.retirementhelper.util.DataBaseUtils.getBalanceData;
import static com.intelliviz.retirementhelper.util.DataBaseUtils.getIncomeTypeData;
import static com.intelliviz.retirementhelper.util.DataBaseUtils.saveBalanceData;
import static com.intelliviz.retirementhelper.util.DataBaseUtils.saveIncomeType;

/**
 * Utility class for savings.
 * Created by Ed Muhlestein on 6/10/2017.
 */
public class SavingsHelper {
    public static String addSavingsIncome(Context context, SavingsIncomeData sid) {
        String id = addIncomeType(context, sid);
        if(id == null) {
            return null;
        }

        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD, sid.getMonthlyIncrease());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST, sid.getInterest());
        context.getContentResolver().insert(RetirementContract.SavingsIncomeEntry.CONTENT_URI, values);

        List<BalanceData> balanceDataList = sid.getBalanceDataList();
        for(BalanceData bd : balanceDataList) {
            addBalanceData(context, incomeId, bd.getBalance(), bd.getDate());
        }
        return "";
    }

    public static int saveSavingsIncomeData(Context context, SavingsIncomeData sid) {
        DataBaseUtils.updateIncomeTypeName(context, sid);
        saveIncomeType(context, sid);
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD, sid.getMonthlyIncrease());
        values.put(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST, sid.getInterest());

        String id = String.valueOf(sid.getId());
        Uri uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        context.getContentResolver().update(uri, values, null, null);
        List<BalanceData> balanceDataList = sid.getBalanceDataList();
        for(BalanceData bd : balanceDataList) {
            saveBalanceData(context, sid.getId(), bd.getBalance(), bd.getDate());
        }

        return 0;
    }

    private static Cursor getSavingsIncome(Context context, long incomeId) {
        String id = String.valueOf(incomeId);
        Uri uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, id);
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public static SavingsIncomeData getSavingsIncomeData(Context context, long incomeId) {
        DataBaseUtils.IncomeDataHelper idh = getIncomeTypeData(context, incomeId);
        if(idh == null) {
            return null;
        }
        Cursor cursor = getSavingsIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int interestIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_INTEREST);
        int monthAddIndex = cursor.getColumnIndex(RetirementContract.SavingsIncomeEntry.COLUMN_MONTH_ADD);
        double interest = Double.parseDouble(cursor.getString(interestIndex));
        double monthAdd = Double.parseDouble(cursor.getString(monthAddIndex));
        SavingsIncomeData sid = new SavingsIncomeData(incomeId, idh.name, idh.type, interest, monthAdd);

        BalanceData[] bds = getBalanceData(context, incomeId);
        if(bds != null) {
            for (BalanceData bd : bds) {
                sid.addBalance(bd);
            }
        }

        return sid;
    }

    public static int deleteSavingsIncome(Context context, long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        context.getContentResolver().delete(uri, null, null);
        uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        context.getContentResolver().delete(uri, null, null);
        uri = RetirementContract.BalanceEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().delete(uri, null, null);
    }
}
