package com.intelliviz.retirementhelper.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/20/2017.
 */

public class PersonalInfoData implements Parcelable {
    private final String mName;
    private final String mBirthdate;
    private final String mEmail;
    private final String mPIN;
    private final String mPassword;

    public PersonalInfoData(String name, String birthdate, String email, String PIN, String password) {
        mName = name;
        mBirthdate = birthdate;
        mEmail = email;
        mPIN = PIN;
        mPassword = password;
    }

    public String getName() {
        return mName;
    }

    public String getBirthdate() {
        return mBirthdate;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPIN() {
        return mPIN;
    }

    public String getPassword() {
        return mPassword;
    }

    public PersonalInfoData(Parcel in) {
        mName = in.readString();
        mBirthdate = in.readString();
        mEmail = in.readString();
        mPIN = in.readString();
        mPassword = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mBirthdate);
        dest.writeString(mEmail);
        dest.writeString(mPIN);
        dest.writeString(mPassword);
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
