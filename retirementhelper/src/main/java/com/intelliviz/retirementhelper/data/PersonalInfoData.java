package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/20/2017.
 */

public class PersonalInfoData implements Parcelable {
    private final String mBirthdate;

    public PersonalInfoData(String birthdate) {
        mBirthdate = birthdate;
    }

    public String getBirthdate() {
        return mBirthdate;
    }

    public PersonalInfoData(Parcel in) {
        mBirthdate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBirthdate);
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
