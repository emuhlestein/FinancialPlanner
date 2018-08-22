package com.intelliviz.data;

import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.SavingsIncomeEntity;

public class SavingsDataEx {
    private SavingsIncomeEntity mSIE;
    private RetirementOptionsEntity mROE;
    private int mNumRecords;
    public static int num = 0;

    public SavingsDataEx(SavingsIncomeEntity sie, int numRecords, RetirementOptionsEntity ROE) {
        mSIE = sie;
        mNumRecords = numRecords;
        mROE = ROE;
        SavingsDataEx.num++;
    }

    public SavingsIncomeEntity getSie() {
        return mSIE;
    }

    public int getNumRecords() {
        return mNumRecords;
    }

    public RetirementOptionsEntity getROE() {
        return mROE;
    }
}
