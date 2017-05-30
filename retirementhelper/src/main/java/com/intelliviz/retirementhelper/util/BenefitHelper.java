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
        List<MilestoneData> milestones = getMilestonesNew(context, Double.parseDouble(bd.get(0).getBalance()),
                Double.parseDouble(tdid.getInterest()),
                Double.parseDouble(tdid.getMonthAddition()), Double.parseDouble(tdid.getPenalty()), tdid.getMinimumAge());
        return milestones;
    }

    /**
     * Calculate the milestones based on balance decrease; ie living off the interest.
     * @param context
     * @param balance
     * @param interest
     * @param monthlyIncrease
     * @return
     */
    private static List<MilestoneData> getMilestones(Context context, double balance, double interest, double monthlyIncrease) {
        List<AgeData> ages = getMilestoneAges(context);
        List<MilestoneData> milestones = new ArrayList<>();
        AgeData refAge = null;
        double monthlyAmount = 0;
        double newBalance = 0;
        for(int i = 0; i < ages.size(); i++) {
            if(i == 0) {
                newBalance = getBalance(balance, 0, interest, monthlyIncrease);
                monthlyAmount = getMonthlyAmountFromBalance(newBalance, interest);
                milestones.add(new MilestoneData(ages.get(0), Double.toString(monthlyAmount), Double.toString(newBalance)));
                refAge = ages.get(0);
            } else {
                AgeData age = ages.get(i);
                AgeData diffAge = age.subtract(refAge);
                int numMonths = diffAge.getNumberOfMonths();
                newBalance = getBalance(newBalance, numMonths, interest, monthlyIncrease);
                monthlyAmount = getMonthlyAmountFromBalance(newBalance, interest);
                milestones.add(new MilestoneData(age, Double.toString(monthlyAmount), Double.toString(newBalance)));
                refAge = age;
            }
        }

        return milestones;
    }

    private static List<MilestoneData> getMilestonesNew(Context context, double balance, double interest, double monthlyIncrease, double penalty, String minAge) {
        List<MilestoneData> milestones = new ArrayList<>();
        List<AgeData> ages = getMilestoneAges(context);
        if(ages.isEmpty()) {
            return milestones;
        }
        AgeData minimumAge = new AgeData(minAge);
        List<Double> balances = getBalances(context, balance, interest, monthlyIncrease);

        for(int i = 0; i < ages.size(); i++) {
            double newBalance = balances.get(i);
            double monthlyAmount = getMonthlyAmountFromBalance(newBalance, interest);
            boolean penaltyApplies = false;
            if (ages.get(i).isBefore(minimumAge)) {
                double percent = 100 - penalty;
                percent = percent / 100;
                monthlyAmount = monthlyAmount * percent;
                penaltyApplies = true;
            }

            milestones.add(new MilestoneData(ages.get(i), Double.toString(monthlyAmount), Double.toString(newBalance), penaltyApplies));
        }
        return milestones;
    }

    /**
     * Get the balances for each milestone.
     * @param context The activity context.
     * @param balance The beginning balance.
     * @param interest The annual interest rate.
     * @param monthlyIncrease The monthly increase.
     * @return The list of balances.
     */
    private static List<Double> getBalances(Context context, double balance, double interest, double monthlyIncrease) {
        List<AgeData> ages = getMilestoneAges(context);
        List<Double> balances = new ArrayList<>();
        if(ages.isEmpty()) {
            return balances;
        }
        AgeData refAge = ages.get(0);
        double newBalance = balance;
        balances.add(newBalance);
        for (int i = 1; i < ages.size(); i++) {
            AgeData age = ages.get(i);
            AgeData diffAge = age.subtract(refAge);
            int numMonths = diffAge.getNumberOfMonths();
            newBalance = getBalance(newBalance, numMonths, interest, monthlyIncrease);
            balances.add(newBalance);
            refAge = age;
        }

        return balances;
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
