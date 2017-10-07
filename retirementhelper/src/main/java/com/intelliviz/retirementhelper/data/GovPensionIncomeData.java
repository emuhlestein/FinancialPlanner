package com.intelliviz.retirementhelper.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to manager government pensions. e.g. social security.
 * Created by Ed Muhlestein on 5/11/2017.
 */
public class GovPensionIncomeData extends IncomeTypeData {
    public static final String TABLE_NAME = "gov_pension_income";

    @Ignore
    private SocialSecurityRules mRules;

    private long income_type_id;

    @ColumnInfo(name = GovPensionEntity.MIN_AGE_FIELD)
    private String mMinAge;

    @ColumnInfo(name = GovPensionEntity.MONTHLY_BENEFIT_FIELD)
    public String mMonthlyBenefit;

    public GovPensionIncomeData(long id, int type, String name, long income_type_id, String mMinAge, String mMonthlyBenefit ) {
        super(id, type, name);
        this.income_type_id = income_type_id;
        this.mMinAge = mMinAge;
        this.mMonthlyBenefit = mMonthlyBenefit;
    }

    public long getIncome_type_id() {
        return income_type_id;
    }

    public void setIncome_type_id(long income_type_id) {
        this.income_type_id = income_type_id;
    }

    public String getMinAge() {
        return mMinAge;
    }

    public void setMinAge(String minAge) {
        mMinAge = minAge;
    }

    public String getMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    public void setMonthlyBenefit(String monthlyBenefit) {
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
        return Double.parseDouble(mMonthlyBenefit);
    }

    @Override
    public double getFullMonthlyBenefit() {
        return Double.parseDouble(mMonthlyBenefit);
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

    @Override
    public List<MilestoneData> getMilestones(List<MilestoneAgeEntity> ages, RetirementOptionsData rod) {
        List<MilestoneData> milestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return milestones;
        }

        int birthYear = SystemUtils.getBirthYear(rod.getBirthdate());
        double monthlyBenefit = Double.parseDouble(mMonthlyBenefit);

        AgeData minimumAge = new AgeData(62, 0);

        MilestoneData milestone;
        for(MilestoneAgeEntity msad : ages) {
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

    @Ignore
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
        dest.writeString(mMonthlyBenefit);
        dest.writeParcelable(mRules, flags);
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mMinAge = in.readString();
        mMonthlyBenefit = in.readString();
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
