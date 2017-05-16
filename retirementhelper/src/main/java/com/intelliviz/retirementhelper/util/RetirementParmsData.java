package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/16/2017.
 */

public class RetirementParmsData {
    private final String startAge;
    private final String endAge;
    private final int withdrawMode;
    private final String withdrawPercent;
    private final int includeInflation;
    private final String inflationAmount;

    public RetirementParmsData(String startAge, String endAge, int withdrawMode, String withdrawPercent, int includeInflation, String inflationAmount) {
        this.startAge = startAge;
        this.endAge = endAge;
        this.withdrawMode = withdrawMode;
        this.withdrawPercent = withdrawPercent;
        this.includeInflation = includeInflation;
        this.inflationAmount = inflationAmount;
    }

    public String getStartAge() {
        return startAge;
    }

    public String getEndAge() {
        return endAge;
    }

    public int getWithdrawMode() {
        return withdrawMode;
    }

    public String getWithdrawPercent() {
        return withdrawPercent;
    }

    public int getIncludeInflation() {
        return includeInflation;
    }

    public String getInflationAmount() {
        return inflationAmount;
    }
}
