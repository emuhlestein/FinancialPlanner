package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ed muhlestein on 5/11/2017.
 */

public class PensionIncomeData extends IncomeTypeData {
    private String mStartAge;
    private double mMonthlyBenefit;

    public PensionIncomeData(int type) {
        super(type);
    }

    public PensionIncomeData(long id, String name, int type, String startAge, double monthlyBenefit) {
        super(id, name, type);
        mStartAge = startAge;
        mMonthlyBenefit = monthlyBenefit;
    }

    @Override
    public boolean hasABalance() {
        return false;
    }

    @Override
    public double getBalance() {
        return 0;
    }

    @Override
    public double getMonthlyBenefit(double withdrawalRate) {
        return mMonthlyBenefit;
    }

    public String getStartAge() {
        return mStartAge;
    }

    private PensionIncomeData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mStartAge);
        dest.writeDouble(mMonthlyBenefit);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mStartAge = in.readString();
        mMonthlyBenefit = in.readDouble();
    }

    public static final Parcelable.Creator<PensionIncomeData> CREATOR = new Parcelable.Creator<PensionIncomeData>()
    {
        @Override
        public PensionIncomeData createFromParcel(Parcel in) {
            return new PensionIncomeData(in);
        }

        @Override
        public PensionIncomeData[] newArray(int size) {
            return new PensionIncomeData[size];
        }
    };
}
