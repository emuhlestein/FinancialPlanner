package com.intelliviz.retirementhelper.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * Created by edm on 5/23/2017.
 */

public class BenefitHelper {

    public static List<MilestoneData> getMilestones(Context context, IncomeType incomeType, RetirementOptionsData rod) {
        if(incomeType instanceof SavingsIncomeData) {
            return getMilestonesFromSavingsIncome(context, (SavingsIncomeData)incomeType);
        } else if(incomeType instanceof TaxDeferredIncomeData) {
            return getMilestonesFromTaxDeferredIncome(context, (TaxDeferredIncomeData)incomeType, rod);
        } else {
            return null; //"0.00";
        }
    }

    private static List<MilestoneData> getMilestonesFromSavingsIncome(Context context, SavingsIncomeData sid) {
        List<BalanceData> bd = sid.getBalanceDataList();
        return getMilestones(context, parseDouble(bd.get(0).getBalance()),
                parseDouble(sid.getInterest()),
                parseDouble(sid.getMonthlyIncrease()));
    }

    private static List<MilestoneData> getMilestonesFromTaxDeferredIncome(Context context, TaxDeferredIncomeData tdid, RetirementOptionsData rod) {
        List<BalanceData> bd = tdid.getBalanceDataList();
        List<MilestoneData> milestones = getMilestonesNoPrincipleReduction(context, tdid, rod);
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
                //milestones.add(new MilestoneData(ages.get(0), Double.toString(monthlyAmount), Double.toString(newBalance)));
                refAge = ages.get(0);
            } else {
                AgeData age = ages.get(i);
                AgeData diffAge = age.subtract(refAge);
                int numMonths = diffAge.getNumberOfMonths();
                newBalance = getBalance(newBalance, numMonths, interest, monthlyIncrease);
                monthlyAmount = getMonthlyAmountFromBalance(newBalance, interest);
               // milestones.add(new MilestoneData(age, Double.toString(monthlyAmount), Double.toString(newBalance)));
                refAge = age;
            }
        }

        return milestones;
    }

    /**
     * Get the number of months the principle will last given the withdraw rate.
     * @param balance
     * @param interest
     * @param withdrawInterest
     * @param penalty
     * @return
     */
    private static int getNumMonthsByWidthdrawInterest(double balance, double interest, double withdrawInterest, double penalty) {
        double newBalance = balance;
        double monthlyAmount = getMonthlyAmountFromBalance(newBalance, withdrawInterest);
        int numMonths = 0;
        while(newBalance > 0) {
            newBalance = getMonthlyAmountFromBalance(newBalance, interest);
            newBalance = newBalance - monthlyAmount;
            numMonths++;
        }
        return numMonths;
    }

    /**
     * Get the milestones based on no loss of principle. Living off the interest only.
     * @return
     */
    private static List<MilestoneData> getMilestonesNoPrincipleReduction(Context context, TaxDeferredIncomeData tdid, RetirementOptionsData rod) {
        List<BalanceData> bd = tdid.getBalanceDataList();
        double balance = parseDouble(bd.get(0).getBalance());
        double interest = parseDouble(tdid.getInterest());
        double monthlyAddition = Double.parseDouble(tdid.getMonthAddition());
        double penalty = Double.parseDouble(tdid.getPenalty());
        String minAge = tdid.getMinimumAge();
        String endAge = rod.getEndAge();
        double withdrawPercent = Double.parseDouble(rod.getWithdrawPercent());
        List<MilestoneData> milestones = new ArrayList<>();
        List<AgeData> ages = getMilestoneAges(context);
        if(ages.isEmpty()) {
            return milestones;
        }
        AgeData minimumAge = new AgeData(minAge);
        AgeData endOfLifeAge = new AgeData(endAge);

        List<Double> balances = getMilestoneBalances(ages, balance, interest, monthlyAddition);

        for(int i = 0; i < ages.size(); i++) {
            AgeData mileStoneAge = ages.get(i);
            double milestoneBalance = balances.get(i);
            MilestoneData milestoneData = getMonthlyBalances(mileStoneAge, endOfLifeAge,
                    milestoneBalance, interest, withdrawPercent, penalty, minimumAge);
            milestones.add(milestoneData);
        }
        return milestones;
    }

    private static MilestoneData getMonthlyBalances(AgeData startAge, AgeData endAge,
                                                    double startBalance, double monthlyInterest, double initialWithdrawInterest,
                                                    double penalty, AgeData minimumAge) {
        AgeData age = endAge.subtract(startAge);
        int numMonthsInRetirement = age.getNumberOfMonths();
        double newBalance = startBalance;
        double monthInterest = monthlyInterest / 1200;
        double widthDrawInterest = initialWithdrawInterest / 1200;
        List<Double> balances = new ArrayList<>();
        double monthlyAmount = newBalance * widthDrawInterest;
        for(int mon = 0; mon < numMonthsInRetirement; mon++) {
            if(newBalance <= 0) {
                break;
            }

            newBalance = newBalance - monthlyAmount;
            double monthIncrease = newBalance * monthInterest;
            newBalance = newBalance + monthIncrease;
            balances.add(newBalance);
        }

        String penaltyAmount = "0.0";
        if(startAge.isBefore(minimumAge)) {
            penaltyAmount = Double.toString(penalty);
        }
        return new MilestoneData(startAge, endAge, Double.toString(monthlyAmount), Double.toString(startBalance), penaltyAmount, numMonthsInRetirement, balances);
    }

    /**
     * Get the balances for each milestone.
     * @param balance The beginning balance.
     * @param interest The annual interest rate.
     * @param monthlyIncrease The monthly increase.
     * @return The list of balances.
     */
    private static List<Double> getMilestoneBalances(List<AgeData> ages, double balance, double interest, double monthlyIncrease) {
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
