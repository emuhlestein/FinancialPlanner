package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to manager tax deferred income sources.
 * Created by Ed Muhlestein on 5/1/2017.
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

    /**
     * Default constructor.
     */
    public TaxDeferredIncomeData() {
        super(RetirementConstants.INCOME_TYPE_TAX_DEFERRED);
        mMinimumAge = DEFAULT_MIN_AGE;
        mPenalty = DEFAULT_PENALTY;
    }

    /**
     * Constructor.
     * @param id The database id.
     * @param name The name.
     * @param type The income type.
     * @param minimumAge The minimum age.
     * @param annualRate The annual interest rate.
     * @param monthlyAdd The monthly addition.
     * @param penalty The penalty.
     * @param is401k Is this a 401k.
     */
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

    /**
     * Get the minimum age.
     * @return The minimum age.
     */
    public String getMinimumAge() {
        return mMinimumAge;
    }

    /**
     * Get the interest rate.
     * @return The interest rate.
     */
    public double getInterestRate() {
        return mInterestRate;
    }

    /**
     * Get the monthly addition.
     * @return The monthly addition.
     */
    public double getMonthAddition() {
        return mMonthAdd;
    }

    /**
     * Get the penalty.
     * @return The penalty.
     */
    public double getPenalty() {
        return mPenalty;
    }

    /**
     * Add a balance.
     * @param bd The balance data to add.
     */
    public void addBalanceData(BalanceData bd) {
        mBalanceDataList.add(bd);
    }

    /**
     * Get the list of balance data.
     * @return The list of balance data.
     */
    public List<BalanceData> getBalanceData() {
        return mBalanceDataList;
    }

    /**
     * Is this income source a 401k.
     * @return True if 401k, otherwise false.
     */
    public int getIs401k() {
        return mIs401k;
    }

    private TaxDeferredIncomeData(Parcel in) {
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
