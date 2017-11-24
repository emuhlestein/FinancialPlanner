package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.BalanceUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public MilestoneData getMilestone(AgeData age) {
        if(age.isBefore(mCurrentAge)) {
            return null;
        }
        int numMonths =  age.diff(mCurrentAge);
        double futureBalance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        double monthlyAmount = BalanceUtils.getMonthlyAmount(futureBalance, mWithdrawMode, mWithdrawAmount);
        MilestoneData milestoneData =
                BalanceUtils.getMilestoneData(age, mEndAge, mInterest, futureBalance, monthlyAmount, mWithdrawMode, mWithdrawAmount);

        if(age.isBefore(mMinAge)) {

        }

        return milestoneData;
    }

    @Override
    public List<AgeData> getAges() {
        return new ArrayList<>(Arrays.asList(mMinAge));
    }

    public TaxDeferredData getMonthlyBenefitForAge(AgeData startAge) {
        if(startAge.isBefore(mCurrentAge)) {
            return null;
        }

        int numMonths =  startAge.diff(mCurrentAge);
        double futureBalance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        return getMilestoneData(startAge, mEndAge, mInterest, futureBalance, mWithdrawMode, mWithdrawAmount);
    }

    private TaxDeferredData getMilestoneData(AgeData startAge, AgeData endAge, double interest,
                                                 double startBalance, int withdrawMode, double withdrawAmount) {
        if(!startAge.isBefore(endAge)) {
            return null;
        }
        int numMonthsInRetirement = endAge.diff(startAge);
        double lastBalance = startBalance;
        double monthlyInterest = interest / 1200;

        double initialAmount;
        if(withdrawMode == RetirementConstants.WITHDRAW_MODE_PERCENT) {
            double percent = withdrawAmount / 1200;
            initialAmount = startBalance * percent;
        } else {
            initialAmount = withdrawAmount;
        }

        double monthlyAmount = initialAmount;
        double monthlyAmountIncrease = 0;

        int numMonths = 0;
        AgeData countAge = new AgeData();
        for(int mon = 0; mon < numMonthsInRetirement; mon++) {
            if(lastBalance <= 0) {
                break;
            }

            lastBalance = lastBalance - monthlyAmount;
            double monthlyIncrease = lastBalance * monthlyInterest;
            lastBalance = lastBalance + monthlyIncrease;

            countAge.add(1);
            if(countAge.getMonth() == 0) {
                // new year, increase withdraw amount
                double amountToIncrease = monthlyAmount * monthlyAmountIncrease;
                monthlyAmount += amountToIncrease;
            }

            numMonths++;
        }

        if(lastBalance < 0) {
            lastBalance = 0;
            numMonths--;
        }

        double annutalAmount = monthlyAmount * 12;
        int status;
        if(lastBalance == 0) {
            status = 0;
        } else if(lastBalance < annutalAmount) {
            status = 1;
        } else {
            status = 2;
        }

        // Need the following:
        // Start Balance, End Balance, num months, init withdraw amount, final withdraw amount, did money run out
        return new TaxDeferredData(startAge, numMonths, startBalance, lastBalance, initialAmount, monthlyAmount, status);
    }
}
