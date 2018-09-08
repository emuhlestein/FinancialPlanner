package com.intelliviz.income.data;

import com.intelliviz.data.IncomeSourceTypeViewData;
import com.intelliviz.data.SavingsData;

public class SavingsViewData implements IncomeSourceTypeViewData {
    private SavingsData mSD;
    private boolean mSpouseIncluded;
    private int mStatus;
    private String mMessage;

    public SavingsViewData(SavingsData sd, boolean spouseIncluded, int status, String message) {
        mSD = sd;
        mSpouseIncluded = spouseIncluded;
        mStatus = status;
        mMessage = message;
    }

    public SavingsData getSavingsData() {
        return mSD;
    }

    @Override
    public boolean isSpouseIncluded() {
        return mSpouseIncluded;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
