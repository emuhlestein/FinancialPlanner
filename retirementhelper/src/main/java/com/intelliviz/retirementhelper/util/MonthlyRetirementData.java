package com.intelliviz.retirementhelper.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/31/2017.
 */

public class MonthlyRetirementData implements Parcelable {
    protected MonthlyRetirementData(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MonthlyRetirementData> CREATOR = new Creator<MonthlyRetirementData>() {
        @Override
        public MonthlyRetirementData createFromParcel(Parcel in) {
            return new MonthlyRetirementData(in);
        }

        @Override
        public MonthlyRetirementData[] newArray(int size) {
            return new MonthlyRetirementData[size];
        }
    };
}
