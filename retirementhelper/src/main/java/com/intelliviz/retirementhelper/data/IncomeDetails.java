package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 11/16/2017.
 */

public class IncomeDetails {
    private String mLine1;
    private String mLine2;

    public IncomeDetails(String line1, String line2) {
        mLine1 = line1;
        mLine2 = line2;
    }

    public String getLine1() {
        return mLine1;
    }

    public String getLine2() {
        return mLine2;
    }
}
