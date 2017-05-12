package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/1/2017.
 */

public class TaxDeferredIncomeData extends IncomeTypeData {
    private final String name;
    private final String minimumAge;
    private final String interest;
    private final String monthAdd;
    private final String penalty;
    private final int is401k;

    public TaxDeferredIncomeData(String name, int type, String minimumAge, String interest, String monthlyAdd, String penalty, int is401k) {
        super(name, type);
        this.name = name;
        this.minimumAge = minimumAge;
        this.interest = interest;
        this.monthAdd = monthlyAdd;
        this.penalty = penalty;
        this.is401k = is401k;
    }

    public String getMinimumAge() {
        return minimumAge;
    }

    public String getInterest() {
        return interest;
    }

    public String getMonthAddition() {
        return monthAdd;
    }

    public String getPenalty() {
        return penalty;
    }

    public int getIs401k() {
        return is401k;
    }
}
