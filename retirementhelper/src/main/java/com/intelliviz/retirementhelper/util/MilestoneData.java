package com.intelliviz.retirementhelper.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 5/29/2017.
 */

public class MilestoneData implements Parcelable {
    private AgeData mStartAge;
    private AgeData mEndAge;
    private String mMonthlyAmount;
    private String mStartBalance;
    private String mPenaltyAmount;
    private int mLengthOfRetirement; // in months
    private List<Double> mMonthlyBalances = new ArrayList<>(); // monthly balances

    public MilestoneData(AgeData startAge, AgeData endAge, String amount, String balance, String penaltyAmount, int numMonths, List<Double> monthlyBalances) {
        mStartAge = startAge;
        mEndAge = endAge;
        mMonthlyAmount = amount;
        mStartBalance = balance;
        mPenaltyAmount = penaltyAmount;
        mLengthOfRetirement = numMonths;
        mMonthlyBalances = monthlyBalances;
    }

    public MilestoneData(Parcel in) {
        readFromParcel(in);
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public AgeData getEndAge() {
        return mEndAge;
    }

    public String getMonthlyAmount() {
        return mMonthlyAmount;
    }

    public String getStartBalance() {
        return mStartBalance;
    }

    public String getPenaltyAmount() {
        return mPenaltyAmount;
    }

    public int getLengthOfRetirement() {
        return mLengthOfRetirement;
    }

    public List<Double> getMonthlyBalances() {
        return mMonthlyBalances;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mStartAge, flags);
        dest.writeParcelable(mEndAge, flags);
        dest.writeString(mMonthlyAmount);
        dest.writeString(mStartBalance);
        dest.writeString(mPenaltyAmount);
        dest.writeInt(mLengthOfRetirement);
        dest.writeList(mMonthlyBalances);
    }

    public void readFromParcel(Parcel in) {
        mStartAge = in.readParcelable(AgeData.class.getClassLoader());
        mEndAge = in.readParcelable(AgeData.class.getClassLoader());
        mMonthlyAmount = in.readString();
        mStartBalance = in.readString();
        mPenaltyAmount = in.readString();
        mLengthOfRetirement = in.readInt();
        in.readList(mMonthlyBalances, Double.class.getClassLoader());
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
