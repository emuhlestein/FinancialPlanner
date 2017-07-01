package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Class to manager retirement milestone ages.
 * Created by Ed Muhlestein on 6/24/2017.
 */

public class MilestoneAgeData implements Parcelable, Comparable {
    private long mId;
    private AgeData mAge;

    /**
     * Constructor.
     * @param id The database id.
     * @param age The age.
     */
    public MilestoneAgeData(long id, AgeData age) {
        mId = id;
        mAge = age;
    }

    /**
     * get the database id.
     * @return The database id.
     */
    public long getId() {
        return mId;
    }

    /**
     * Get the age.
     * @return The age.
     */
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

    private void readFromParcel(Parcel in) {
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
