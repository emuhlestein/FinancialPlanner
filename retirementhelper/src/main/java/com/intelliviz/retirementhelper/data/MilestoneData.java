package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/29/2017.
 */

public class MilestoneData implements Parcelable {
    private AgeData mStartAge;
    private AgeData mEndAge;
    private AgeData mMinimumAge;
    private double mMonthlyBenefit;
    private double mStartBalance;
    private double mEndBalance;
    private double mPenaltyAmount;
    private int mMonthsFundsFillLast;

    public MilestoneData(AgeData startAge) {
        this(startAge, null, null, 0, 0, 0, 0, 0);
    }
    
    public MilestoneData(AgeData startAge, AgeData endAge, AgeData minimumAge,
                         double monthlyBenefit, double startBalance, double endBalance,
                         double penaltyAmount, int monthsFundsWillLast) {
        mStartAge = startAge;
        mEndAge = endAge;
        mMinimumAge = minimumAge;
        mMonthlyBenefit = monthlyBenefit;
        mStartBalance = startBalance;
        mEndBalance = endBalance;
        mPenaltyAmount = penaltyAmount;
        mMonthsFundsFillLast = monthsFundsWillLast;
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public AgeData getEndAge() {
        return mEndAge;
    }

    public AgeData getMinimumAge() {
        return mMinimumAge;
    }

    public double getMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    public double getStartBalance() {
        return mStartBalance;
    }

    public double getEndBalance() {
        return mEndBalance;
    }

    public double getPenaltyAmount() {
        return mPenaltyAmount;
    }

    public int getMonthsFundsFillLast() {
        return mMonthsFundsFillLast;
    }

    private MilestoneData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mStartAge, flags);
        dest.writeParcelable(mEndAge, flags);
        dest.writeParcelable(mMinimumAge, flags);
        dest.writeDouble(mMonthlyBenefit);
        dest.writeDouble(mStartBalance);
        dest.writeDouble(mEndBalance);
        dest.writeDouble(mPenaltyAmount);
        dest.writeInt(mMonthsFundsFillLast);
    }

    private void readFromParcel(Parcel in) {
        mStartAge = in.readParcelable(AgeData.class.getClassLoader());
        mEndAge = in.readParcelable(AgeData.class.getClassLoader());
        mMinimumAge = in.readParcelable(AgeData.class.getClassLoader());
        mMonthlyBenefit = in.readDouble();
        mStartBalance = in.readDouble();
        mEndBalance = in.readDouble();
        mPenaltyAmount = in.readDouble();
        mMonthsFundsFillLast = in.readInt();
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
