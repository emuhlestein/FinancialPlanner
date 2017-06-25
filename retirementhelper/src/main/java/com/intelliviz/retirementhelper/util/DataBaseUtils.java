package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BalanceData;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.IncomeType;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.data.SummaryData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intelliviz.retirementhelper.R.string.balance;
import static com.intelliviz.retirementhelper.util.BenefitHelper.getAllMilestones;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;

/**
 * Created by edm on 4/25/2017.
 */

public class DataBaseUtils {

    public static String addAge(Context context, AgeData age) {
        String amount = Double.toString(balance);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.MilestoneEntry.COLUMN_AGE, age.getUnformattedString());
        Uri uri = context.getContentResolver().insert(RetirementContract.MilestoneEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    public static int deleteAge(Context context, long id) {
            String sid = String.valueOf(id);
            Uri uri = RetirementContract.MilestoneEntry.CONTENT_URI;
            uri = Uri.withAppendedPath(uri, sid);
            int numRowsDeleted = context.getContentResolver().delete(uri, null, null);
            return numRowsDeleted; // TODO return succeed or fail flag
    }

    public static List<MilestoneAgeData> getMilestoneAges(Context context, PersonalInfoData pid) {
        List<MilestoneAgeData> ages = new ArrayList<>();
        Uri uri = RetirementContract.MilestoneEntry.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if(cursor == null || !cursor.moveToFirst()) {
            return ages;
        }
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(RetirementContract.MilestoneEntry._ID);
            int ageIndex = cursor.getColumnIndex(RetirementContract.MilestoneEntry.COLUMN_AGE);
            long id = cursor.getLong(idIndex);
            String ageString = cursor.getString(ageIndex);
            AgeData age = SystemUtils.parseAgeString(ageString);
            ages.add(new MilestoneAgeData(id, age));
        }
        cursor.close();

        Collections.sort(ages);
        return ages;
    }

    public static void updateSummaryData(Context context) {
        RetirementOptionsData rod = getRetirementOptionsData(context);
        PersonalInfoData perid = getPersonalInfoData(context);
        List<MilestoneData> milestones = getAllMilestones(context, rod, perid);
        List<SummaryData> listSummaryData = new ArrayList<>();
        for(MilestoneData msd : milestones) {
            listSummaryData.add(new SummaryData(msd.getStartAge().toString(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
        }
        Uri uri = RetirementContract.SummaryEntry.CONTENT_URI;
        int numRowsDeleted = context.getContentResolver().delete(uri, null, null);
        for(SummaryData summaryData : listSummaryData) {
            ContentValues values = new ContentValues();
            values.put(RetirementContract.SummaryEntry.COLUMN_AGE, summaryData.getAge().toString());
            values.put(RetirementContract.SummaryEntry.COLUMN_AMOUNT, summaryData.getMonthlyBenefit());
            uri = context.getContentResolver().insert(RetirementContract.SummaryEntry.CONTENT_URI, values);
        }
    }

    public static List<SummaryData> getSummaryData(Context context) {
        Uri uri = RetirementContract.SummaryEntry.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if(cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }
        List<SummaryData> summaryData = new ArrayList<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int ageIndex = cursor.getColumnIndex(RetirementContract.SummaryEntry.COLUMN_AGE);
            int amountIndex = cursor.getColumnIndex(RetirementContract.SummaryEntry.COLUMN_AMOUNT);
            String age = cursor.getString(ageIndex);
            String amount = cursor.getString(amountIndex);
            summaryData.add(new SummaryData(age, amount));
        }

        return summaryData;
    }

    public static List<IncomeType> getAllIncomeTypes(Context context) {
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if(cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }

        List<IncomeType> incomeTypes = new ArrayList<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry._ID);
            int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);
            long id = Long.parseLong(cursor.getString(idIndex));
            int type = Integer.parseInt(cursor.getString(typeIndex));
            switch(type) {
                case INCOME_TYPE_SAVINGS:
                    SavingsIncomeData sid = SavingsHelper.getSavingsIncomeData(context, id);
                    incomeTypes.add(sid);
                    break;
                case INCOME_TYPE_TAX_DEFERRED:
                    TaxDeferredIncomeData tdid = TaxDeferredHelper.getTaxDeferredIncomeData(context, id);
                    incomeTypes.add(tdid);
                    break;
                case INCOME_TYPE_PENSION:
                    PensionIncomeData pid = PensionHelper.getPensionIncomeData(context, id);
                    incomeTypes.add(pid);
                    break;
                case INCOME_TYPE_GOV_PENSION:
                    GovPensionIncomeData gpid = GovPensionHelper.getGovPensionIncomeData(context, id);
                    incomeTypes.add(gpid);
                    break;
            }
        }
        cursor.close();

