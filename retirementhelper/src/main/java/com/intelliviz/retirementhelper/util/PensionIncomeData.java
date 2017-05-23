package com.intelliviz.retirementhelper.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/11/2017.
 */

public class PensionIncomeData extends IncomeTypeData {
    private String mStartAge;
    private String mMonthlyBenefit;

    public PensionIncomeData(long id, String name, int type, String startAge, String monthlyBenefit) {
        super(id, name, type);
        mStartAge = startAge;
        mMonthlyBenefit = monthlyBenefit;
    }

    public String getStartAge() {
        return mStartAge;
    }

    public String getMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    public PensionIncomeData(Parcel in) {
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
        dest.writeString(mMonthlyBenefit);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mStartAge = in.readString();
        mMonthlyBenefit = in.readString();
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
