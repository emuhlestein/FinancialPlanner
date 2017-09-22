package com.intelliviz.retirementhelper.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to manager government pensions. e.g. social security.
 * Created by Ed Muhlestein on 5/11/2017.
 */

public class GovPensionIncomeData extends IncomeTypeData {
    private SocialSecurityRules mRules;
    private String mMinAge;
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
     * @param minAge The start age.
     * @param monthlyBenefit The monthly benefit.
     */
    public GovPensionIncomeData(long id, String name, int type, String minAge, double monthlyBenefit) {
        super(id, name, type);
        mMinAge = minAge;
        mMonthlyBenefit = monthlyBenefit;
    }

    @Override
    public double getBalance() {
        return 0;
    }

    @Override
    public double getMonthlyBenefitForAge(AgeData age) {
        if(mRules != null) {
            AgeData startAge = SystemUtils.parseAgeString(mMinAge);
            return mRules.getMonthlyBenefitForAge(startAge);
        } else {

        }
        return mMonthlyBenefit;
    }

    @Override
    public double getFullMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    public AgeData getFullRetirementAge() {
        return new AgeData(); //mRules.getFullRetirementAge();
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof SocialSecurityRules) {
            mRules = (SocialSecurityRules)rules;
        } else {
            mRules = null;
        }
    }

    public String getMinAge() {
        return mMinAge;
    }

    @Override
    public List<MilestoneData> getMilestones(List<MilestoneAgeData> ages, RetirementOptionsData rod) {
        List<MilestoneData> milestones = new ArrayList<>();
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
            if(mRules != null) {
                monthlyBenefit = mRules.getMonthlyBenefitForAge(age);
                milestone = new MilestoneData(age, null, minimumAge, monthlyBenefit, 0, 0, 0, 0);
            /*
            if(age.isBefore(minimumAge)) {
                milestone = new MilestoneData(age, null, minimumAge, 0, 0, 0, 0, 0);
            } else {
                double factor = getSocialSecurityAdjustment(birthDate, age);

                double factorAmount = (monthlyBenefit * factor) / 100.0;
                double adjustedBenefit = monthlyBenefit - factorAmount;
                milestone = new MilestoneData(age, null, minimumAge, adjustedBenefit, 0, 0, 0, 0);
            }
            */
            }
            //milestones.add(milestone);
        }
        return milestones;
    }

    @Override
    public List<AgeData> getAges() {
        List<AgeData> ages = new ArrayList<>();
        if(mRules != null) {
            ages.add(mRules.getFullRetirementAge());
            ages.add(mRules.getMinimumAge());
            ages.add(mRules.getMaximumAge());
        }
        return ages;
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
        dest.writeString(mMinAge);
        dest.writeDouble(mMonthlyBenefit);
        dest.writeParcelable(mRules, flags);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mMinAge = in.readString();
        mMonthlyBenefit = in.readDouble();
        mRules = in.readParcelable(SocialSecurityRules.class.getClassLoader());
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
