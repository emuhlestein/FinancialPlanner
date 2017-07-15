package com.intelliviz.retirementhelper.util;

import android.content.Context;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.GovPensionIncomeData;
import com.intelliviz.retirementhelper.data.IncomeType;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PensionIncomeData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intelliviz.retirementhelper.util.DataBaseUtils.getMilestoneAges;
import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_AMOUNT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;
import static java.lang.Double.parseDouble;

/**
 * Utility class.
 * Created by edm on 5/23/2017.
 */

public class BenefitHelper {

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
        List<MilestoneData> saveMilestones = null;
        for(IncomeType incomeType : incomeTypes) {
            List<MilestoneData> milestones = getMilestones(context, incomeType, rod);
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
            }
        }

        AgeData endAge = saveMilestones.get(0).getEndAge();
        AgeData minimumAge = saveMilestones.get(0).getMinimumAge();
        for(int i = 0; i < msad.size(); i++) {
            AgeData startAge = msad.get(i).getAge();
            MilestoneData milestoneData = new MilestoneData(startAge, endAge, minimumAge, sumMonthlyAmount[i], sumStartBalance[i], sumEndBalance[i], 0, 0);
            sumMilestones.add(milestoneData);
        }

        return sumMilestones;
    }

    public static List<MilestoneData> getMilestones(Context context,
                                                    IncomeType incomeType,
                                                    RetirementOptionsData rod) {
        if(incomeType instanceof SavingsIncomeData) {
            return getMilestonesFromSavingsIncome(context, (SavingsIncomeData)incomeType, rod);
        } else if(incomeType instanceof TaxDeferredIncomeData) {
            return getMilestonesFromTaxDeferredIncome(context, (TaxDeferredIncomeData)incomeType, rod);
        } else if(incomeType instanceof PensionIncomeData){
            return getMilestonesFromPensionIncome(context, (PensionIncomeData)incomeType, rod);
        } else if(incomeType instanceof GovPensionIncomeData) {
            return getMilestonesFromGovPensionIncome(context, (GovPensionIncomeData)incomeType, rod);
        } else {
            return Collections.emptyList();
        }
    }

    private static List<MilestoneData> getMilestonesFromSavingsIncome(Context context, SavingsIncomeData sid,
                                                                      RetirementOptionsData rod) {
        double startBalance = sid.getBalance();
        double interestRate = sid.getInterest();
        double monthlyAddition = sid.getMonthlyIncrease();
        String endAge = rod.getEndAge();
        double withdrawAmount = parseDouble(rod.getWithdrawAmount());
        List<MilestoneData> milestones = new ArrayList<>();
        List<MilestoneAgeData> msad = getMilestoneAges(context, rod);
        if(msad.isEmpty()) {
            return milestones;
        }

        AgeData endOfLifeAge = SystemUtils.parseAgeString(endAge);

        List<Double> milestoneBalances = getMilestoneBalances(msad, startBalance, interestRate, monthlyAddition);

        milestones = getMilestones(endOfLifeAge, null, interestRate, 0, rod.getWithdrawMode(), withdrawAmount, msad, milestoneBalances);
        return milestones;
    }

    private static List<MilestoneData> getMilestonesFromTaxDeferredIncome(Context context, TaxDeferredIncomeData tdid,
                                                                          RetirementOptionsData rod) {
        double startBalance = tdid.getBalance();
        double interestRate = tdid.getInterestRate();
        double monthlyAddition = tdid.getMonthAddition();
        double penalty = tdid.getPenalty();
        String minAge = tdid.getMinimumAge();
        String endAge = rod.getEndAge();
        double withdrawAmount = parseDouble(rod.getWithdrawAmount());
        List<MilestoneData> milestones = new ArrayList<>();
        List<MilestoneAgeData> ages = getMilestoneAges(context, rod);
        if(ages.isEmpty()) {
            return milestones;
        }
        AgeData minimumAge = SystemUtils.parseAgeString(minAge);
        AgeData endOfLifeAge = SystemUtils.parseAgeString(endAge);

        List<Double> milestoneBalances = getMilestoneBalances(ages, startBalance, interestRate, monthlyAddition);

        milestones = getMilestones(endOfLifeAge, minimumAge, interestRate, penalty, rod.getWithdrawMode(), withdrawAmount, ages, milestoneBalances);
        return milestones;
    }

    private static List<MilestoneData> getMilestonesFromPensionIncome(Context context, PensionIncomeData pid,
                                                                      RetirementOptionsData rod) {
        List<MilestoneData> milestones = new ArrayList<>();
        List<MilestoneAgeData> ages = getMilestoneAges(context, rod);
        if(ages.isEmpty()) {
            return milestones;
        }

        AgeData minimumAge = SystemUtils.parseAgeString(pid.getStartAge());
        AgeData endAge = SystemUtils.parseAgeString(rod.getEndAge());
        double monthlyBenefit = pid.getMonthlyBenefit(0);

        MilestoneData milestone;
        for(MilestoneAgeData msad : ages) {
            AgeData age = msad.getAge();
            if(age.isBefore(minimumAge)) {
                milestone = new MilestoneData(age, endAge, minimumAge, 0, 0, 0, 0, 0);
            } else {
                AgeData diffAge = endAge.subtract(age);
                int numMonths = diffAge.getNumberOfMonths();

                milestone = new MilestoneData(age, endAge, minimumAge, monthlyBenefit, 0, 0, 0, numMonths);
            }
            milestones.add(milestone);
        }
        return milestones;
    }

    private static List<MilestoneData> getMilestonesFromGovPensionIncome(Context context,
                                                                         GovPensionIncomeData gpid,
                                                                         RetirementOptionsData rod) {
        List<MilestoneData> milestones = new ArrayList<>();
        List<MilestoneAgeData> ages = getMilestoneAges(context, rod);
        if(ages.isEmpty()) {
            return milestones;
        }

        String birthDate = rod.getBirthdate();
        int birthYear = SystemUtils.getBirthYear(birthDate);
        double monthlyBenefit = gpid.getMonthlyBenefit();

        AgeData minimumAge = new AgeData(62, 0);

        MilestoneData milestone;
        for(MilestoneAgeData msad : ages) {
            AgeData age = msad.getAge();
            if(age.isBefore(minimumAge)) {
                milestone = new MilestoneData(age, null, minimumAge, 0, 0, 0, 0, 0);
            } else {
                double factor = GovPensionHelper.getSocialSecuretyAdjustment(birthDate, age);

                double factorAmount = (monthlyBenefit * factor) / 100.0;
                double adjustedBenefit = monthlyBenefit - factorAmount;
                milestone = new MilestoneData(age, null, minimumAge, adjustedBenefit, 0, 0, 0, 0);
            }
            milestones.add(milestone);
        }
        return milestones;
    }

    private static List<MilestoneData> getMilestones(AgeData endOfLifeAge, AgeData minimumAge,
                                                     double interestRate, double penalty, int withdrawMode, double withdrawAmount,
                                                     List<MilestoneAgeData> ages, List<Double> milestoneBalances) {

        List<MilestoneData> milestones = new ArrayList<>();

        for(int i = 0; i < ages.size(); i++) {
            AgeData startAge = ages.get(i).getAge();
            if(minimumAge == null) {
                penalty = 0;
            } else {
                if (!startAge.isBefore(minimumAge)) {
                    penalty = 0;
                }
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
        double lastBalance = startBalance;
        double monthlyInterest = interestRate / 1200;

        int numMonths = 0;
        for(int mon = 0; mon < numMonthsInRetirement; mon++) {
            if(lastBalance <= 0) {
                break;
            }

            lastBalance = lastBalance - monthlyAmount;
            double monthlyIncrease = lastBalance * monthlyInterest;
            lastBalance = lastBalance + monthlyIncrease;
            numMonths++;
        }

        if(lastBalance < 0) {
            lastBalance = 0;
            numMonths--;
        }

        return new MilestoneData(startAge, endAge, minimumAge, monthlyAmount, startBalance, lastBalance, penalty, numMonths);
    }

    /**
     * Get the balances for each milestone.
     * @param balance The beginning balance.
     * @param interest The annual interest rate.
     * @param monthlyIncrease The monthly increase.
     * @return The list of balances.
     */
    private static List<Double> getMilestoneBalances(List<MilestoneAgeData> ages, double balance, double interest, double monthlyIncrease) {
        List<Double> balances = new ArrayList<>();
        if(ages.isEmpty()) {
            return balances;
        }
        AgeData refAge = ages.get(0).getAge();
        double newBalance = balance;
        balances.add(newBalance);
        for (int i = 1; i < ages.size(); i++) {
            AgeData age = ages.get(i).getAge();
            AgeData diffAge = age.subtract(refAge);
            int numMonths = diffAge.getNumberOfMonths();
            newBalance = getFutureBalance(newBalance, numMonths, interest, monthlyIncrease);
            balances.add(newBalance);
            refAge = age;
        }

        return balances;
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
