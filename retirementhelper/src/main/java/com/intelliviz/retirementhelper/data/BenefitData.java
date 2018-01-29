package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 11/3/2017.
 */

public class BenefitData {
    private AgeData mAge;
    private double mMonthlyAmount;
    private double mPenaltyAmount;
    private double mBalance;
    private int mBalanceState;
    private boolean mPenalty;

    public BenefitData(AgeData age, double monthlyAmount, double balance, int balanceState, boolean penalty) {
        this(age, monthlyAmount, 0, balance, balanceState, penalty);
    }

    public BenefitData(AgeData age, double monthlyAmount, double penaltyAmount, double balance, int balanceState, boolean penalty) {
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
