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

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class SavingsIncomeEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "savings_income";
    public static final String BALANCE_FIELD = "balance";
    public static final String INTEREST_FIELD = "interest";
    public static final String MONTHLY_ADDITION_FIELD = "monthly_addition";
    public static final String START_AGE_FIELD = "start_age";
    public static final String SAVINGS_TYPE_FIELD = "savings_type";

    @ColumnInfo(name = BALANCE_FIELD)
    private String mBalance;

    @ColumnInfo(name = INTEREST_FIELD)
    private String mInterest;

    @ColumnInfo(name = MONTHLY_ADDITION_FIELD)
    private String mMonthlyAddition;

    @ColumnInfo(name = START_AGE_FIELD)
    private String mStartAge;

    @Ignore
    private TaxDeferredIncomeRules mRules;

    public SavingsIncomeEntity(long id, int type, String name, String balance, String interest, String monthlyAddition, String startAge) {
        super(id, type, name);
        mBalance = balance;
        mInterest = interest;
        mMonthlyAddition = monthlyAddition;
        mStartAge = startAge;
    }

    public String getBalance() {
        return mBalance;
    }

    public void setBalance(String balance) {
        mBalance = balance;
    }

    public String getInterest() {
        return mInterest;
    }

    public void setInterest(String interest) {
        mInterest = interest;
    }

    public String getMonthlyAddition() {
        return mMonthlyAddition;
    }

    public void setMonthlyAddition(String monthlyAddition) {
        mMonthlyAddition = monthlyAddition;
    }

    public String getStartAge() {
        return mStartAge;
    }

    public void setStartAge(String startAge) {
        mStartAge = startAge;
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
        //ages.add(SystemUtils.parseAgeString(minAge));
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
