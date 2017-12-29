package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * CLass for managing retirement options.
 * Created by Ed Muhlestein on 5/16/2017.
 */
public class RetirementOptionsData implements Parcelable {
    private AgeData mEndAge;
    private String mBirthdate;

    public RetirementOptionsData(AgeData endAge, String birthdate) {
        mEndAge = endAge;
        mBirthdate = birthdate;
    }

    public AgeData getEndAge() {
        return mEndAge;
    }

    public String getBirthdate() {
        return mBirthdate;
    }

    protected RetirementOptionsData(Parcel in) {
        mEndAge = in.readParcelable(AgeData.class.getClassLoader());
        mBirthdate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mEndAge, 0);
        dest.writeString(mBirthdate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RetirementOptionsData> CREATOR = new Parcelable.Creator<RetirementOptionsData>() {
        @Override
        public RetirementOptionsData createFromParcel(Parcel in) {
            return new RetirementOptionsData(in);
        }

        @Override
        public RetirementOptionsData[] newArray(int size) {
            return new RetirementOptionsData[size];
        }
    };
}