package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.SystemUtils;

import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_AMOUNT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;

/**
 * Created by edm on 10/18/2017.
 */

public class TaxDeferredIncomeRules implements IncomeTypeRules {
    private AgeData mMinAge;
    private AgeData mCurrentAge;
    private AgeData mEndAge;
    private double mBalance;
    private double mInterest;
    private double mMonthlyIncrease;
    private double mPenalty;
    private int mWithdrawMode;
    private double mWithdrawAmount;

    public TaxDeferredIncomeRules(String birthDate, AgeData endAge, double balance,
                                  double interest, double monthlyIncrease, int withdrawMode, double withdrawAmount) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mMinAge = new AgeData(59, 6);
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
        mPenalty = 0.10;
        mBalance = balance;
        mEndAge = endAge;
        mWithdrawAmount = withdrawAmount;
        mWithdrawMode = withdrawMode;
    }

    @Override
    public double getMonthlyBenefitForAge(AgeData age) {
        if(age.isBefore(mCurrentAge)) {
            return 0;
        }
        AgeData diffAge = age.subtract(mCurrentAge);
        int numMonths = diffAge.getNumberOfMonths();
        double futureBalance = getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        double monthlyAmount = getMonthlyAmount(mBalance, mWithdrawMode, mWithdrawAmount);
        MilestoneData milestoneData = getMilestoneData(age, futureBalance, monthlyAmount);

        if(age.isBefore(mMinAge)) {

        }

        return milestoneData.getMonthlyBenefit();
    }

    @Override
    public AgeData getFullRetirementAge() {
        return mMinAge;
    }

    @Override
    public AgeData getMinimumAge() {
        return mMinAge;
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

        return new MilestoneData(startAge, mEndAge, mMinAge, monthlyAmount, startBalance, lastBalance, mPenalty, numMonths);
    }
}
