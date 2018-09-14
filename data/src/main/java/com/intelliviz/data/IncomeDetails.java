package com.intelliviz.data;

/**
 * Created by edm on 11/16/2017.
 */

public class IncomeDetails {
    private String mLine1;
    private String mLine2;
    private String mLine3;
    private String mMessage;
    private int mNumLines;
    private int mBenefitInfo;
    private boolean mClickAccepted;

    public IncomeDetails(String line1, int benefitInfo, String message) {
       this(line1, "", "", benefitInfo, message);
       mNumLines = 1;
    }

    public IncomeDetails(String line1, String line2, String line3, int benefitInfo, String message) {
        mLine1 = line1;
        mLine2 = line2;
        mLine3 = line3;
        mBenefitInfo = benefitInfo;
        mNumLines = 3;
        mMessage = message;
        mClickAccepted = false;
    }

    public String getLine1() {
        return mLine1;
    }

    public String getLine2() {
        return mLine2;
    }

    public String getLine3() {
        return mLine3;
    }

    public int getNumLines() {
        return mNumLines;
    }

    public int getBenefitInfo() {
        return mBenefitInfo;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isClickAccepted() {
        return mClickAccepted;
    }

    public void setClickAccepted(boolean acceptClick) {
        mClickAccepted = acceptClick;
    }

    public boolean hasDetails() {
        return (mMessage != null && !mMessage.isEmpty());
    }
}
