package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 6/14/2017.
 */

public class SummaryData implements Parcelable {
    private String mAge;
    private String mMonthlyBenefit;

    public SummaryData(String age, String monthlyBenefit) {
        mAge = age;
        mMonthlyBenefit = monthlyBenefit;
    }

    public String getAge() {
        return mAge;
    }

    public String getMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    protected SummaryData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAge);
        dest.writeString(mMonthlyBenefit);
    }

    public void readFromParcel(Parcel in) {
        mAge = in.readString();
        mMonthlyBenefit = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SummaryData> CREATOR = new Creator<SummaryData>() {
        @Override
        public SummaryData createFromParcel(Parcel in) {
            return new SummaryData(in);
        }

        @Override
        public SummaryData[] newArray(int size) {
            return new SummaryData[size];
        }
    };
}
