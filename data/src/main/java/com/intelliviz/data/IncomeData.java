package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

/**
 * Created by edm on 11/3/2017.
 */

public class IncomeData {
    private AgeData mAge;
    private double mMonthlyAmount;
    private double mPenaltyAmount;
    private double mBalance;
    private int mBalanceState;
    private boolean mPenalty;

    public IncomeData(AgeData age, double monthlyAmount, double balance, int balanceState) {
        this(age, monthlyAmount, 0, balance, balanceState, false);
    }

    public IncomeData(AgeData age, double monthlyAmount, double balance, int balanceState, boolean penalty) {
        this(age, monthlyAmount, 0, balance, balanceState, penalty);
    }

    public IncomeData(AgeData age, double monthlyAmount, double penaltyAmount, double balance, int balanceState, boolean penalty) {
        mAge = age;
        mMonthlyAmount = monthlyAmount;
        mPenaltyAmount = penaltyAmount;
        mBalance = balance;
        mBalanceState = balanceState;
        mPenalty = penalty;
    }

    public AgeData getAge() {
        return mAge;
    }

    public double getMonthlyAmount() {
        return mMonthlyAmount - mPenaltyAmount;
    }

    public void setMonthlyAmount(double monthlyAmount) {
        mMonthlyAmount = monthlyAmount;
    }

    public double getMonthlyAmountNoPenalty() {
        return mMonthlyAmount;
    }

    public double getBalance() {
        return mBalance;
    }

    public int getBalanceState() {
        return mBalanceState;
    }

    public boolean isPenalty() {
        return mPenalty;
    }
}
