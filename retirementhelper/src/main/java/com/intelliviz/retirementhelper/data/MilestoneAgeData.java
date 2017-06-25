package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by edm on 6/24/2017.
 */

public class MilestoneAgeData implements Parcelable, Comparable {
    private long mId;
    private AgeData mAge;

    public MilestoneAgeData(long id, AgeData age) {
        mId = id;
        mAge = age;
    }

    public long getId() {
        return mId;
    }

    public AgeData getAge() {
        return mAge;
    }

    private MilestoneAgeData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeParcelable(mAge, flags);
    }

    public void readFromParcel(Parcel in) {
        mId = in.readLong();
        mAge = in.readParcelable(AgeData.class.getClassLoader());
    }

    public static final Parcelable.Creator<MilestoneAgeData> CREATOR = new Parcelable.Creator<MilestoneAgeData>()
    {
        @Override
        public MilestoneAgeData createFromParcel(Parcel in) {
            return new MilestoneAgeData(in);
        }

        @Override
        public MilestoneAgeData[] newArray(int size) {
            return new MilestoneAgeData[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        MilestoneAgeData mad = (MilestoneAgeData)o;
        return mAge.getNumberOfMonths()-mad.getAge().getNumberOfMonths();
    }
}
