package com.intelliviz.income.data;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.IncomeSourceTypeViewData;

public class GovPensionViewData implements IncomeSourceTypeViewData {
    private GovPension mGP;
    private boolean mSpouseIncluded;
    private int mStatus;
    private String mMessage;

    public GovPensionViewData(GovPension gp, boolean spouseIncluded, int status, String message) {
        mGP = gp;
        mSpouseIncluded = spouseIncluded;
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

    @Override
    public boolean isSpouseIncluded() {
        return mSpouseIncluded;
    }
}
