package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.AmountData;
import com.intelliviz.retirementhelper.data.IncomeTypeRules;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.TaxDeferredData;
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeRules;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */

@Entity(tableName = TABLE_NAME)
public class TaxDeferredIncomeEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "tax_deferred_income";
    public static final String MONTHLY_INCREASE_FIELD = "monthly_increase";
    public static final String MIN_AGE_FIELD = "min_age";
    public static final String IS_401K_FIELD = "is_401k";
    public static final String START_AGE_FIELD = "start_age";

    @ColumnInfo(name = MONTHLY_INCREASE_FIELD)
    private String monthlyIncrease;

    private String penalty;

    private String interest;

    @ColumnInfo(name = MIN_AGE_FIELD)
    private String minAge;

    @ColumnInfo(name = IS_401K_FIELD)
    private int is401k;

    private String balance;

    @ColumnInfo(name = START_AGE_FIELD)
    private String mStartAge;

    @Ignore
    private TaxDeferredIncomeRules mRules;

    public TaxDeferredIncomeEntity(long id, int type, String name, String interest, String monthlyIncrease, String penalty, String minAge, int is401k, String balance, String startAge) {
        super(id, type, name);
        this.interest = interest;
        this.monthlyIncrease = monthlyIncrease;
        this.penalty = penalty;
        this.minAge = minAge = "59 6";
        this.is401k = is401k;
        this.balance = balance;
        this.mStartAge = startAge;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getMonthlyIncrease() {
        return monthlyIncrease;
    }

    public void setMonthlyIncrease(String monthlyAddition) {
        this.monthlyIncrease = monthlyAddition;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public String getMinAge() {
        return minAge;
    }

    public void setMinAge(String minAge) {
        this.minAge = minAge;
    }

    public int getIs401k() {
        return is401k;
    }

    public void setIs401k(int is401k) {
        this.is401k = is401k;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStartAge() {
        return mStartAge;
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof TaxDeferredIncomeRules) {
            mRules = (TaxDeferredIncomeRules)rules;
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
        List<AgeData> ages = new ArrayList<>();
        ages.add(SystemUtils.parseAgeString(minAge));
        return ages;
    }

    public List<AmountData> getMonthlyAmountData() {
        if(mRules != null) {
            return mRules.getMonthlyAmountData();
        } else {
            return null;
        }
    }

    public TaxDeferredData getMonthlyBenefitForAge(AgeData startAge) {
        if(mRules != null) {
            return mRules.getMonthlyBenefitForAge(startAge);
        } else {
            return null;
        }
    }
}
