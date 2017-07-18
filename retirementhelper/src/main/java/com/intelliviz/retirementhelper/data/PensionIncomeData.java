package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.RetirementConstants;

/**
 * Class for pension income data.
 * Created by Ed Muhlestein on 5/11/2017.
 */

public class PensionIncomeData extends IncomeTypeData {
    private String mStartAge;
    private double mMonthlyBenefit;

    /**
     * Constructor.
     */
    public PensionIncomeData() {
        super(RetirementConstants.INCOME_TYPE_PENSION);
    }

    /**
     * Constructor.
     * @param id The database id.
     * @param name The income source name.
     * @param type The income source type.
     * @param startAge The start age.
     * @param monthlyBenefit The monthly benefir.
     */
    public PensionIncomeData(long id, String name, int type, String startAge, double monthlyBenefit) {
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
        return mMonthlyBenefit;
    }

    /**
     * Get the start age.
     * @return The start age.
     */
    public String getStartAge() {
        return mStartAge;
    }

    private PensionIncomeData(Parcel in) {
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

    public static final Parcelable.Creator<PensionIncomeData> CREATOR = new Parcelable.Creator<PensionIncomeData>()
    {
        @Override
        public PensionIncomeData createFromParcel(Parcel in) {
            return new PensionIncomeData(in);
        }

        @Override
        public PensionIncomeData[] newArray(int size) {
            return new PensionIncomeData[size];
        }
    };
}
