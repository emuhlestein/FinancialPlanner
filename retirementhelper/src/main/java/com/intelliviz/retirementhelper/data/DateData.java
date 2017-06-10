package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/20/2017.
 */

public class DateData implements Parcelable {
    private String mYear;
    private String mMonth;
    private String mDay;

    public DateData(String year, String month, String day) {
        mYear = year;
        mMonth = month;
        mDay = day;
    }

    public String getYear() {
        return mYear;
    }

    public String getMonth() {
        return mMonth;
    }

    public String getDay() {
        return mDay;
    }

    public DateData(Parcel in) {
        mYear = in.readString();
        mMonth = in.readString();
        mDay = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mYear);
        dest.writeString(mMonth);
        dest.writeString(mDay);
    }

    public static final Creator<DateData> CREATOR = new Creator<DateData>() {
        @Override
        public DateData createFromParcel(Parcel source) {
            return new DateData(source);
        }

        @Override
        public DateData[] newArray(int size) {
            return new DateData[size];
        }
    };
}
