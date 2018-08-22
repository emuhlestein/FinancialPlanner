package com.intelliviz.income.data;

import com.intelliviz.data.SavingsData;

public class SavingsViewData {
    private SavingsData mSD;
    private int mStatus;
    private String mMessage;

    public SavingsViewData(SavingsData sd, int status, String message) {
        mSD = sd;
        mStatus = status;
        mMessage = message;
    }

    public SavingsData getSavingsData() {
        return mSD;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
