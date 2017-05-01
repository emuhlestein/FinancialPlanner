package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/1/2017.
 */

public class TaxDeferredData {
    private final String penaltyAmount;
    private final String penaltyAge;
    private final int is401k;

    public String getPenaltyAmount() {
        return penaltyAmount;
    }

    public String getPenaltyAge() {
        return penaltyAge;
    }

    public int getIs401k() {
        return is401k;
    }

    public TaxDeferredData(String penaltyAmount, String penaltyAge, int is401k) {

        this.penaltyAmount = penaltyAmount;
        this.penaltyAge = penaltyAge;
        this.is401k = is401k;
    }
}
