package com.intelliviz.retirementhelper.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 5/1/2017.
 */

public class SavingsIncomeData extends IncomeTypeData {
    private String mInterest;
    private String mMonthlyIncrease;
    private List<BalanceData> mBalanceDataList = new ArrayList<>();

    public SavingsIncomeData() {
        super();
    }

    public SavingsIncomeData(int type) {
        super(type);
    }

    public SavingsIncomeData(long id, String name, int type) {
        super(id);
        mInterest = "0";
        mMonthlyIncrease = "0";
    }

    public SavingsIncomeData(long id, String name, int type, String interest, String monthlyIncrease) {
        super(id, name, type);
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
    }

    public String getInterest() {
        return mInterest;
    }

    public String getMonthlyIncrease() {
        return mMonthlyIncrease;
    }

    public void addBalance(BalanceData bd) {
        mBalanceDataList.add(bd);
    }

    public List<BalanceData> getBalanceDataList() {
        return mBalanceDataList;
    }

    public SavingsIncomeData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mInterest);
        dest.writeString(mMonthlyIncrease);
        dest.writeTypedList(mBalanceDataList);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mInterest = in.readString();
        mMonthlyIncrease = in.readString();
        in.readTypedList(mBalanceDataList, BalanceData.CREATOR);
    }

    public static final Parcelable.Creator<SavingsIncomeData> CREATOR = new Parcelable.Creator<SavingsIncomeData>()
    {
        @Override
        public SavingsIncomeData createFromParcel(Parcel in) {
            return new SavingsIncomeData(in);
        }

        @Override
        public SavingsIncomeData[] newArray(int size) {
            return new SavingsIncomeData[size];
        }
    };
}
