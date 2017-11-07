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

    public void addMonths(int numMonths) {
        if(numMonths <= 0) {
            return;
        }

        mMonth += numMonths;
        int years = mMonth / 12;
        mYear += years;

        mMonth = mMonth % 12;
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
        return Integer.toString(mYear) + "y " + Integer.toString(mMonth) + "m";
    }

    /**
     * Get the unformated string.
     * @return The unformatted string.
     */
    public String getUnformattedString() {
        return Integer.toString(mYear) + " " + Integer.toString(mMonth);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(obj == this) {
            return true;
        }

        if(!(obj instanceof AgeData)) {
            return false;
        }

        AgeData ageData = (AgeData)obj;

        if(ageData.mYear != mYear) {
            return false;
        }

        if(ageData.mMonth != mMonth) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 17 * result + mYear;
        result = 31 * result + mMonth;
        return result;
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

    private void readFromParcel(Parcel in) {
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
