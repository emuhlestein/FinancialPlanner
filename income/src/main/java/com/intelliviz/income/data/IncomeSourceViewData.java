package com.intelliviz.income.data;

import com.intelliviz.db.entity.AbstractIncomeSource;

import java.util.List;

public class IncomeSourceViewData {
    private List<AbstractIncomeSource> mIncomeSources;
    private int mStatus;
    private String mMessage;

    public IncomeSourceViewData(List<AbstractIncomeSource> incomeSources, int status, String message) {
        mIncomeSources = incomeSources;
        mStatus = status;
        mMessage = message;
    }

    public List<AbstractIncomeSource> getData() {
        return mIncomeSources;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
