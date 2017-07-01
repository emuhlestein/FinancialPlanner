package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class for personal info data.
 * Created by Ed Muhlestein on 5/20/2017.
 */

public class PersonalInfoData implements Parcelable {
    private String mBirthdate;

    /**
     * Constructor.
     * @param birthdate The birthdate.
     */
    public PersonalInfoData(String birthdate) {
        mBirthdate = birthdate;
    }

    /**
     * Get the birthdate.
     * @return The birthdate.
     */
    public String getBirthdate() {
        return mBirthdate;
    }

    private PersonalInfoData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBirthdate);
    }

    public void readFromParcel(Parcel in) {
        mBirthdate = in.readString();
    }

    public static final Creator<PersonalInfoData> CREATOR = new Creator<PersonalInfoData>() {
        @Override
        public PersonalInfoData createFromParcel(Parcel source) {
            return new PersonalInfoData(source);
        }

        @Override
        public PersonalInfoData[] newArray(int size) {
            return new PersonalInfoData[size];
        }
    };
}
