package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class for retirement milestone data.
 * Created by Ed Muhlestein on 5/29/2017.
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

    /**
     * Constructor.
     * @param startAge The start age.
     */
    public MilestoneData(AgeData startAge) {
        this(startAge, startAge, startAge, 0, 0, 0, 0, 0);
    }

    /**
     * Constructor.
     * @param startAge The start age.
     * @param endAge The end age.
     * @param minimumAge The minimum age.
     * @param monthlyBenefit The monthly benefit.
     * @param startBalance The start balance.
     * @param endBalance The end balance.
     * @param penaltyAmount The penalty amount.
     * @param monthsFundsWillLast The number of months the funds will last.
     */
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

    /**
     * Get the start age.
     * @return The start age.
     */
    public AgeData getStartAge() {
        return mStartAge;
    }

    /**
     * Get the end age.
     * @return The end age.
     */
    public AgeData getEndAge() {
        return mEndAge;
    }

    /**
     * Get the minimum age.
     * @return The minimum age.
     */
    public AgeData getMinimumAge() {
        return mMinimumAge;
    }

    /**
     * Get the monthly benefit.
     * @return The monthly benefit.
     */
    public double getMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    /**
     * Get the start balance.
     * @return The start balance.
     */
    public double getStartBalance() {
        return mStartBalance;
    }

    /**
     * Get the end balance.
     * @return The end balance.
     */
    public double getEndBalance() {
        return mEndBalance;
    }

    /**
     * Get the penalty amount.
     * @return The penalty amount.
     */
    public double getPenaltyAmount() {
        return mPenaltyAmount;
    }

    /**
     * Get the number of months the funds will last.
     * @return The number of months.
     */
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
