package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.IncomeType;
import com.intelliviz.retirementhelper.data.IncomeTypeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.SummaryEntity;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.services.MilestoneSummaryIntentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;

/**
 * Utility class for database access.
 * Created by edm on 4/25/2017.
 */

public class DataBaseUtils {

    public static List<MilestoneData> getAllMilestones(AppDatabase mDB) {
        List<IncomeSourceEntityBase> allEntities = getAllIncomeEntities(mDB);
        RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
        List<MilestoneAgeEntity> ages = getMilestoneAges(mDB);
        return getAllMilestones(allEntities, ages, rod);
    }

    public static List<IncomeSourceEntityBase> getAllIncomeEntities(AppDatabase mDB) {
        List<IncomeSourceEntityBase> allEntities = new ArrayList<>();
        List<GovPensionEntity> govEntities = mDB.govPensionDao().get();
        if(!govEntities.isEmpty()) {
            allEntities.addAll(govEntities);
        }
        List<PensionIncomeEntity> pensionEntities = mDB.pensionIncomeDao().get();
        if(!pensionEntities.isEmpty()) {
            allEntities.addAll(pensionEntities);
        }
        List<SavingsIncomeEntity> savingsEntities = mDB.savingsIncomeDao().get();
        if(!savingsEntities.isEmpty()) {
            allEntities.addAll(savingsEntities);
        }
        List<TaxDeferredIncomeEntity> taxDefEntities = mDB.taxDeferredIncomeDao().get();
        if(!taxDefEntities.isEmpty()) {
            allEntities.addAll(taxDefEntities);
        }
        return allEntities;
    }

    public static List<MilestoneAgeEntity> getMilestoneAges(AppDatabase mDB) {
        List<IncomeSourceEntityBase> incomeTypes = getAllIncomeEntities(mDB);
        List<MilestoneAgeEntity> ageEntities = mDB.milestoneAgeDao().getAges();
        RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();

        HashSet<MilestoneAgeEntity> ages = new HashSet<>();
        for(MilestoneAgeEntity age : ageEntities) {
            long id = age.getId();
            //String ageString = age.getAge();
            //AgeData ageData = SystemUtils.parseAgeString(ageString);
            ages.add(new MilestoneAgeEntity(id, age.getAge()));
        }

        // add ages from income types
        for(IncomeSourceEntityBase incomeType : incomeTypes) {
            List<AgeData> allAges = incomeType.getAges();
            for(AgeData anAge : allAges) {
                ages.add(new MilestoneAgeEntity(-1, anAge));
            }
        }

        String birthdate = rod.getBirthdate();
        if(SystemUtils.validateBirthday(birthdate)) {
            // add birth date
            AgeData nowAge = SystemUtils.getAge(birthdate);
            ages.add(new MilestoneAgeEntity(-1, nowAge));

            // add full retirement age
            //int year = SystemUtils.getBirthYear(birthdate);
            //AgeData fullRetirementAge = GovPensionHelper.getFullRetirementAge(year);
            //ages.add(new MilestoneAgeData(-1, fullRetirementAge));
        }

        // Need to get this age from deferred tax income source
        //AgeData min401kAge = new AgeData(59, 6);
        //ages.add(new MilestoneAgeData(-1, min401kAge));

        List<MilestoneAgeEntity> sortedAges = new ArrayList<>(ages);
        Collections.sort(sortedAges);

        return sortedAges;
    }

    public static String extractBirthDate(Cursor cursor) {
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        int birthdateIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE);
        if(birthdateIndex != -1) {
            String birthDate = cursor.getString(birthdateIndex);
            if(SystemUtils.validateBirthday(birthDate)) {
                return birthDate;
            }
        }
        return null;
    }
