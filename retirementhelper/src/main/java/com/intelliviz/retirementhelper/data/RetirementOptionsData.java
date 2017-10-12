package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * CLass for managing retirement options.
 * Created by Ed Muhlestein on 5/16/2017.
 */
public class RetirementOptionsData implements Parcelable {
    private String mEndAge;
    private int mWithdrawMode;
    private String mWithdrawAmount;
    private String mBirthdate;

    public RetirementOptionsData(String endAge, int withdrawMode, String withdrawAmount, String birthdate) {
        mEndAge = endAge;
        mWithdrawMode = withdrawMode;
        mWithdrawAmount = withdrawAmount;
        mBirthdate = birthdate;
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

    public String getBirthdate() {
        return mBirthdate;
    }





    protected RetirementOptionsData(Parcel in) {
        mEndAge = in.readString();
        mWithdrawMode = in.readInt();
        mWithdrawAmount = in.readString();
        mBirthdate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEndAge);
        dest.writeInt(mWithdrawMode);
        dest.writeString(mWithdrawAmount);
        dest.writeString(mBirthdate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RetirementOptionsData> CREATOR = new Parcelable.Creator<RetirementOptionsData>() {
        @Override
        public RetirementOptionsData createFromParcel(Parcel in) {
            return new RetirementOptionsData(in);
        }

        @Override
        public RetirementOptionsData[] newArray(int size) {
            return new RetirementOptionsData[size];
        }
    };
}