package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 11/3/2017.
 */

public class AmountData {
    private AgeData mAge;
    private double mMonthlyAmount;
    private double mBalance;

    public AmountData(AgeData age, double monthlyAmount, double balance) {
        mAge = age;
        mMonthlyAmount = monthlyAmount;
        mBalance = balance;
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
}
