package com.intelliviz.retirementhelper.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

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

    public List<MilestoneData> getMilestones(Context context, List<MilestoneAgeData> ages, RetirementOptionsData rod) {
        List<MilestoneData> milestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return milestones;
        }

        AgeData minimumAge = SystemUtils.parseAgeString(mStartAge);
        AgeData endAge = SystemUtils.parseAgeString(rod.getEndAge());
        double monthlyBenefit = mMonthlyBenefit;

        MilestoneData milestone;
        for(MilestoneAgeData msad : ages) {
            AgeData age = msad.getAge();
            if(age.isBefore(minimumAge)) {
                milestone = new MilestoneData(age, endAge, minimumAge, 0, 0, 0, 0, 0);
            } else {
                AgeData diffAge = endAge.subtract(age);
                int numMonths = diffAge.getNumberOfMonths();

                milestone = new MilestoneData(age, endAge, minimumAge, monthlyBenefit, 0, 0, 0, numMonths);
            }
            milestones.add(milestone);
        }
        return milestones;
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
