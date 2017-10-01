package com.intelliviz.retirementhelper.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.IncomeType;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_GOV_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_SAVINGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.INCOME_TYPE_TAX_DEFERRED;

/**
 * Created by edm on 9/30/2017.
 */

public class MilestoneDatabase {
    private volatile static MilestoneDatabase mINSTANCE;
    private GovPensionDatabase mGovPensionDatabase;
    private PensionDatabase mPensionDatabase;
    private TaxDeferredDatabase mTaxDeferredDatabase;
    private SavingsDatabase mSavingsDatabase;
    private RetirementOptionsDatabase mROD;
    private ContentResolver mCR;

    public static MilestoneDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (MilestoneDatabase.class) {
                if(mINSTANCE == null) {

                    mINSTANCE = new MilestoneDatabase(context);
                }
            }
        }
        return mINSTANCE;
    }

    private MilestoneDatabase(Context context) {
        mCR = context.getContentResolver();
        mGovPensionDatabase = GovPensionDatabase.getInstance(context);
        mTaxDeferredDatabase = TaxDeferredDatabase.getInstance(context);
        mPensionDatabase = PensionDatabase.getInstance(context);
        mSavingsDatabase = SavingsDatabase.getInstance(context);
        mROD = RetirementOptionsDatabase.getInstance(context);
    }

    public List<MilestoneData> getList() {
        List<MilestoneAgeData> ages = getMilestoneAges(mROD.get());
        return getAllMilestones(ages, mROD.get());
    }

    public List<MilestoneData> getAllMilestones(List<MilestoneAgeData> ages, RetirementOptionsData rod) {
        List<MilestoneData> sumMilestones = new ArrayList<>();
        List<IncomeType> incomeTypes = getAllIncomeTypes();
        if(ages.isEmpty()) {
            return sumMilestones;
        }

        if(incomeTypes == null || incomeTypes.isEmpty()) {

            for(MilestoneAgeData msad : ages) {
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
        for(IncomeType incomeType : incomeTypes) {
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


    public List<IncomeType> getAllIncomeTypes() {
        Uri uri = RetirementContract.IncomeTypeEntry.CONTENT_URI;
        Cursor cursor = mCR.query(uri, null, null, null, null);
        if(cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }

        List<IncomeType> incomeTypes = new ArrayList<>();
        IncomeType incomeType;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry._ID);
            int typeIndex = cursor.getColumnIndex(RetirementContract.IncomeTypeEntry.COLUMN_TYPE);
            long id = Long.parseLong(cursor.getString(idIndex));
            int type = Integer.parseInt(cursor.getString(typeIndex));
            switch(type) {
                case INCOME_TYPE_SAVINGS:
                    incomeType = mSavingsDatabase.get(id);
                    break;
                case INCOME_TYPE_TAX_DEFERRED:
                    incomeType = mTaxDeferredDatabase.get(id);
                    break;
                case INCOME_TYPE_PENSION:
                    incomeType = mPensionDatabase.get(id);
                    break;
                case INCOME_TYPE_GOV_PENSION:
                    incomeType = mGovPensionDatabase.get(id);
                    break;
                default:
                    continue;
            }
            incomeTypes.add(incomeType);
        }
        cursor.close();

        return incomeTypes;
    }

    public List<MilestoneAgeData> getMilestoneAges(RetirementOptionsData rod) {

        // ages from data base
        Uri uri = RetirementContract.MilestoneEntry.CONTENT_URI;
        Cursor cursor = mCR.query(uri, null, null, null, null);
        if(cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }

        HashSet<MilestoneAgeData> ages = new HashSet<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(RetirementContract.MilestoneEntry._ID);
            int ageIndex = cursor.getColumnIndex(RetirementContract.MilestoneEntry.COLUMN_AGE);
            long id = cursor.getLong(idIndex);
            String ageString = cursor.getString(ageIndex);
            AgeData age = SystemUtils.parseAgeString(ageString);
            ages.add(new MilestoneAgeData(id, age));
        }
        cursor.close();

        // add ages from income types
        List<IncomeType> incomeTypes = getAllIncomeTypes();
        for(IncomeType incomeType : incomeTypes) {
            List<AgeData> allAges = incomeType.getAges();
            for(AgeData anAge : allAges) {
                ages.add(new MilestoneAgeData(-1, anAge));
            }
        }

        String birthdate = rod.getBirthdate();
        if(SystemUtils.validateBirthday(birthdate)) {
            // add birth date
            AgeData nowAge = SystemUtils.getAge(birthdate);
            ages.add(new MilestoneAgeData(-1, nowAge));

            // add full retirement age
            //int year = SystemUtils.getBirthYear(birthdate);
            //AgeData fullRetirementAge = GovPensionHelper.getFullRetirementAge(year);
            //ages.add(new MilestoneAgeData(-1, fullRetirementAge));
        }

        // Need to get this age from deferred tax income source
        //AgeData min401kAge = new AgeData(59, 6);
        //ages.add(new MilestoneAgeData(-1, min401kAge));

        List<MilestoneAgeData> sortedAges = new ArrayList<>(ages);
        Collections.sort(sortedAges);

        return sortedAges;
    }



}
