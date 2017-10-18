package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.IncomeTypeRules;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.GovPensionEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class GovPensionEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "gov_pension_income";
    public static final String MIN_AGE_FIELD = "min_age";
    public static final String MONTHLY_BENEFIT_FIELD = "full_monthly_benefit";

    @ColumnInfo(name = MIN_AGE_FIELD)
    private String minAge;

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    private String fullMonthlyBenefit;

    @Ignore
    private SocialSecurityRules mRules;

    public GovPensionEntity(long id, int type, String name, String minAge, String fullMonthlyBenefit) {
        super(id, type, name);
        this.minAge = minAge;
        this.fullMonthlyBenefit = fullMonthlyBenefit;
    }

    public String getMinAge() {
        return minAge;
    }

    public void setMinAge(String minAge) {
        this.minAge = minAge;
    }

    public String getFullMonthlyBenefit() {
        return fullMonthlyBenefit;
    }

    public void setFullMonthlyBenefit(String fullMonthlyBenefit) {
        this.fullMonthlyBenefit = fullMonthlyBenefit;
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof SocialSecurityRules) {
            mRules = (SocialSecurityRules)rules;
        } else {
            mRules = null;
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
                AgeData minimumAge = mRules.getMinimumAge();
                double monthlyBenefit = mRules.getMonthlyBenefitForAge(age);
                milestone = new MilestoneData(age, null, minimumAge, monthlyBenefit, 0, 0, 0, 0);
                milestones.add(milestone);
            }
        }
        return milestones;
    }

    @Override
    public List<AgeData> getAges() {
        List<AgeData> ages = new ArrayList<>();
        if(mRules != null) {
            ages.add(mRules.getFullRetirementAge());
            ages.add(mRules.getMinimumAge());
        }
        return ages;
    }
}
