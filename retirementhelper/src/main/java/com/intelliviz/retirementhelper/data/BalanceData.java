package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/1/2017.
 */

public class BalanceData implements Parcelable {
    private double mBalance;
    private String mDate;

    public BalanceData(double balance, String date) {
        mBalance = balance;
        mDate = date;
    }

    public double getBalance() {
        return mBalance;
    }

    public String getDate() {
        return mDate;
    }

    public BalanceData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mBalance);
        dest.writeString(mDate);
    }

    public void readFromParcel(Parcel in) {
        mBalance = in.readDouble();
        mDate = in.readString();
    }

    public static final Parcelable.Creator<BalanceData> CREATOR = new Parcelable.Creator<BalanceData>()
    {
        @Override
        public BalanceData createFromParcel(Parcel in) {
            return new BalanceData(in);
        }

        @Override
        public BalanceData[] newArray(int size) {
            return new BalanceData[size];
        }
    };
}
