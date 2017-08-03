package com.intelliviz.retirementhelper.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.DataBaseUtils.getMilestoneAges;
import static com.intelliviz.retirementhelper.util.GovPensionHelper.getFullRetirementAge;

/**
 * Class to manager government pensions. e.g. social security.
 * Created by Ed Muhlestein on 5/11/2017.
 */

public class GovPensionIncomeData extends IncomeTypeData {
    private static double MAX_SS_PENALTY = 30.0;
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

    public List<MilestoneData> getMilestones(Context context, RetirementOptionsData rod) {
        List<MilestoneData> milestones = new ArrayList<>();
        List<MilestoneAgeData> ages = getMilestoneAges(context, rod);
        if(ages.isEmpty()) {
            return milestones;
        }

        String birthDate = rod.getBirthdate();
        int birthYear = SystemUtils.getBirthYear(birthDate);
        double monthlyBenefit = mMonthlyBenefit;

        AgeData minimumAge = new AgeData(62, 0);

        MilestoneData milestone;
        for(MilestoneAgeData msad : ages) {
            AgeData age = msad.getAge();
            if(age.isBefore(minimumAge)) {
                milestone = new MilestoneData(age, null, minimumAge, 0, 0, 0, 0, 0);
            } else {
                double factor = getSocialSecurityAdjustment(birthDate, age);

                double factorAmount = (monthlyBenefit * factor) / 100.0;
                double adjustedBenefit = monthlyBenefit - factorAmount;
                milestone = new MilestoneData(age, null, minimumAge, adjustedBenefit, 0, 0, 0, 0);
            }
            milestones.add(milestone);
        }
        return milestones;
    }

    private static double getSocialSecurityAdjustment(String birthDate, AgeData startAge) {
        int year = SystemUtils.getBirthYear(birthDate);
        AgeData retireAge = getFullRetirementAge(year);
        AgeData diffAge = retireAge.subtract(startAge);
        int numMonths = diffAge.getNumberOfMonths();
        if(numMonths > 0) {
            // this is early retirement; the adjustment will be a penalty.
            if(numMonths < 37) {
                return (numMonths * 5.0) / 9.0;
            } else {
                double penalty = (numMonths * 5.0) / 12.0;
                if(penalty > MAX_SS_PENALTY) {
                    penalty = MAX_SS_PENALTY;
                }
                return penalty;
            }
        } else if(numMonths < 0) {
            // this is delayed retirement; the adjustment is a credit.
            double annualCredit = getDelayedCredit(year);
            return numMonths * (annualCredit / 12.0);
        } else {
            return 0; // exact retirement age
        }
    }

    /**
     * Get the percent credit per year.
     * @param birthyear The birth year.
     * @return THe delayed credit.
     */
    private static double getDelayedCredit(int birthyear) {
        if(birthyear < 1925) {
            return 3;
        } else if(birthyear < 1927) {
            return 3.5;
        } else if(birthyear < 1929) {
            return 4.0;
        } else if(birthyear < 1931) {
            return 4.5;
        } else if(birthyear < 1933 ) {
            return 5.0;
        } else if(birthyear < 1935) {
            return 5.5;
        } else if(birthyear < 1937) {
            return 6.0;
        } else if(birthyear < 1939) {
            return 6.5;
        } else if(birthyear < 1941) {
            return 7.0;
        } else if(birthyear < 1943) {
            return 7.5;
        } else {
            return 8.0; // the max
        }
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
