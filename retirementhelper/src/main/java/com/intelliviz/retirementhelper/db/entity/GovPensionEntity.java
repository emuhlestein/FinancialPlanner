package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;
import android.os.Bundle;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.GovPensionData;
import com.intelliviz.retirementhelper.data.IncomeTypeRules;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.GovPensionEntity.TABLE_NAME;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_FULL_BENEFIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_INCLUDE_SPOUSE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SPOUSE_BENEFIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_START_AGE;

/**
 * Created by edm on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class GovPensionEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "gov_pension_income";
    private static final String MONTHLY_BENEFIT_FIELD = "full_monthly_benefit";
    private static final String SPOUSE_FIELD = "spouse";
    private static final String SPOUSE_BENEFIT_FIELD = "spouse_benefit";
    private static final String SPOUSE_BIRTHDATE_FIELD = "spouse_birthdate";
    private static final String START_AGE_FIELD = "start_age";

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    private String fullMonthlyBenefit;

    @ColumnInfo(name = SPOUSE_FIELD)
    private int mSpouse;

    @ColumnInfo(name = SPOUSE_BENEFIT_FIELD)
    private String mSpouseBenefit;

    @ColumnInfo(name = SPOUSE_BIRTHDATE_FIELD)
    private String mSpouseBirhtdate;

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = START_AGE_FIELD)
    private AgeData mStartAge;

    @Ignore
    private SocialSecurityRules mRules;

    public GovPensionEntity(long id, int type, String name, String fullMonthlyBenefit,
                            int spouse, String spouseBenefit, String spouseBirhtdate, AgeData startAge) {
        super(id, type, name);
        this.fullMonthlyBenefit = fullMonthlyBenefit;
        mSpouse = spouse;
        mSpouseBenefit = spouseBenefit;
        mSpouseBirhtdate = spouseBirhtdate;
        mStartAge = startAge;
    }

    public String getFullMonthlyBenefit() {
        return fullMonthlyBenefit;
    }

    public void setFullMonthlyBenefit(String fullMonthlyBenefit) {
        this.fullMonthlyBenefit = fullMonthlyBenefit;
    }

    public int getSpouse() {
        return mSpouse;
    }

    public void setSpouse(int spouse) {
        mSpouse = spouse;
    }

    public String getSpouseBenefit() {
        return mSpouseBenefit;
    }

    public void setSpouseBenefit(String spouseBenefit) {
        mSpouseBenefit = spouseBenefit;
    }

    public String getSpouseBirhtdate() {
        return mSpouseBirhtdate;
    }

    public void setSpouseBirhtdate(String spouseBirhtdate) {
        mSpouseBirhtdate = spouseBirhtdate;
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public void setStartAge(AgeData startAge) {
        mStartAge = startAge;
    }

    public AgeData getFullRetirementAge() {
        return mRules.getFullRetirementAge();
    }

    public AgeData getFullRetirementAge(String birthdate) {
        return mRules.getFullRetirementAge(birthdate);
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof SocialSecurityRules) {
            mRules = (SocialSecurityRules)rules;

            Bundle bundle = new Bundle();
            bundle.putDouble(EXTRA_INCOME_FULL_BENEFIT, Double.parseDouble(fullMonthlyBenefit));
            bundle.putParcelable(EXTRA_INCOME_START_AGE, mStartAge);
            bundle.putBoolean(EXTRA_INCOME_INCLUDE_SPOUSE, mSpouse == 1);
            bundle.putDouble(EXTRA_INCOME_SPOUSE_BENEFIT, Double.parseDouble(mSpouseBenefit));
            bundle.putString(EXTRA_INCOME_SPOUSE_BIRTHDATE, mSpouseBirhtdate);
            mRules.setValues(bundle);
        } else {
            mRules = null;
        }
    }

    public BenefitData getBenefitForAge(AgeData age) {
        if(mRules != null) {
            return mRules.getBenefitForAge(age);
        } else {
            return null;
        }
    }

    @Override
    public List<BenefitData> getBenefitData() {
        if(mRules != null) {
            return mRules.getBenefitData();
        } else {
            return null;
        }
    }

    @Override
    public List<MilestoneData> getMilestones(List<MilestoneAgeEntity> ages, RetirementOptionsEntity rod) {
        List<MilestoneData> milestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return milestones;
        }

        MilestoneData milestone;
        for(MilestoneAgeEntity msad : ages) {
            AgeData age = msad.getAge();
            if(mRules != null) {
                milestone = mRules.getMilestone(age);
                milestones.add(milestone);
            }
        }
        return milestones;
    }

    @Override
    public List<AgeData> getAges() {
        return mRules.getAges();
    }

    public GovPensionData getMonthlyBenefitForAge(AgeData startAge) {
        if(mRules != null) {
            return mRules.getMonthlyBenefitForAge(startAge);
        } else {
            return null;
        }
    }
}
