package com.intelliviz.retirementhelper.data;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;

/**
 * CLass for managing retirement options.
 * Created by Ed Muhlestein on 5/16/2017.
 */
public class RetirementOptionsData implements Parcelable {
    private String mEndAge;
    private int mWithdrawMode;
    private String mWithdrawAmount;
    private String mBirthdate;

    public RetirementOptionsData(String endAge, int withdrawMode, String withdrawAmount, String birthdate) {
        mEndAge = endAge;
        mWithdrawMode = withdrawMode;
        mWithdrawAmount = withdrawAmount;
        mBirthdate = birthdate;
    }

    public String getEndAge() {
        return mEndAge;
    }

    public int getWithdrawMode() {
        return mWithdrawMode;
    }

    public String getWithdrawAmount() {
        return mWithdrawAmount;
    }

    public String getBirthdate() {
        return mBirthdate;
    }

    public static RetirementOptionsData create(RetirementOptionsEntity rom) {
        return new RetirementOptionsData(rom.getEndAge(), rom.getWithdrawMode(), rom.getWithdrawAmount(), rom.getBirthdate());
    }

    public static RetirementOptionsEntity create(int id, RetirementOptionsData rod) {
        return new RetirementOptionsEntity(id, rod.getEndAge(), rod.getWithdrawMode(), rod.getWithdrawAmount(), rod.getBirthdate());
    }

    /**
     * Constructor used by parcelable
     * @param in The parcel.
     */
    @Ignore
    public RetirementOptionsData(Parcel in) {
        mBirthdate = in.readString();
        mEndAge = in.readString();
        mWithdrawMode = in.readInt();
        mWithdrawAmount = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBirthdate);
        dest.writeString(mEndAge);
        dest.writeInt(mWithdrawMode);
        dest.writeString(mWithdrawAmount);
    }

    public static final Creator<RetirementOptionsData> CREATOR = new Creator<RetirementOptionsData>() {
        @Override
        public RetirementOptionsData createFromParcel(Parcel source) {
            return new RetirementOptionsData(source);
        }

        @Override
        public RetirementOptionsData[] newArray(int size) {
            return new RetirementOptionsData[size];
        }
    };
}
