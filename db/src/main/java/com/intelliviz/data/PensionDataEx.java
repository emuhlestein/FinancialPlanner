package com.intelliviz.data;

import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;

public class PensionDataEx {
    private PensionIncomeEntity mPIE;
    private RetirementOptionsEntity mROE;
    private int mNumRecords;

    public PensionDataEx(PensionIncomeEntity pie, int numRecords, RetirementOptionsEntity ROE) {
        mPIE = pie;
        mNumRecords = numRecords;
        mROE = ROE;
    }

    public PensionIncomeEntity getPie() {
        return mPIE;
    }

    public int getNumRecords() {
        return mNumRecords;
    }

    public RetirementOptionsEntity getROE() {
        return mROE;
    }
}
