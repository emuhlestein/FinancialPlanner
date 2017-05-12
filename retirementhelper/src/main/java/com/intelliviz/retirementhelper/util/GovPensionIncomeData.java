package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/11/2017.
 */

public class GovPensionIncomeData extends IncomeTypeData {
    private final String name;
    private final String startAge;
    private final String monthlyBenefit;

    public GovPensionIncomeData(String name, int type, String startAge, String monthlyBenefit) {
        super(name, type);

        this.name = name;
        this.startAge = startAge;
        this.monthlyBenefit = monthlyBenefit;
    }

    public String getName() {
        return name;
    }

    public String getStartAge() {
        return startAge;
    }

    public String getMonthlyBenefit() {
        return monthlyBenefit;
    }

}
