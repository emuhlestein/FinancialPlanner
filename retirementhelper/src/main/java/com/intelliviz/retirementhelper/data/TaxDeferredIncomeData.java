package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 5/1/2017.
 */

public class TaxDeferredIncomeData extends IncomeTypeData {
    private static final String DEFAULT_MIN_AGE = "59 6";
    private static final double DEFAULT_PENALTY = 10.0;
    private String mMinimumAge;
    private double mInterestRate;
    private double mMonthAdd;
    private double mPenalty;
    private int mIs401k;
    private List<BalanceData> mBalanceDataList = new ArrayList<>();

    public TaxDeferredIncomeData() {
        super();
    }

    public TaxDeferredIncomeData(int type) {
        super(type);
        mMinimumAge = DEFAULT_MIN_AGE;
        mPenalty = DEFAULT_PENALTY;
    }

    public TaxDeferredIncomeData(long id, String name, int type, String minimumAge, double annualRate, double monthlyAdd, double penalty, int is401k) {
        super(id, name, type);
        mMinimumAge = minimumAge;
        mInterestRate = annualRate;
        mMonthAdd = monthlyAdd;
        mPenalty = penalty;
        mIs401k = is401k;
    }

    @Override
    public double getBalance() {
        if(mBalanceDataList.isEmpty()) {
            return 0;
        } else {
            return mBalanceDataList.get(0).getBalance();
        }
    }

    @Override
    public double getMonthlyBenefit(double withdrawalRate) {
        double monthlyInterest = withdrawalRate / 1200.0;
        return getBalance() * monthlyInterest;
    }

    public String getMinimumAge() {
        return mMinimumAge;
    }

    public double getInterestRate() {
        return mInterestRate;
    }

    public double getMonthAddition() {
        return mMonthAdd;
    }

    public double getPenalty() {
        return mPenalty;
    }

    public void addBalanceData(BalanceData bd) {
        mBalanceDataList.add(bd);
    }

    public List<BalanceData> getBalanceData() {
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
        dest.writeDouble(mInterestRate);
        dest.writeDouble(mMonthAdd);
        dest.writeDouble(mPenalty);
        dest.writeInt(mIs401k);
        dest.writeTypedList(mBalanceDataList);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mMinimumAge = in.readString();
        mInterestRate = in.readDouble();
        mMonthAdd = in.readDouble();
        mPenalty = in.readDouble();
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
