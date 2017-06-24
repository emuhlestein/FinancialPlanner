package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 5/20/2017.
 */

public class PersonalInfoData implements Parcelable {
    private String mBirthdate;
    private List<AgeData> mAgeList = new ArrayList<>();

    public PersonalInfoData(String birthdate, List<AgeData> ageList) {
        mBirthdate = birthdate;
        mAgeList = ageList;
    }

    public String getBirthdate() {
        return mBirthdate;
    }

    public List<AgeData> getAgeList() {
        return mAgeList;
    }

    public PersonalInfoData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBirthdate);
        dest.writeTypedList(mAgeList);
    }

    public void readFromParcel(Parcel in) {
        mBirthdate = in.readString();
        in.readTypedList(mAgeList, AgeData.CREATOR);
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
