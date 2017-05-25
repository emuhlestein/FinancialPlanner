package com.intelliviz.retirementhelper.util;

/**
 * Created by edm on 5/23/2017.
 */

public class AgeData {
    private final int mYear;
    private final int mMonth;

    public AgeData() {
        mYear = 0;
        mMonth = 0;
    }

    public AgeData(int year, int month) {
        mYear = year;
        mMonth = month;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }
}
