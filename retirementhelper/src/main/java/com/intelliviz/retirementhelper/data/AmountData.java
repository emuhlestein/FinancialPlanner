package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 11/3/2017.
 */

public class AmountData {
    private AgeData mAge;
    private double mMonthlyAmount;
    private double mBalance;
    private int mBalanceState;
    private boolean mPenalty;

    public AmountData(AgeData age, double monthlyAmount, double balance, int balanceState, boolean penalty) {
        mAge = age;
        mMonthlyAmount = monthlyAmount;
        mBalance = balance;
        mBalanceState = balanceState;
        mPenalty = penalty;
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

    public int getBalanceState() {
        return mBalanceState;
    }

    public boolean isPenalty() {
        return mPenalty;
    }
}
