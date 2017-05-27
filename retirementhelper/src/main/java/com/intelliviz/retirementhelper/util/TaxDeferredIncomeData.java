package com.intelliviz.retirementhelper.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 5/1/2017.
 */

public class TaxDeferredIncomeData extends IncomeTypeData {
    private String mMinimumAge;
    private String mInterest;
    private String mMonthAdd;
    private String mPenalty;
    private int mIs401k;
    private List<BalanceData> mBalanceDataList = new ArrayList<>();

    public TaxDeferredIncomeData() {
        super();
    }

    public TaxDeferredIncomeData(int type) {
        super(type);
    }

    public TaxDeferredIncomeData(long id, String name, int type, String minimumAge, String interest, String monthlyAdd, String penalty, int is401k) {
        super(id, name, type);
        mMinimumAge = minimumAge;
        mInterest = interest;
        mMonthAdd = monthlyAdd;
        mPenalty = penalty;
        mIs401k = is401k;
    }

    public String getMinimumAge() {
        return mMinimumAge;
    }

    public String getInterest() {
        return mInterest;
    }

    public String getMonthAddition() {
        return mMonthAdd;
    }

    public String getPenalty() {
        return mPenalty;
    }

    public void addBalance(BalanceData bd) {
        mBalanceDataList.add(bd);
    }

    public List<BalanceData> getBalanceDataList() {
        return mBalanceDataList;
    }

    public int getIs401k() {
        return mIs401k;
    }

    public TaxDeferredIncomeData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mMinimumAge);
        dest.writeString(mInterest);
        dest.writeString(mMonthAdd);
        dest.writeString(mPenalty);
        dest.writeInt(mIs401k);
        dest.writeTypedList(mBalanceDataList);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mMinimumAge = in.readString();
        mInterest = in.readString();
        mMonthAdd = in.readString();
        mPenalty = in.readString();
        mIs401k = in.readInt();
        in.readTypedList(mBalanceDataList, BalanceData.CREATOR);
    }

    public static final Parcelable.Creator<TaxDeferredIncomeData> CREATOR = new Parcelable.Creator<TaxDeferredIncomeData>()
    {
        @Override
        public TaxDeferredIncomeData createFromParcel(Parcel in) {
            return new TaxDeferredIncomeData(in);
        }

        @Override
        public TaxDeferredIncomeData[] newArray(int size) {
            return new TaxDeferredIncomeData[size];
        }
    };
}
