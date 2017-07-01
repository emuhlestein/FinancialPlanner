package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * CLass for managing retirement options.
 * Created by Ed Muhlestein on 5/16/2017.
 */
public class RetirementOptionsData implements Parcelable {
    private final String mBirthdate;
    private final String mEndAge;
    private final int mWithdrawMode;
    private final String mWithdrawAmount;

    /**
     * Constructor.
     * @param birthdate The birthdate.
     * @param endAge The end age.
     * @param withdrawMode The withdrawal mode.
     * @param withdrawAmount The withdrawal amount.
     */
    public RetirementOptionsData(String birthdate, String endAge, int withdrawMode, String withdrawAmount) {
        mBirthdate = birthdate;
        mEndAge = endAge;
        mWithdrawMode = withdrawMode;
        mWithdrawAmount = withdrawAmount;
    }

    /**
     * Get the birthdate.
     * @return The birthdate.
     */
    public String getBirthdate() {
        return mBirthdate;
    }

    /**
     * Get the end age.
     * @return The end age.
     */
    public String getEndAge() {
        return mEndAge;
    }

    /**
     * Get the withdrawal mode.
     * @return The withdrawal mode.
     */
    public int getWithdrawMode() {
        return mWithdrawMode;
    }

    /**
     * Get the withdrawal amount.
     * @return The withdrawal amount.
     */
    public String getWithdrawAmount() {
        return mWithdrawAmount;
    }

    /**
     * Constructor used by parcelable
     * @param in The parcel.
     */
    public RetirementOptionsData(Parcel in) {
        mBirthdate = in.readString();
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
        dest.writeString(mBirthdate);
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
