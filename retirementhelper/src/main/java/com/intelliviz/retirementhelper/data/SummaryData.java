package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class for summary data.
 * Created by Ed Muhlestein on 6/14/2017.
 */

public class SummaryData implements Parcelable {
    private String mAge;
    private String mMonthlyBenefit;

    /**
     * Constructor.
     * @param age The age.
     * @param monthlyBenefit The monthly benefit.
     */
    public SummaryData(String age, String monthlyBenefit) {
        mAge = age;
        mMonthlyBenefit = monthlyBenefit;
    }

    /**
     * Get the age.
     * @return The age.
     */
    public String getAge() {
        return mAge;
    }

    /**
     * Get the monthly benefit.
     * @return The monthly benefit.
     */
    public String getMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    private SummaryData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAge);
        dest.writeString(mMonthlyBenefit);
    }

    private void readFromParcel(Parcel in) {
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
