package com.intelliviz.retirementhelper.util;

import android.content.Context;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.IncomeType;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PersonalInfoData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_AMOUNT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;
import static java.lang.Double.parseDouble;

/**
 * Created by edm on 5/23/2017.
 */

public class BenefitHelper {

    public static List<MilestoneData> getAllMilestones(Context context, RetirementOptionsData rod) {
        List<MilestoneData> milestones = new ArrayList<>();
        List<IncomeType> incomeTypes = DataBaseUtils.getAllIncomeTypes(context);
        if(incomeTypes == null) {
            return milestones;
        }

        for(IncomeType incomeType : incomeTypes) {
            milestones = getMilestones(context, incomeType, rod);
            if(milestones == null || milestones.isEmpty()) {
                continue;
            }
            double sumMonthlyAmount = 0;
            double sumBalance = 0;

            AgeData startAge = milestones.get(0).getStartAge();
            AgeData endAge = milestones.get(0).getEndAge();
            AgeData minimumAge = milestones.get(0).getMinimumAge();
            double penalty = milestones.get(0).getPenaltyAmount();
            List<Double> balances = milestones.get(0).getMonthlyBalances();
            double monthlyAmount;
            double balance;
            for(MilestoneData milestoneData : milestones) {
                monthlyAmount = milestoneData.getMonthlyAmount();
                sumMonthlyAmount += monthlyAmount;
                balance = milestoneData.getStartBalance();
                sumBalance += balance;
            }
            MilestoneData milestoneData = new MilestoneData(startAge, endAge, minimumAge, sumMonthlyAmount, sumBalance, penalty, balances);
            milestones.add(milestoneData);
        }

        return milestones;
    }

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
        return getMilestones(context, sid.getBalance(),
                parseDouble(sid.getInterest()),
                parseDouble(sid.getMonthlyIncrease()));
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
                newBalance = getFutureBalance(balance, 0, interest, monthlyIncrease);
                monthlyAmount = getMonthlyAmountFromBalance(newBalance, interest);
                //milestones.add(new MilestoneData(ages.get(0), Double.toString(monthlyAmount), Double.toString(newBalance)));
                refAge = ages.get(0);
            } else {
                AgeData age = ages.get(i);
                AgeData diffAge = age.subtract(refAge);
                int numMonths = diffAge.getNumberOfMonths();
                newBalance = getFutureBalance(newBalance, numMonths, interest, monthlyIncrease);
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

    private static List<MilestoneData> getMilestonesFromTaxDeferredIncome(Context context, TaxDeferredIncomeData tdid, RetirementOptionsData rod) {
        double startBalance = tdid.getBalance();
        double interestRate = tdid.getInterestRate();
        double monthlyAddition = tdid.getMonthAddition();
        double penalty = tdid.getPenalty();
        String minAge = tdid.getMinimumAge();
        String endAge = rod.getEndAge();
        double withdrawAmount = parseDouble(rod.getWithdrawAmount());
        List<MilestoneData> milestones = new ArrayList<>();
        List<AgeData> ages = getMilestoneAges(context);
        if(ages.isEmpty()) {
            return milestones;
        }
        AgeData minimumAge = new AgeData(minAge);
        AgeData endOfLifeAge = new AgeData(endAge);

        List<Double> milestoneBalances = getMilestoneBalances(ages, startBalance, interestRate, monthlyAddition);

        milestones = getMilestones(endOfLifeAge, minimumAge, interestRate, penalty, rod.getWithdrawMode(), withdrawAmount, ages, milestoneBalances);
        return milestones;
    }

    private static List<MilestoneData> getMilestones(AgeData endOfLifeAge, AgeData minimumAge,
                                                     double interestRate, double penalty, int withdrawMode, double withdrawAmount,
                                                     List<AgeData> ages, List<Double> milestoneBalances) {

        List<MilestoneData> milestones = new ArrayList<>();

        for(int i = 0; i < ages.size(); i++) {
            AgeData startAge = ages.get(i);
            if(!startAge.isBefore(minimumAge)) {
                penalty = 0;
            }
            double startBalance = milestoneBalances.get(i);
            double monthlyAmount = getMonthlyAmount(startBalance, withdrawMode, withdrawAmount);
            MilestoneData milestoneData = getMonthlyBalances(startAge, endOfLifeAge, minimumAge,
                    startBalance, interestRate, monthlyAmount, penalty);
            milestones.add(milestoneData);
        }
        return milestones;
    }

    private static MilestoneData getMonthlyBalances(AgeData startAge, AgeData endAge, AgeData minimumAge,
                                                    double startBalance, double interestRate, double monthlyAmount,
                                                    double penalty) {
        AgeData age = endAge.subtract(startAge);
        int numMonthsInRetirement = age.getNumberOfMonths();
        double newBalance = startBalance;
        double monthlyInterest = interestRate / 1200;

        List<Double> balances = new ArrayList<>();
        for(int mon = 0; mon < numMonthsInRetirement; mon++) {
            if(newBalance <= 0) {
                break;
            }

            newBalance = newBalance - monthlyAmount;
            double monthlyIncrease = newBalance * monthlyInterest;
            newBalance = newBalance + monthlyIncrease;
            balances.add(newBalance);
        }

        return new MilestoneData(startAge, endAge, minimumAge, monthlyAmount, startBalance, penalty, balances);
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
            newBalance = getFutureBalance(newBalance, numMonths, interest, monthlyIncrease);
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

    /**
     * Calculate the balance in the future after a certain number of months, at the specified interest rate
     * and with monthly increases.
     * @param balance The current balance.
     * @param numMonths The number of months over which to calculate the gains.
     * @param interest The annual interest rate.
     * @param monthlyIncrease THe monthly amount added to the balance.
     * @return The new balance.
     */
    private static double getFutureBalance(double balance, int numMonths, double interest, double monthlyIncrease) {
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

    private static double getMonthlyAmount(double balance, int withdrawMode, double withdrawAmount) {
        double monthlyAmount;
        switch(withdrawMode) {
            case WITHDRAW_MODE_AMOUNT:
                monthlyAmount = withdrawAmount;
                break;
            case WITHDRAW_MODE_PERCENT:
                monthlyAmount = getMonthlyAmountFromBalance(balance, withdrawAmount);
                break;
            default:
                monthlyAmount = withdrawAmount;
        }
        return monthlyAmount;
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
