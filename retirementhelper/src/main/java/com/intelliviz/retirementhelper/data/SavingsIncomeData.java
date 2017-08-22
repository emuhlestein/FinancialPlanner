package com.intelliviz.retirementhelper.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * Class to manage savings income.
 * Created by Ed Muhlestein on 5/1/2017.
 */
public class SavingsIncomeData extends IncomeTypeData {
    private double mInterest;
    private double mMonthlyIncrease;
    private double mBalance;

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
     * @param balance The balance.
     */
    public SavingsIncomeData(long id, String name, int type, double interest, double monthlyIncrease, double balance) {
        super(id, name, type);
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
        mBalance = balance;
    }

    @Override
    public double getBalance() {
        return mBalance;
    }

    @Override
    public double getMonthlyBenefitForAge(AgeData age) {
        return 0;
    }

    @Override
    public double getFullMonthlyBenefit() {
        double withdrawalRate = 4; // TODO need to move into rules class.
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

    public List<MilestoneData> getMilestones(Context context, List<MilestoneAgeData> ages, RetirementOptionsData rod) {
        String endAge = rod.getEndAge();
        double withdrawAmount = parseDouble(rod.getWithdrawAmount());
        List<MilestoneData> milestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return milestones;
        }

        AgeData endOfLifeAge = SystemUtils.parseAgeString(endAge);

        List<Double> milestoneBalances = getMilestoneBalances(ages, mBalance, mInterest, mMonthlyIncrease);

        milestones = getMilestones(endOfLifeAge, null, mInterest, 0, rod.getWithdrawMode(), withdrawAmount, ages, milestoneBalances);
        return milestones;
    }

    @Override
    public List<AgeData> getAges() {
        return Collections.emptyList();
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
        dest.writeDouble(mBalance);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mInterest = in.readDouble();
        mMonthlyIncrease = in.readDouble();
        mBalance = in.readDouble();
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
