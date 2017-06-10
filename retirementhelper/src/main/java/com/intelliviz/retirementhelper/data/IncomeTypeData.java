package com.intelliviz.retirementhelper.data;

import android.os.Parcel;

/**
 * Created by edm on 5/12/2017.
 */

public abstract class IncomeTypeData implements IncomeType {
    private long mId;
    private String mName;
    private int mType;

    public IncomeTypeData() {
        this(-1, "", 0);
    }

    public IncomeTypeData(int type) {
        this(-1, "", type);
    }

    public IncomeTypeData(long id) {
        this(id, "", 0);
    }

    public IncomeTypeData(long id, String name, int type) {
        mId = id;
        mName = name;
        mType = type;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeInt(mType);
    }

    public void readFromParcel(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mType = in.readInt();
    }
}
