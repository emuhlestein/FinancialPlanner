package com.intelliviz.income.data;

import com.intelliviz.data.GovPension;

public class GovPensionViewData {
    private GovPension mGP;
    private int mStatus;
    private String mMessage;

    public GovPensionViewData(GovPension GP, int status, String message) {
        mGP = GP;
        mStatus = status;
        mMessage = message;
    }

    public GovPension getGovPension() {
        return mGP;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
