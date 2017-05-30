package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/29/2017.
 */

public class MilestoneData {
    private AgeData mAge;
    private String mAmount;
    private String mBalance;

    public MilestoneData(AgeData age, String amount, String balance) {
        mAge = age;
        mAmount = amount;
        mBalance = balance;
    }

    public AgeData getAge() {
        return mAge;
    }

    public String getAmount() {
        return mAmount;
    }

    public String getBalance() {
        return mBalance;
    }
}
