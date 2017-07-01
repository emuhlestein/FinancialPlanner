package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to manage balances.
 * Created by Ed Muhlestein on 5/1/2017.
 */

public class BalanceData implements Parcelable {
    private double mBalance;
    private String mDate;

    /**
     * Constructor.
     * @param balance The balance/
     * @param date The date of the balance.
     */
    public BalanceData(double balance, String date) {
        mBalance = balance;
        mDate = date;
    }

    /**
     * Get the balance.
     * @return The balance.
     */
    public double getBalance() {
        return mBalance;
    }

    /**
     * Get the date.
     * @return The date.
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Constructor used buy parcealble.
     * @param in The parcel.
     */
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

    private void readFromParcel(Parcel in) {
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
