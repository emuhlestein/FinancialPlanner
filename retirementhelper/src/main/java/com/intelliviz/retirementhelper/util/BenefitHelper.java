package com.intelliviz.retirementhelper.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 5/23/2017.
 */

public class BenefitHelper {
    public static List<MilestoneData> getMilestones(Context context, IncomeType incomeType) {
        if(incomeType instanceof SavingsIncomeData) {
            return getMilestonesFromSavingsIncome(context, (SavingsIncomeData)incomeType);
        } else if(incomeType instanceof TaxDeferredIncomeData) {
            return getMilestonesFromTaxDeferredIncome(context, (TaxDeferredIncomeData)incomeType);
        } else {
            return null; //"0.00";
        }
    }

    private static List<MilestoneData> getMilestonesFromSavingsIncome(Context context, SavingsIncomeData sid) {
        List<BalanceData> bd = sid.getBalanceDataList();
        return getMilestones(context, Double.parseDouble(bd.get(0).getBalance()),
                Double.parseDouble(sid.getInterest()),
                Double.parseDouble(sid.getMonthlyIncrease()));
    }

    private static List<MilestoneData> getMilestonesFromTaxDeferredIncome(Context context, TaxDeferredIncomeData tdid) {
        List<BalanceData> bd = tdid.getBalanceDataList();
        List<MilestoneData> milestones = getMilestones(context, Double.parseDouble(bd.get(0).getBalance()),
                Double.parseDouble(tdid.getInterest()),
                Double.parseDouble(tdid.getMonthAddition()));
        if(tdid.getIs401k() == 1) {
            AgeData minAge = new AgeData(tdid.getMinimumAge());
            String penalty = tdid.getPenalty();
            double dpenalty = Double.parseDouble(penalty);
            double percent = 100 - dpenalty;
            percent = percent / 100;

            for(int i = 0; i < milestones.size(); i++) {
                MilestoneData msd = milestones.get(i);
                if (msd.getAge().onOrBefore(minAge)) {
                    String amount = msd.getAmount();
                    double damount = Double.parseDouble(amount) * percent;
                    MilestoneData newMsd = new MilestoneData(msd.getAge(), Double.toString(damount), msd.getBalance());
                    milestones.set(i, newMsd);
                }
            }
        }
        return milestones;
    }

    private static List<MilestoneData> getMilestones(Context context, double balance, double interest, double monthlyIncrease) {
        List<AgeData> ages = getMilestoneAges(context);
        List<MilestoneData> milestones = new ArrayList<>();
        AgeData refAge = null;
        double monthlyAmount = 0;
        for(int i = 0; i < ages.size(); i++) {
            if(i == 0) {
                double newBalance = getBalance(balance, 0, interest, monthlyIncrease);
                monthlyAmount = getMonthlyAmountFromBalance(newBalance, interest);
                milestones.add(new MilestoneData(ages.get(0), Double.toString(monthlyAmount), Double.toString(newBalance)));
                refAge = ages.get(0);
            } else {
                AgeData age = ages.get(i);
                AgeData diffAge = age.subtract(refAge);
                int numMonths = diffAge.getNumberOfMonths();
                double newBalance = getBalance(balance, numMonths, interest, monthlyIncrease);
                monthlyAmount = getMonthlyAmountFromBalance(newBalance, interest);
                milestones.add(new MilestoneData(age, Double.toString(monthlyAmount), Double.toString(newBalance)));
                refAge = age;
            }
        }

        return milestones;
    }

    public static List<AgeData> getMilestoneAges(Context context) {
        List<AgeData> milestones = new ArrayList<>();
        PersonalInfoData pid = DataBaseUtils.getPersonalInfoData(context);
        AgeData nowAge = SystemUtils.getAge(pid.getBirthdate());

        milestones.add(nowAge);
        milestones.add(new AgeData(59, 6));
        milestones.add(new AgeData(62, 0)); // minimum age to receive benefit
        milestones.add(new AgeData(65, 0));
        milestones.add(new AgeData(66, 8)); // Full retirement age
        milestones.add(new AgeData(70, 0));
        return milestones;
    }

    private static double getBalance(double balance, int numMonths, double interest, double monthlyIncrease) {
        double cumulativeBalance = balance;
        for(int i = 0; i < numMonths; i++) {
            cumulativeBalance = getBalance(cumulativeBalance, interest, monthlyIncrease);
        }
        return cumulativeBalance;
    }

    private static double getBalance(double balance, double interest, double monthlyIncrease) {
        double interestEarned = getMonthlyAmountFromBalance(balance, interest);
        return monthlyIncrease + interestEarned + balance;
    }

    /**
     * Calculate the monthly amount from the specified balance and annual interest rate.
     * @param balance The balance.
     * @param interest The annual interest rate.
     * @return The monthly amount.
     */
    private static double getMonthlyAmountFromBalance(double balance, double interest) {
        double monthlyInterest = interest / 1200.0;
        return balance * monthlyInterest;
    }
}
