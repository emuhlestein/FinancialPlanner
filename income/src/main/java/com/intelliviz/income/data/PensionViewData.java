package com.intelliviz.income.data;

import com.intelliviz.data.IncomeSourceTypeViewData;
import com.intelliviz.data.PensionData;

public class PensionViewData implements IncomeSourceTypeViewData {
    private PensionData mPD;
    private boolean mSpouseIncluded;
    private int mStatus;
    private String mMessage;

    public PensionViewData(PensionData PD, boolean spouseIncluded, int status, String message) {
        mPD = PD;
        mSpouseIncluded = spouseIncluded;
        mStatus = status;
        mMessage = message;
    }

    @Override
    public boolean isSpouseIncluded() {
        return mSpouseIncluded;
    }

    public PensionData getPensionData() {
        return mPD;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
