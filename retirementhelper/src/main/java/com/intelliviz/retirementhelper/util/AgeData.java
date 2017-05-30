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

    public AgeData(String age) {
        float fage = Float.parseFloat(age);
        mYear = (int)fage;
        float fmonths = fage - mYear;
        mMonth = (int)(fmonths * 12);
    }

    public AgeData(int year, int month) {
        mYear = year;
        mMonth = month;
    }

    /**
     * Does ageDate come on or before this date.
     * @param age
     * @return
     */
    public boolean onOrBefore(AgeData age) {
        if(mYear < age.getYear()) {
            return true;
        } else if(mYear == age.getYear()) {
            if(mMonth <= age.getMonth() ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public AgeData subtract(AgeData ageData) {
        int year = mYear - ageData.getYear();
        int month = mMonth - ageData.getMonth();
        if(month < 0) {
            year--;
            month = 12 + month;
        }

        if(year < 0) {
            return new AgeData();
        } else {
            return new AgeData(year, month);
        }
    }

    public float getAge() {
        return (float)(mYear + mMonth / 12.0);
    }

    public int getNumberOfMonths() {
        return mYear * 12 + mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }
}
