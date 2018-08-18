package com.intelliviz.income.data;

import com.intelliviz.data.PensionData;

public class PensionViewData {
    private PensionData mPD;
    private int mStatus;
    private String mMessage;

    public PensionViewData(PensionData PD, int status, String message) {
        mPD = PD;
        mStatus = status;
        mMessage = message;
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
