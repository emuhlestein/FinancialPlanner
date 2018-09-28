package com.intelliviz.lowlevel.data;

import android.os.Parcel;
import android.os.Parcelable;

import static java.lang.Integer.parseInt;

/**
 * Class to handle ages.
 * Created by Ed Muhlestein on 5/23/2017.
 */

public class AgeData implements Parcelable {
    private static final int INVALID_AGE = -1;
    private static final int MIN_VALID_AGE = 0;
    private static final int MAX_VALID_AGE = 200*12;
    private int mNumMonths;

    /**
     * Default constructor.
     */
    public AgeData() {
        mNumMonths = 0;
    }

    public AgeData(String age) {
        parseAge(age);
    }

    public AgeData(String year, String month) {
        String age = year + " " + month;
        parseAge(age);
    }

    /**
     * Constructor.
     *
     * @param year The year.
     * @param month The month.
     */
    public AgeData(int year, int month) {
        setMonths(year, month);
    }

    /**
     * Copy Constructor
     * @param ageData The age.
     */
    public AgeData(AgeData ageData) {
        this(ageData.getNumberOfMonths());
    }

    /**
     * Constructor
     *
     * @param numMonths The number of months.
     */
    public AgeData(int numMonths) {
        setMonths(0, numMonths);
    }

    /**
     * Does this age come on or before the specified age.
     * @param age The age to check.
     * @return True if age comes before this age. False otherwise.
     */
    public boolean isBefore(AgeData age) {
        return getNumberOfMonths() < age.getNumberOfMonths();
    }

    public boolean isAfter(AgeData age) {
        return getNumberOfMonths() > age.getNumberOfMonths();
    }

    public boolean isOnOrAfter(AgeData age) {
        return getNumberOfMonths() >= age.getNumberOfMonths();
    }

    public void addMonths(int numMonths) {
        if(numMonths <= 0) {
            return;
        }
        setMonths(0, mNumMonths + numMonths);
    }

    /**
     * Subtract the specified age from this age.
     * @param ageData The specified age.
     * @return The age difference.
     */
    public AgeData subtract(AgeData ageData) {
        return new AgeData(mNumMonths - ageData.getNumberOfMonths());
    }

    public AgeData subtract(int numMonths) {
        return new AgeData(getNumberOfMonths() - numMonths);
    }

    /**
     * Add the specified age to this age.
     * @param ageData The age.
     * @return The sum age.
     */
    public AgeData add(AgeData ageData) {
        return new AgeData(mNumMonths + ageData.getNumberOfMonths());
    }

    public AgeData add(int numMonths) {
        return new AgeData(getNumberOfMonths() + numMonths);
    }

    public AgeData addYear(int numYears) {
        return new AgeData(getNumberOfMonths() + numYears*12);
    }

    public int diff(AgeData ageData) {
        return Math.abs(mNumMonths - ageData.getNumberOfMonths());
    }

    /**
     * Get the number of months.
     * @return THe number of months.
     */
    public int getNumberOfMonths() {
        return mNumMonths;
    }

    /**
     * Get the year.
     * @return The year.
     */
    public int getYear() {
        return mNumMonths / 12;
    }

    /**
     * Get the month.
     * @return The month.
     */
    public int getMonth() {
        return mNumMonths % 12;
    }

    public boolean isValid() {
        return (mNumMonths != INVALID_AGE);
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
        return Integer.toString(getYear()) + "y " + Integer.toString(getMonth()) + "m";
    }

    /**
     * Get the unformated string.
     * @return The unformatted string.
     */
    public String getUnformattedString() {
        return Integer.toString(getYear()) + " " + Integer.toString(getMonth());
    }

    private void setMonths(int year, int month) {
        int numMonths = year * 12 + month;
        if(numMonths < MIN_VALID_AGE || numMonths > MAX_VALID_AGE) {
            mNumMonths = INVALID_AGE;
        } else {
            mNumMonths = numMonths;
        }
    }

    private void parseAge(String age) {
        if(age == null || age.isEmpty()) {
            mNumMonths = INVALID_AGE;
        }

        String trimmedAge = trimAge(age);

        String[] tokens = trimmedAge.split(" ");
        int year;
        int month = 0;
        if(tokens.length == 1) {
            try {
                year = parseInt(tokens[0]);
                setMonths(year, month);
            } catch (NumberFormatException e) {
                mNumMonths = INVALID_AGE;
            }
        } else if(tokens.length == 2) {
            try {
                year = parseInt(tokens[0]);
                month = parseInt(tokens[1]);
                setMonths(year, month);
            } catch (NumberFormatException e) {
                mNumMonths = INVALID_AGE;
            }
        } else {
            mNumMonths = INVALID_AGE;
        }
    }

    private String trimAge(String age) {
        age = age.replace("y", "");
        age = age.replace("m", "");
        return age;
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

        return(ageData.mNumMonths == mNumMonths);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 17 * result + mNumMonths;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mNumMonths);
    }

    private void readFromParcel(Parcel in) {
        mNumMonths = in.readInt();
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
