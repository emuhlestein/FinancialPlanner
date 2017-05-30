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
    private int mIncludesPenalty;

    public MilestoneData(AgeData age, String amount, String balance) {
        mAge = age;
        mAmount = amount;
        mBalance = balance;
        mIncludesPenalty = 0;
    }

    public MilestoneData(AgeData age, String amount, String balance, boolean includesPenalty) {
        mAge = age;
        mAmount = amount;
        mBalance = balance;
        mIncludesPenalty = includesPenalty ? 1 : 0;
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

    public int getIncludesPenalty() {
        return mIncludesPenalty;
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
        dest.writeInt(mIncludesPenalty);
    }

    public void readFromParcel(Parcel in) {
        mAge = in.readParcelable(AgeData.class.getClassLoader());
        mAmount = in.readString();
        mBalance = in.readString();
        mIncludesPenalty = in.readInt();
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
