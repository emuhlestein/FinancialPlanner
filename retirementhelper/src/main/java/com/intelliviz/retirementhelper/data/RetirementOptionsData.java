package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * CLass for managing retirement options.
 * Created by Ed Muhlestein on 5/16/2017.
 */
public class RetirementOptionsData implements Parcelable {
    private AgeData mEndAge;
    private int mWithdrawMode;
    private String mWithdrawAmount;
    private String mBirthdate;
    private String mPercentIncrease;

    public RetirementOptionsData(AgeData endAge, int withdrawMode, String withdrawAmount, String birthdate, String percentIncrease) {
        mEndAge = endAge;
        mWithdrawMode = withdrawMode;
        mWithdrawAmount = withdrawAmount;
        mBirthdate = birthdate;
        mPercentIncrease = percentIncrease;
    }

    public AgeData getEndAge() {
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

    public String getPercentIncrease() {
        return mPercentIncrease;
    }

    protected RetirementOptionsData(Parcel in) {
        mEndAge = in.readParcelable(AgeData.class.getClassLoader());
        mWithdrawMode = in.readInt();
        mWithdrawAmount = in.readString();
        mBirthdate = in.readString();
        mPercentIncrease = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mEndAge, 0);
        dest.writeInt(mWithdrawMode);
        dest.writeString(mWithdrawAmount);
        dest.writeString(mBirthdate);
        dest.writeString(mPercentIncrease);
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