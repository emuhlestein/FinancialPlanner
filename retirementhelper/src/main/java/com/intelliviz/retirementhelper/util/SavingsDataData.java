package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/1/2017.
 */

public class SavingsDataData {
    private final String interest;
    private final String monthlyIncrease;

    public SavingsDataData(String interest, String monthlyIncrease) {
        this.interest = interest;
        this.monthlyIncrease = monthlyIncrease;
    }

    public String getInterest() {
        return interest;
    }

    public String getMonthlyIncrease() {
        return monthlyIncrease;
    }
}
