package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.RetirementConstants;

/**
 * Class to manager government pensions. e.g. social security.
 * Created by Ed Muhlestein on 5/11/2017.
 */

public class GovPensionIncomeData extends IncomeTypeData {
    private String mStartAge;
    private double mMonthlyBenefit;

    /**
     * Default constructor.
     */
    public GovPensionIncomeData() {
        super(RetirementConstants.INCOME_TYPE_GOV_PENSION);
    }

    /**
     * Constructor.
     * @param id The database id.
     * @param name The income type name.
     * @param type The income type.
     * @param startAge The start age.
     * @param monthlyBenefit The monthly benefit.
     */
    public GovPensionIncomeData(long id, String name, int type, String startAge, double monthlyBenefit) {
        super(id, name, type);
        mStartAge = startAge;
        mMonthlyBenefit = monthlyBenefit;
    }

    @Override
    public double getBalance() {
        return 0;
    }

    @Override
    public double getMonthlyBenefit(double withdrawalRate) {
        return 0;
    }

    public String getStartAge() {
        return mStartAge;
    }

    public double getMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    private GovPensionIncomeData(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mStartAge);
        dest.writeDouble(mMonthlyBenefit);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mStartAge = in.readString();
        mMonthlyBenefit = in.readDouble();
    }

    public static final Parcelable.Creator<GovPensionIncomeData> CREATOR = new Parcelable.Creator<GovPensionIncomeData>()
    {
        @Override
        public GovPensionIncomeData createFromParcel(Parcel in) {
            return new GovPensionIncomeData(in);
        }

        @Override
        public GovPensionIncomeData[] newArray(int size) {
            return new GovPensionIncomeData[size];
        }
    };
}
