package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/29/2017.
 */

public class MilestoneData {
    private AgeData mAge;
    private String mAmount;

    public MilestoneData(AgeData age, String amount) {
        mAge = age;
        mAmount = amount;
    }

    public AgeData getAge() {
        return mAge;
    }

    public String getAmount() {
        return mAmount;
    }
}
