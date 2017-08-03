package com.intelliviz.retirementhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.IncomeType;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.db.RetirementContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class.
 * Created by edm on 5/23/2017.
 */

public class BenefitHelper {

    public static void updateStatus(Context context, int status, int action, String result, int incomeType) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.TransactionStatusEntry.COLUMN_STATUS, status);
        values.put(RetirementContract.TransactionStatusEntry.COLUMN_ACTION, action);
        values.put(RetirementContract.TransactionStatusEntry.COLUMN_RESULT, result);
        values.put(RetirementContract.TransactionStatusEntry.COLUMN_TYPE, incomeType);
        Uri uri = RetirementContract.TransactionStatusEntry.CONTENT_URI;
        context.getContentResolver().update(uri, values, null, null);
    }

    public static List<MilestoneData> getAllMilestones(Context context, List<MilestoneAgeData> ages, RetirementOptionsData rod) {
        List<MilestoneData> sumMilestones = new ArrayList<>();
        List<IncomeType> incomeTypes = DataBaseUtils.getAllIncomeTypes(context);
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
            List<MilestoneData> milestones = incomeType.getMilestones(context, ages, rod);
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
}
