package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

/**
 * Created by edm on 11/3/2017.
 */

public class IncomeData {
    private AgeData mAge;
    private double mMonthlyAmount;
    private double mBalance;
    private int mStatus;
    private String mMessage;

    public IncomeData() {
        mAge = new AgeData(0);
        mMonthlyAmount = 0;
        mBalance = 0;
        mStatus = RetirementConstants.SC_GOOD;
        mMessage = null;
    }

    public IncomeData(AgeData age, double monthlyAmount, double balance, int status, String message) {
        mAge = age;
        mMonthlyAmount = monthlyAmount;
        mBalance = balance;
        mStatus = status;

        // empty strings are not allowed
        if(message != null && message.isEmpty()) {
            mMessage = null;
        } else {
            mMessage = message;
        }
    }

    public AgeData getAge() {
        return mAge;
    }

    public double getMonthlyAmount() {
        return mMonthlyAmount;
    }

    public double getBalance() {
        return mBalance;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
