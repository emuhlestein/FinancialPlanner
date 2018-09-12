package com.intelliviz.income.data;

import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.IncomeSourceTypeViewData;
import com.intelliviz.data.SavingsData;

import java.util.List;

public class SavingsViewData implements IncomeSourceTypeViewData {
    private SavingsData mSD;
    private List<IncomeDetails> mIncomeDetail;
    private boolean mSpouseIncluded;
    private int mStatus;
    private String mMessage;

    public SavingsViewData(SavingsData sd, final List<IncomeDetails> incomeDetail, boolean spouseIncluded, int status, String message) {
        mSD = sd;
        mIncomeDetail = incomeDetail;
        mSpouseIncluded = spouseIncluded;
        mStatus = status;
        mMessage = message;
    }

    public SavingsData getSavingsData() {
        return mSD;
    }

    public List<IncomeDetails> getIncomeDetail() {
        return mIncomeDetail;
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
