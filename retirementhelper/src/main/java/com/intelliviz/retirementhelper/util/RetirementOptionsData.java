package com.intelliviz.retirementhelper.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/16/2017.
 */

public class RetirementOptionsData implements Parcelable {
    private final String mStartAge;
    private final String mEndAge;
    private final int mWithdrawMode;
    private final String mWithdrawAmount;

    public RetirementOptionsData(String startAge, String endAge, int withdrawMode, String withdrawAmount) {
        mStartAge = startAge;
        mEndAge = endAge;
        mWithdrawMode = withdrawMode;
        mWithdrawAmount = withdrawAmount;
    }

    public String getStartAge() {
        return mStartAge;
    }

    public String getEndAge() {
        return mEndAge;
    }

    public int getWithdrawMode() {
        return mWithdrawMode;
    }

    public String getWithdrawAmount() {
        return mWithdrawAmount;
    }

    public RetirementOptionsData(Parcel in) {
        mStartAge = in.readString();
        mEndAge = in.readString();
        mWithdrawMode = in.readInt();
        mWithdrawAmount = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mStartAge);
        dest.writeString(mEndAge);
        dest.writeInt(mWithdrawMode);
        dest.writeString(mWithdrawAmount);
    }

    public static final Creator<RetirementOptionsData> CREATOR = new Creator<RetirementOptionsData>() {
        @Override
        public RetirementOptionsData createFromParcel(Parcel source) {
            return new RetirementOptionsData(source);
        }

        @Override
        public RetirementOptionsData[] newArray(int size) {
            return new RetirementOptionsData[size];
        }
    };
}
