package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.Collections;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_AMOUNT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;

/**
 * Created by edm on 10/19/2017.
 */

public class SavingsIncomeRules implements IncomeTypeRules {
    private AgeData mEndAge;
    private AgeData mCurrentAge;
    private double mBalance;
    private double mInterest;
    private double mMonthlyIncrease;
    private int mWithdrawMode;
    private double mWithdrawAmount;


    public SavingsIncomeRules(String birthDate, AgeData endAge,
                              double balance, double interest, double monthlyIncrease,  int withdrawMode, double withdrawAmount) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mEndAge = endAge;
        mBalance = balance;
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
        mWithdrawAmount = withdrawAmount;
        mWithdrawMode = withdrawMode;
    }

    @Override
    public double getMonthlyBenefitForAge(AgeData age) {
        AgeData diffAge = age.subtract(mCurrentAge);
        int numMonths = diffAge.getNumberOfMonths();
        double futureBalance = getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        double monthlyAmount = getMonthlyAmount(mBalance, mWithdrawMode, mWithdrawAmount);
        MilestoneData milestoneData = getMilestoneData(age, futureBalance, monthlyAmount);

        return milestoneData.getMonthlyBenefit();
    }

    @Override
    public List<AgeData> getAges() {
        return Collections.emptyList();
    }

    @Override
    public MilestoneData getMilestone(AgeData age) {
        double monthlyBenefit = getMonthlyBenefitForAge(age);
        return new MilestoneData(age, null, null, monthlyBenefit, 0, 0, 0, 0);
    }

    private double getFutureBalance(double balance, int numMonths, double interest, double monthlyIncrease) {
        double cumulativeBalance = balance;
        for(int i = 0; i < numMonths; i++) {
            cumulativeBalance = getNewBalance(cumulativeBalance, interest, monthlyIncrease);
        }
        return cumulativeBalance;
    }

    private double getNewBalance(double balance, double interest, double monthlyIncrease) {
        double interestEarned = getMonthlyAmountFromBalance(balance, interest);
        return monthlyIncrease + interestEarned + balance;
    }

    private double getMonthlyAmountFromBalance(double balance, double interest) {
        double monthlyInterest = interest / 1200.0;
        return balance * monthlyInterest;
    }

    private double getMonthlyAmount(double balance, int withdrawMode, double withdrawAmount) {
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

    private MilestoneData getMilestoneData(AgeData startAge, double startBalance, double monthlyAmount) {
        AgeData age = mEndAge.subtract(startAge);
        int numMonthsInRetirement = age.getNumberOfMonths();
        double lastBalance = startBalance;
        double monthlyInterest = mInterest / 1200;

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

        return new MilestoneData(startAge, mEndAge, null, monthlyAmount, startBalance, lastBalance, 0, numMonths);
    }
}
