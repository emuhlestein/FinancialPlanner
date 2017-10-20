package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.IncomeTypeRules;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.PensionRules;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity.TABLE_NAME;
import static com.intelliviz.retirementhelper.util.SystemUtils.parseAgeString;

/**
 * Created by edm on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class PensionIncomeEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "pension_income";
    public static final String MIN_AGE_FIELD = "min_age";
    public static final String MONTHLY_BENEFIT_FIELD = "monthly_benefit";

    @ColumnInfo(name = MIN_AGE_FIELD)
    private String minAge;

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    private String monthlyBenefit;

    @Ignore
    private PensionRules mRules;

    public PensionIncomeEntity(long id, int type, String name, String minAge, String monthlyBenefit) {
        super(id, type, name);
        this.minAge = minAge;
        this.monthlyBenefit = monthlyBenefit;
    }

    public String getMinAge() {
        return minAge;
    }

    public void setMinAge(String minAge) {
        this.minAge = minAge;
    }

    public String getMonthlyBenefit() {
        return monthlyBenefit;
    }

    public void setMonthlyBenefit(String monthlyBenefit) {
        this.monthlyBenefit = monthlyBenefit;
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof PensionRules) {
            mRules = (PensionRules)rules;
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

        AgeData minimumAge = parseAgeString(minAge);
        AgeData endAge = parseAgeString(rod.getEndAge());
        double monthlyBenefit = Double.parseDouble(this.monthlyBenefit);

        MilestoneData milestone;
        for(MilestoneAgeEntity msad : ages) {
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

    @Override
    public List<AgeData> getAges() {
        AgeData ageData = SystemUtils.parseAgeString(minAge);
        List<AgeData> ages = new ArrayList<>();
        ages.add(ageData);
        return ages;
    }
}
