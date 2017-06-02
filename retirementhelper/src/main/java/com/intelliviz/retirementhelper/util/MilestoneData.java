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
    private double mMonthlyAmount;
    private double mStartBalance;
    private double mPenaltyAmount;
    private List<Double> mMonthlyBalances = new ArrayList<>(); // monthly balances

    public MilestoneData(AgeData startAge, AgeData endAge, AgeData minimumAge, double amount, double balance, double penaltyAmount, List<Double> monthlyBalances) {
        mStartAge = startAge;
        mEndAge = endAge;
        mMonthlyAmount = amount;
        mStartBalance = balance;
        mPenaltyAmount = penaltyAmount;
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

    public double getMonthlyAmount() {
        return mMonthlyAmount;
    }

    public double getStartBalance() {
        return mStartBalance;
    }

    public double getPenaltyAmount() {
        return mPenaltyAmount;
    }


    public int getLengthOfRetirement() {
        AgeData ageData = mEndAge.subtract(mStartAge);
        return ageData.getNumberOfMonths();
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
        dest.writeDouble(mMonthlyAmount);
        dest.writeDouble(mStartBalance);
        dest.writeDouble(mPenaltyAmount);
        dest.writeList(mMonthlyBalances);
    }

    public void readFromParcel(Parcel in) {
        mStartAge = in.readParcelable(AgeData.class.getClassLoader());
        mEndAge = in.readParcelable(AgeData.class.getClassLoader());
        mMonthlyAmount = in.readDouble();
        mStartBalance = in.readDouble();
        mPenaltyAmount = in.readDouble();
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
