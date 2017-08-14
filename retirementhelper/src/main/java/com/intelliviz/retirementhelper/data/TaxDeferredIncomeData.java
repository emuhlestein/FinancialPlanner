package com.intelliviz.retirementhelper.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

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
    private double mBalance;
    private int mIs401k;

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
    public TaxDeferredIncomeData(long id, String name, int type, String minimumAge,
                                 double annualRate, double monthlyAdd, double penalty, double balance, int is401k) {
        super(id, name, type);
        mMinimumAge = minimumAge;
        mInterestRate = annualRate;
        mMonthAdd = monthlyAdd;
        mPenalty = penalty;
        mBalance = balance;
        mIs401k = is401k;
    }

    @Override
    public double getBalance() {
        return mBalance;
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
     * Is this income source a 401k.
     * @return True if 401k, otherwise false.
     */
    public int getIs401k() {
        return mIs401k;
    }

    @Override
    public List<MilestoneData> getMilestones(Context context, List<MilestoneAgeData> ages, RetirementOptionsData rod) {
        String endAge = rod.getEndAge();
        double withdrawAmount = parseDouble(rod.getWithdrawAmount());
        List<MilestoneData> milestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return milestones;
        }
        AgeData minimumAge = SystemUtils.parseAgeString(mMinimumAge);
        AgeData endOfLifeAge = SystemUtils.parseAgeString(endAge);

        List<Double> milestoneBalances = getMilestoneBalances(ages, mBalance, mInterestRate, mMonthAdd);

        milestones = getMilestones(endOfLifeAge, minimumAge, mInterestRate, mPenalty, rod.getWithdrawMode(), withdrawAmount, ages, milestoneBalances);
        return milestones;
    }

    @Override
    public List<AgeData> getAges() {
        List<AgeData> ages = new ArrayList<>();
        ages.add(SystemUtils.parseAgeString(mMinimumAge));
        return ages;
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
        dest.writeDouble(mBalance);
        dest.writeInt(mIs401k);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mMinimumAge = in.readString();
        mInterestRate = in.readDouble();
        mMonthAdd = in.readDouble();
        mPenalty = in.readDouble();
        mBalance = in.readDouble();
        mIs401k = in.readInt();
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