/*
    public static void updateMilestoneSummary(Context context) {
        ContentResolver cr = context.getContentResolver();

        RetirementOptionsData rod = RetirementOptionsDatabase.getInstance(context).get();
        List<MilestoneAgeData> ages = DataBaseUtils.getMilestoneAges(context, rod);
        List<MilestoneData> milestoneDataList = DataBaseUtils.getAllMilestones(context, ages, rod);

        // delete all milestone summary tables, then re-add them.
        Uri uri = RetirementContract.MilestoneSummaryEntry.CONTENT_URI;
        cr.delete(uri, null, null);

        for (MilestoneData milestoneData : milestoneDataList) {
            ContentValues values = new ContentValues();
            values.put(RetirementContract.MilestoneSummaryEntry.COLUMN_START_AGE, milestoneData.getStartAge().getUnformattedString());
            values.put(RetirementContract.MilestoneSummaryEntry.COLUMN_END_AGE, milestoneData.getEndAge().getUnformattedString());
            values.put(RetirementContract.MilestoneSummaryEntry.COLUMN_MINIMUM_AGE, milestoneData.getMinimumAge().getUnformattedString());
            String monthlyBenefit = Double.toString(milestoneData.getMonthlyBenefit());
            values.put(RetirementContract.MilestoneSummaryEntry.COLUMN_MONTHLY_BENEFIT, monthlyBenefit);
            String startBalance = Double.toString(milestoneData.getStartBalance());
            values.put(RetirementContract.MilestoneSummaryEntry.COLUMN_START_BALANCE, startBalance);
            String endBalance = Double.toString(milestoneData.getEndBalance());
            values.put(RetirementContract.MilestoneSummaryEntry.COLUMN_END_BALANCE, endBalance);
            String penaltyAmount = Double.toString(milestoneData.getPenaltyAmount());
            values.put(RetirementContract.MilestoneSummaryEntry.COLUMN_PENALTY_AMOUNT, penaltyAmount);
            values.put(RetirementContract.MilestoneSummaryEntry.COLUMN_MONTHS, milestoneData.getMonthsFundsFillLast());
            cr.insert(RetirementContract.MilestoneSummaryEntry.CONTENT_URI, values);
        }
    }
*/

    public static void updateMilestoneData(Context context) {
        Intent intent = new Intent(context, MilestoneSummaryIntentService.class);
        context.startService(intent);
    }

    public static void updateStatus(Context context, int status, int action, String result, int incomeType) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TransactionStatusEntry.COLUMN_STATUS, status);
        values.put(RetirementContract.TransactionStatusEntry.COLUMN_ACTION, action);
        values.put(RetirementContract.TransactionStatusEntry.COLUMN_RESULT, result);
        values.put(RetirementContract.TransactionStatusEntry.COLUMN_TYPE, incomeType);
        Uri uri = RetirementContract.TransactionStatusEntry.CONTENT_URI;
        context.getContentResolver().update(uri, values, null, null);
    }

    public static List<MilestoneData> getAllMilestones(List<IncomeSourceEntityBase> incomeTypes, List<MilestoneAgeEntity> ages, RetirementOptionsEntity rod) {
        List<MilestoneData> sumMilestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return sumMilestones;
        }

        if(incomeTypes == null || incomeTypes.isEmpty()) {

            for(MilestoneAgeEntity msad : ages) {
                MilestoneData msd = new MilestoneData(msad.getAge());
                sumMilestones.add(msd);
            }
            return sumMilestones;
        }

        double[] sumMonthlyAmount = new double[ages.size()];
        double[] sumStartBalance = new double[ages.size()];
        double[] sumEndBalance = new double[ages.size()];
        for(int i = 0; i < ages.size(); i++) {
            sumMonthlyAmount[i] = 0;
            sumStartBalance[i] = 0;
            sumEndBalance[i] = 0;
        }

        int numMonthsFundsWillLast = 0;
        List<MilestoneData> saveMilestones = null;
        for(IncomeSourceEntityBase incomeType : incomeTypes) {
            List<MilestoneData> milestones = incomeType.getMilestones(ages, rod);
            if(milestones == null || milestones.isEmpty()) {
                continue;
            }

            if(saveMilestones == null) {
                saveMilestones = milestones;
            }

            double monthlyAmount;
            double startBalance;
            double endBalance;
            for(int i = 0; i < milestones.size(); i++) {
                MilestoneData milestoneData = milestones.get(i);
                monthlyAmount = milestoneData.getMonthlyBenefit();
                sumMonthlyAmount[i] += monthlyAmount;
                startBalance = milestoneData.getStartBalance();
                sumStartBalance[i] += startBalance;
                endBalance = milestoneData.getEndBalance();
                sumEndBalance[i] += endBalance;

                int numMonths = milestoneData.getMonthsFundsFillLast();
                if(numMonths > numMonthsFundsWillLast) {
                    numMonthsFundsWillLast = numMonths;
                }
            }
        }

        AgeData endAge = saveMilestones.get(0).getEndAge();
        AgeData minimumAge = saveMilestones.get(0).getMinimumAge();
        for(int i = 0; i < ages.size(); i++) {
            AgeData startAge = ages.get(i).getAge();
            MilestoneData milestoneData = new MilestoneData(startAge, endAge, minimumAge, sumMonthlyAmount[i], sumStartBalance[i], sumEndBalance[i], 0, numMonthsFundsWillLast);
            sumMilestones.add(milestoneData);
        }

        return sumMilestones;
    }

    public static List<MilestoneAgeEntity> getMilestoneAges(Context context, RetirementOptionsData rod) {

        // ages from data base
        Uri uri = RetirementContract.MilestoneEntry.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if(cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }

        HashSet<MilestoneAgeEntity> ages = new HashSet<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(RetirementContract.MilestoneEntry._ID);
            int ageIndex = cursor.getColumnIndex(RetirementContract.MilestoneEntry.COLUMN_AGE);
            long id = cursor.getLong(idIndex);
            String ageString = cursor.getString(ageIndex);
            AgeData age = SystemUtils.parseAgeString(ageString);
            ages.add(new MilestoneAgeEntity(id, age));
        }
        cursor.close();

        // add ages from income types
        List<IncomeType> incomeTypes = DataBaseUtils.getAllIncomeTypes(context);
        for(IncomeType incomeType : incomeTypes) {
            List<AgeData> allAges = incomeType.getAges();
            for(AgeData anAge : allAges) {
                ages.add(new MilestoneAgeEntity(-1, anAge));
            }
        }

        String birthdate = rod.getBirthdate();
        if(SystemUtils.validateBirthday(birthdate)) {
            // add birth date
            AgeData nowAge = SystemUtils.getAge(birthdate);
            ages.add(new MilestoneAgeEntity(-1, nowAge));

            // add full retirement age
            //int year = SystemUtils.getBirthYear(birthdate);
            //AgeData fullRetirementAge = GovPensionHelper.getFullRetirementAge(year);
            //ages.add(new MilestoneAgeData(-1, fullRetirementAge));
        }

        // Need to get this age from deferred tax income source
        //AgeData min401kAge = new AgeData(59, 6);
        //ages.add(new MilestoneAgeData(-1, min401kAge));

        List<MilestoneAgeEntity> sortedAges = new ArrayList<>(ages);
        Collections.sort(sortedAges);

        return sortedAges;
    }

    static void updateSummaryData(AppDatabase db) {
        List<SummaryEntity> summaries = db.summaryDao().get();
        db.summaryDao().delete(summaries);
        List<MilestoneData> milestones = getAllMilestones(db);
        for(MilestoneData msd : milestones) {
            db.summaryDao().insert(new SummaryEntity(-1, msd.getStartAge(), SystemUtils.getFormattedCurrency(msd.getMonthlyBenefit())));
        }
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
                    SavingsIncomeData sid = SavingsIncomeHelper.getData(context, id);
                    incomeTypes.add(sid);
                    break;
                case INCOME_TYPE_TAX_DEFERRED:
                    TaxDeferredIncomeData tdid = TaxDeferredHelper.getTaxDeferredIncomeData(context, id);
                    incomeTypes.add(tdid);
                    break;
                case INCOME_TYPE_PENSION:
                    PensionIncomeData pid = PensionHelper.getData(context, id);
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
    // Methods for IncomeType table
    //
    static String addIncomeType(Context context, IncomeTypeData incomeType) {
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

    static int updateIncomeTypeName(Context context, IncomeTypeData incomeType) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.IncomeTypeEntry.COLUMN_NAME, incomeType.getName());

        String sid = String.valueOf(incomeType.getId());
        String selectionClause = RetirementContract.IncomeTypeEntry._ID + " = ?";
        String[] selectionArgs = new String[]{sid};
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().update(uri, values, selectionClause, selectionArgs);
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

    static class IncomeDataHelper {
        public String name;
        public int type;
        public IncomeDataHelper(String name, int type) {
            this.name = name;
            this.type = type;
        }
    }
}