        return incomeTypes;
    }
    //
    // Methods for personal info table
    //

    public static int savePersonalInfo(Context context, PersonalInfoData pid) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.PersonalInfoEntry.COLUMN_BIRTHDATE, pid.getBirthdate());
        Uri uri = RetirementContract.PersonalInfoEntry.CONTENT_URI;
        return context.getContentResolver().update(uri, values, null, null);
    }

    public static PersonalInfoData getPersonalInfoData(Context context) {
        Cursor cursor = getPersonalInfo(context);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int birthdateIndex = cursor.getColumnIndex(RetirementContract.PersonalInfoEntry.COLUMN_BIRTHDATE);

        String birthdate = cursor.getString(birthdateIndex);
        return new PersonalInfoData(birthdate, null);
    }

    public static Cursor getPersonalInfo(Context context) {
        Uri uri = RetirementContract.PersonalInfoEntry.CONTENT_URI;
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    //
    // Methods for retirement options table
    //
    public static int saveRetirementOptions(Context context, RetirementOptionsData rod) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_END_AGE, rod.getEndAge());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE, rod.getWithdrawMode());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT, rod.getWithdrawAmount());
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return context.getContentResolver().update(uri, values, null, null);
    }

    public static int saveRetirementOptions(Context context, String startAge, String endAge, int withdrawMode, String withdrawAmount) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_END_AGE, endAge);
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE, withdrawMode);
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT, withdrawAmount);
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return context.getContentResolver().update(uri, values, null, null);
    }

    private static Cursor getRetirementOptions(Context context) {
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public static RetirementOptionsData getRetirementOptionsData(Context context) {
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
        return new RetirementOptionsData(endAge, withdrawMode, withdrawAmount);
    }

    //
    // Methods for IncomeType table
    //
    public static String addIncomeType(Context context, IncomeType incomeType) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_NAME, incomeType.getName());
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_TYPE, incomeType.getType());
        Uri uri = context.getContentResolver().insert(RetirementContract.IncomeTypeEntry.CONTENT_URI, values);
        if(uri == null) {
            return null;
        } else {
            return uri.getLastPathSegment();
        }
    }

    public static int saveIncomeType(Context context, IncomeType incomeType) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_NAME, incomeType.getName());
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_TYPE, incomeType.getType());

        String sid = String.valueOf(incomeType.getId());
        String selectionClause = RetirementContract.IncomeTypeEntry._ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    public static Cursor getIncomeType(Context context, String name) {
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        String selection = RetirementContract.IncomeTypeEntry.COLUMN_NAME+ " = ?";
        String[] selectionArgs = {name};
        return context.getContentResolver().query(uri, null, selection, selectionArgs, null);
    }

    private static Cursor getIncomeType(Context context, long incomeId) {
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        String selection = RetirementContract.IncomeTypeEntry._ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, null, selection, selectionArgs, null);
    }

    static IncomeDataHelper getIncomeTypeData(Context context, long incomeId) {
        Cursor cursor = getIncomeType(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);
        String name = cursor.getString(nameIndex);
        int type = cursor.getInt(typeIndex);
        cursor.close();
        return new IncomeDataHelper(name, type);
    }

    static String addBalanceData(Context context, long incomeId, double balance, String date) {
        String amount = Double.toString(balance);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.BalanceEntry.COLUMN_AMOUNT, amount);
        values.put(RetirementContract.BalanceEntry.COLUMN_DATE, date);
        Uri uri = context.getContentResolver().insert(RetirementContract.BalanceEntry.CONTENT_URI, values);
        return uri.getLastPathSegment();
    }

    static int saveBalanceData(Context context, long incomeId, double balance, String date) {
        String amount = Double.toString(balance);
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.BalanceEntry.COLUMN_AMOUNT, amount);
        values.put(RetirementContract.BalanceEntry.COLUMN_DATE, date);

        String sid = String.valueOf(incomeId);
        String selectionClause = RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.BalanceEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    private static Cursor getBalances(Context context, long incomeId) {
        Uri uri = RetirementContract.BalanceEntry.CONTENT_URI;
        String selection = RetirementContract.BalanceEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        String sortOrder = RetirementContract.BalanceEntry.COLUMN_DATE + " DESC";
        return context.getContentResolver().query(uri, null, selection, selectionArgs, sortOrder);
    }

    static BalanceData[] getBalanceData(Context context, long incomeId) {
        Cursor cursor = getBalances(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        BalanceData bd[] = new BalanceData[cursor.getCount()];
        int index = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int balanceIndex = cursor.getColumnIndex(RetirementContract.BalanceEntry.COLUMN_AMOUNT);
            int dateIndex = cursor.getColumnIndex(RetirementContract.BalanceEntry.COLUMN_DATE);
            String amount = cursor.getString(balanceIndex);
            String date = cursor.getString(dateIndex);

            double balance = Double.parseDouble(amount);
            bd[index++] = new BalanceData(balance, date);
        }
        cursor.close();
        return bd;
    }

    public static int deleteBalance(Context context, long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.BalanceEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().delete(uri, null, null);
    }

    static class IncomeDataHelper {
        public String name;
        public int type;
        public IncomeDataHelper(String name, int type) {
            this.name = name;
            this.type = type;
        }
    }

    public static Cursor getMilestones(Context context) {
        Uri uri = RetirementContract.MilestoneEntry.CONTENT_URI;
        String[] projection = null; // we want all columns
        return context.getContentResolver().query(uri, projection, null, null, null);
    }
}
