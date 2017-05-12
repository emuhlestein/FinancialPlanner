package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/1/2017.
 */

public class SavingsIncomeData extends IncomeTypeData {
    private final String name;
    private final String interest;
    private final String monthlyIncrease;

    public SavingsIncomeData(String name, int type, String interest, String monthlyIncrease) {
        super(name, type);
        this.name = name;
        this.interest = interest;
        this.monthlyIncrease = monthlyIncrease;
    }

    public String getName() {
        return name;
    }

    public String getInterest() {
        return interest;
    }

    public String getMonthlyIncrease() {
        return monthlyIncrease;
    }
}
