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

import static com.intelliviz.retirementhelper.util.DataBaseUtils.getMilestoneAges;

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

    public static List<MilestoneData> getAllMilestones(Context context, RetirementOptionsData rod) {
        List<MilestoneData> sumMilestones = new ArrayList<>();
        List<IncomeType> incomeTypes = DataBaseUtils.getAllIncomeTypes(context);
        if(incomeTypes == null || incomeTypes.isEmpty()) {
            List<MilestoneAgeData> milestoneAges = getMilestoneAges(context, rod);
            for(MilestoneAgeData msad : milestoneAges) {
                MilestoneData msd = new MilestoneData(msad.getAge());
                sumMilestones.add(msd);
            }
            return sumMilestones;
        }
        List<MilestoneAgeData> msad = getMilestoneAges(context, rod);
        if(msad.isEmpty()) {
            return sumMilestones;
        }

        double[] sumMonthlyAmount = new double[msad.size()];
        double[] sumStartBalance = new double[msad.size()];
        double[] sumEndBalance = new double[msad.size()];
        for(int i = 0; i < msad.size(); i++) {
            sumMonthlyAmount[i] = 0;
            sumStartBalance[i] = 0;
            sumEndBalance[i] = 0;
        }

        int numMonthsFundsWillLast = 0;
        List<MilestoneData> saveMilestones = null;
        for(IncomeType incomeType : incomeTypes) {
            List<MilestoneData> milestones = incomeType.getMilestones(context, msad, rod);
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
        for(int i = 0; i < msad.size(); i++) {
            AgeData startAge = msad.get(i).getAge();
            MilestoneData milestoneData = new MilestoneData(startAge, endAge, minimumAge, sumMonthlyAmount[i], sumStartBalance[i], sumEndBalance[i], 0, numMonthsFundsWillLast);
            sumMilestones.add(milestoneData);
        }

        return sumMilestones;
    }
}
