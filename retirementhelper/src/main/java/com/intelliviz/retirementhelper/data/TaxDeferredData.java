package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.RetirementConstants;

/**
 * Created by edm on 11/21/2017.
 */

public class TaxDeferredData {
    private AgeData mAge;
    private double mBalance;
    private double mWithdrawAmount;
    private int mWithdrawMode;
    private double mAmount;

    public TaxDeferredData(AgeData age, double balance, double withdrawAmount, int withdrawMode) {
        mAge = age;
        mBalance = balance;
        mWithdrawAmount = withdrawAmount;
        mWithdrawMode = withdrawMode;

        if(mWithdrawMode == RetirementConstants.WITHDRAW_MODE_PERCENT) {
            mAmount = mBalance * (mWithdrawAmount / 1200);
        } else {
            mAmount = mBalance - mWithdrawAmount;
        }
    }

    public AgeData getAge() {
        return mAge;
    }

    public double getBalance() {
        return mBalance;
    }

    public double getWithdrawAmount() {
        return mWithdrawAmount;
    }

    public int getWithdrawMode() {
        return mWithdrawMode;
    }

    public double getAmount() {
        return mAmount;
    }
}
