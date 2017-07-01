package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to handle ages.
 * Created by Ed Muhlestein on 5/23/2017.
 */

public class AgeData implements Parcelable {
    private int mYear;
    private int mMonth;

    /**
     * Default constructor.
     */
    public AgeData() {
        mYear = 0;
        mMonth = 0;
    }

    /**
     * Constructor.
     *
     * @param year The year.
     * @param month The month.
     */
    public AgeData(int year, int month) {
        mYear = year;
        mMonth = month;
    }

    /**
     * Does this age come on or before the specified age.
     * @param age The age to check.
     * @return True if age comes before this age. False otherwise.
     */
    public boolean isBefore(AgeData age) {
        return (mYear < age.getYear() ||  (mYear == age.getYear() && mMonth < age.getMonth()));
    }

    /**
     * Subtract the specified age from this age.
     * @param ageData The specified age.
     * @return The age difference.
     */
    public AgeData subtract(AgeData ageData) {
        int year = mYear - ageData.getYear();
        int month = mMonth - ageData.getMonth();
        if(month < 0) {
            year--;
            month = 12 + month;
        }

        return new AgeData(year, month);
    }

    /**
     * Get the number of months.
     * @return THe number of months.
     */
    public int getNumberOfMonths() {
        return mYear * 12 + mMonth;
    }

    /**
     * Get the year.
     * @return The year.
     */
    public int getYear() {
        return mYear;
    }

    /**
     * Get the month.
     * @return The month.
     */
    public int getMonth() {
        return mMonth;
    }

    /**
     * Constuctor used by parcelable.
     * @param in THe input parcel from which to extract the values.
     */
    public AgeData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(mYear));
        sb.append("y ");
        sb.append(Integer.toString(mMonth));
        sb.append("m");
        return sb.toString();
    }

    /**
     * Get the unformated string.
     * @return The unformatted string.
     */
    public String getUnformattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(mYear));
        sb.append(" ");
        sb.append(Integer.toString(mMonth));
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mYear);
        dest.writeInt(mMonth);
    }

    /**
     * Read the values from the parcel.
     * @param in THe parcel.
     */
    public void readFromParcel(Parcel in) {
        mYear = in.readInt();
        mMonth = in.readInt();
    }

    public static final Parcelable.Creator<AgeData> CREATOR = new Parcelable.Creator<AgeData>()
    {
        @Override
        public AgeData createFromParcel(Parcel in) {
            return new AgeData(in);
        }

        @Override
        public AgeData[] newArray(int size) {
            return new AgeData[size];
        }
    };
}
