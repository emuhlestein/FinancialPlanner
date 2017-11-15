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

    @ColumnInfo(name = START_AGE_FIELD)
    private String mStartAge;

    @Ignore
    private SocialSecurityRules mRules;

    public GovPensionEntity(long id, int type, String name, String fullMonthlyBenefit,
                            int spouse, String spouseBenefit, String spouseBirhtdate, String startAge) {
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

    public String getStartAge() {
        return mStartAge;
    }

    public void setStartAge(String startAge) {
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


}
