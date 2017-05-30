package com.intelliviz.retirementhelper.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edm on 5/23/2017.
 */

public class AgeData implements Parcelable {
    private int mYear;
    private int mMonth;

    public AgeData() {
        mYear = 0;
        mMonth = 0;
    }

    public AgeData(String age) {
        float fage = Float.parseFloat(age);
        mYear = (int)fage;
        float fmonths = fage - mYear;
        mMonth = (int)(fmonths * 12);
    }

    public AgeData(int year, int month) {
        mYear = year;
        mMonth = month;
    }

    /**
     * Does ageDate come on or before this date.
     * @param age
     * @return
     */
    public boolean isBefore(AgeData age) {
        if(mYear < age.getYear()) {
            return true;
        } else if(mYear == age.getYear()) {
            if(mMonth < age.getMonth() ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public AgeData subtract(AgeData ageData) {
        int year = mYear - ageData.getYear();
        int month = mMonth - ageData.getMonth();
        if(month < 0) {
            year--;
            month = 12 + month;
        }

        if(year < 0) {
            return new AgeData();
        } else {
            return new AgeData(year, month);
        }
    }

    public float getAge() {
        return (float)(mYear + mMonth / 12.0);
    }

    public int getNumberOfMonths() {
        return mYear * 12 + mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    public AgeData(Parcel in) {
        readFromParcel(in);
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
