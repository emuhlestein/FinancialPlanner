package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;

import static com.intelliviz.retirementhelper.util.DataBaseUtils.getIncomeTypeData;

/**
 * Utility class for government pensions.
 * Created by edm on 6/12/2017.
 */

public class GovPensionHelper {
    private static double MAX_SS_PENALTY = 30.0;

    public static AgeData getFullRetirementAge(int birthYear) {
        AgeData fullAge;
        if(birthYear <= 1937) {
            fullAge = new AgeData(65, 0);
        } else if(birthYear == 1938) {
            fullAge = new AgeData(65, 2);
        } else if(birthYear == 1939) {
            fullAge = new AgeData(65, 4);
        }else if(birthYear == 1940) {
            fullAge = new AgeData(65, 6);
        } else if(birthYear == 1941) {
            fullAge = new AgeData(65, 8);
        } else if(birthYear == 19342) {
            fullAge = new AgeData(65, 10);
        } else if(birthYear >= 1939 && birthYear < 1955) {
            fullAge = new AgeData(66, 0);
        } else if(birthYear == 1955) {
            fullAge = new AgeData(66, 2);
        } else if(birthYear == 1956) {
            fullAge = new AgeData(66, 4);
        } else if(birthYear == 1957) {
            fullAge = new AgeData(66, 6);
        } else if(birthYear == 1958) {
            fullAge = new AgeData(66, 8);
        } else if(birthYear == 1959) {
            fullAge = new AgeData(66, 10);
        } else {
            fullAge = new AgeData(67, 0);
        }

        return fullAge;
    }

    /**
     * Get the percent credit per year.
     * @param birthyear The birth year.
     * @return THe delayed credit.
     */
    private static double getDelayedCredit(int birthyear) {
        if(birthyear < 1925) {
            return 3;
        } else if(birthyear < 1927) {
            return 3.5;
        } else if(birthyear < 1929) {
            return 4.0;
        } else if(birthyear < 1931) {
            return 4.5;
        } else if(birthyear < 1933 ) {
            return 5.0;
        } else if(birthyear < 1935) {
            return 5.5;
        } else if(birthyear < 1937) {
            return 6.0;
        } else if(birthyear < 1939) {
            return 6.5;
        } else if(birthyear < 1941) {
            return 7.0;
        } else if(birthyear < 1943) {
            return 7.5;
        } else {
            return 8.0; // the max
        }
    }

    static double getSocialSecurityAdjustment(String birthDate, AgeData startAge) {
        int year = SystemUtils.getBirthYear(birthDate);
        AgeData retireAge = getFullRetirementAge(year);
        AgeData diffAge = retireAge.subtract(startAge);
        int numMonths = diffAge.getNumberOfMonths();
        if(numMonths > 0) {
            // this is early retirement; the adjustment will be a penalty.
            if(numMonths < 37) {
                return (numMonths * 5.0) / 9.0;
            } else {
                double penalty = (numMonths * 5.0) / 12.0;
                if(penalty > MAX_SS_PENALTY) {
                    penalty = MAX_SS_PENALTY;
                }
                return penalty;
            }
        } else if(numMonths < 0) {
            // this is delayed retirement; the adjustment is a credit.
            double annualCredit = getDelayedCredit(year);
            return numMonths * (annualCredit / 12.0);
        } else {
            return 0; // exact retirement age
        }
    }

    public static String addGovPensionData(Context context, GovPensionIncomeData gpid) {
        String id = DataBaseUtils.addIncomeType(context, gpid);
        if(id == null) {
            return null;
        }
        long incomeId = Long.parseLong(id);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID, incomeId);
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(gpid.getMonthlyBenefit()));
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE, gpid.getStartAge());
        Uri uri = context.getContentResolver().insert(RetirementContract.GovPensionIncomeEntry.CONTENT_URI, values);
        if (uri == null) {
            return null;
        } else
            return uri.getLastPathSegment();
    }

    public static int saveGovPensionData(Context context, GovPensionIncomeData gpid) {
        DataBaseUtils.updateIncomeTypeName(context, gpid);
        ContentValues values = new ContentValues();
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT, Double.toString(gpid.getMonthlyBenefit()));
        values.put(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE, gpid.getStartAge());

        String sid = String.valueOf(gpid.getId());
        String selectionClause = RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
    }

    private static Cursor getGovPensionIncome(Context context, long incomeId) {
        Uri uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        String selection = RetirementContract.GovPensionIncomeEntry.COLUMN_INCOME_TYPE_ID + " = ?";
        String id = String.valueOf(incomeId);
        String[] selectionArgs = {id};
        return context.getContentResolver().query(uri, null, selection, selectionArgs, null);
    }

    public static GovPensionIncomeData getGovPensionIncomeData(Context context, long incomeId) {
        DataBaseUtils.IncomeDataHelper idh = getIncomeTypeData(context, incomeId);
        if(idh == null) {
            return null;
        }

        Cursor cursor = getGovPensionIncome(context, incomeId);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int minAgeIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT);
        String startAge = cursor.getString(minAgeIndex);
        String monthlyBenefit = cursor.getString(monthlyBenefitIndex);
        double amount = Double.parseDouble(monthlyBenefit);
        cursor.close();
        return new GovPensionIncomeData(incomeId, idh.name, idh.type, startAge, amount);
    }

    public static int deleteSavingsIncome(Context context, long incomeId) {
        String sid = String.valueOf(incomeId);
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        context.getContentResolver().delete(uri, null, null);
        uri = RetirementContract.GovPensionIncomeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().delete(uri, null, null);
    }

    public static GovPensionIncomeData extractData(Cursor cursor) {
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int incomeIdIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry._ID);
        int nameIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_NAME);
        int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);

        int minAgeIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MIN_AGE);
        int monthlyBenefitIndex = cursor.getColumnIndex(RetirementContract.GovPensionIncomeEntry.COLUMN_MONTH_BENEFIT);

        long incomeId = cursor.getLong(incomeIdIndex);
        String name = cursor.getString(nameIndex);
        int incomeType = cursor.getInt(typeIndex);

        String minAge = cursor.getString(minAgeIndex);
        double monthlyBenefit = Double.parseDouble(cursor.getString(monthlyBenefitIndex));

        return new GovPensionIncomeData(incomeId, name, incomeType, minAge, monthlyBenefit);
    }
}
