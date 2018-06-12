package com.intelliviz.retirementhelper.data;

import com.intelliviz.income.data.AgeData;

/**
 * Created by edm on 11/21/2017.
 */

public class TaxDeferredData {
    private AgeData mAge;
    private int mNumMonths;
    private double mStartBalance;
    private double mEndBalance;
    private double mWithdrawAmount;
    private double mFinalWithdrawAmount;
    private int mStatus;


    public TaxDeferredData(AgeData age, int numMonths, double startBalance, double endBalance,
                           double withdrawAmount, double finalWithdrawAmount, int status) {
        mAge = age;
        mNumMonths = numMonths;
        mStartBalance = startBalance;
        mEndBalance = endBalance;
        mWithdrawAmount = withdrawAmount;
        mFinalWithdrawAmount = finalWithdrawAmount;
        mStatus = status;
    }

    public AgeData getAge() {
        return mAge;
    }

    public double getStartBalance() {
        return mStartBalance;
    }

    public double getWithdrawAmount() {
        return mWithdrawAmount;
    }

    public int getNumMonths() {
        return mNumMonths;
    }

    public double getEndBalance() {
        return mEndBalance;
    }

    public double getFinalWithdrawAmount() {
        return mFinalWithdrawAmount;
    }

    public int getStatus() {
        return mStatus;
    }
}
