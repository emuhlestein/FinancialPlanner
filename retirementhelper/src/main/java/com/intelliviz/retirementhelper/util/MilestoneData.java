package com.intelliviz.retirementhelper.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/29/2017.
 */

public class MilestoneData implements Parcelable {
    private AgeData mAge;
    private String mAmount;
    private String mBalance;

    public MilestoneData(AgeData age, String amount, String balance) {
        mAge = age;
        mAmount = amount;
        mBalance = balance;
    }

    public MilestoneData(Parcel in) {
        readFromParcel(in);
    }

    public AgeData getAge() {
        return mAge;
    }

    public String getAmount() {
        return mAmount;
    }

    public String getBalance() {
        return mBalance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mAge, flags);
        dest.writeString(mAmount);
        dest.writeString(mBalance);
    }

    public void readFromParcel(Parcel in) {
        in.readParcelable(AgeData.class.getClassLoader());
        mAmount = in.readString();
        mBalance = in.readString();
    }

    public static final Parcelable.Creator<MilestoneData> CREATOR = new Parcelable.Creator<MilestoneData>()
    {
        @Override
        public MilestoneData createFromParcel(Parcel in) {
            return new MilestoneData(in);
        }

        @Override
        public MilestoneData[] newArray(int size) {
            return new MilestoneData[size];
        }
    };
}
