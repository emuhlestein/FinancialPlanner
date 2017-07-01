package com.intelliviz.retirementhelper.data;

import android.os.Parcel;

/**
 * Class for a basic income type.
 * Created by Ed Muhlestein on 5/12/2017.
 */

abstract class IncomeTypeData implements IncomeType {
    /**
     * The database id.
     */
    private long mId;

    /**
     * The name of the income type.
     */
    private String mName;

    /**
     * The income type. Can be one of following:
     *      INCOME_TYPE_SAVINGS
     *      INCOME_TYPE_TAX_DEFERRED
     *      INCOME_TYPE_PENSION
     *      INCOME_TYPE_GOV_PENSION
     */
    private int mType;

    /**
     * Default constructor.
     */
    IncomeTypeData() {
        this(-1, "", 0);
    }

    /**
     * Constructor.
     * @param type The income type.
     */
    IncomeTypeData(int type) {
        this(-1, "", type);
    }

    /**
     * Constructor.
     * @param id The database id.
     * @param name The income type name.
     * @param type The income type.
     */
    IncomeTypeData(long id, String name, int type) {
        mId = id;
        mName = name;
        mType = type;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
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
