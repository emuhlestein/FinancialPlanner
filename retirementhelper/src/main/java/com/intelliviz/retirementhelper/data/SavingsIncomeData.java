package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage savings income.
 * Created by Ed Muhlestein on 5/1/2017.
 */
public class SavingsIncomeData extends IncomeTypeData {
    private double mInterest;
    private double mMonthlyIncrease;
    private List<BalanceData> mBalanceDataList = new ArrayList<>();

    /**
     * Default constructor.
     */
    public SavingsIncomeData() {
        super(RetirementConstants.INCOME_TYPE_SAVINGS);
    }

    /**
     * Constructor.
     * @param id The database id.
     * @param name The income source name.
     * @param type The income source type.
     * @param interest The interest.
     * @param monthlyIncrease The monthly increase.
     */
    public SavingsIncomeData(long id, String name, int type, double interest, double monthlyIncrease) {
        super(id, name, type);
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
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
     * Get the interest rate.
     * @return The interest rate.
     */
    public double getInterest() {
        return mInterest;
    }

    /**
     * Get the monthly increase.
     * @return The monthly increase.
     */
    public double getMonthlyIncrease() {
        return mMonthlyIncrease;
    }

    /**
     * Add a balance.
     * @param bd The balance data to add.
     */
    public void addBalance(BalanceData bd) {
        mBalanceDataList.add(bd);
    }

    /**
     * Get the list of balance data.
     * @return THe list of balance data.
     */
    public List<BalanceData> getBalanceDataList() {
        return mBalanceDataList;
    }

    private SavingsIncomeData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(mInterest);
        dest.writeDouble(mMonthlyIncrease);
        dest.writeTypedList(mBalanceDataList);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mInterest = in.readDouble();
        mMonthlyIncrease = in.readDouble();
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
