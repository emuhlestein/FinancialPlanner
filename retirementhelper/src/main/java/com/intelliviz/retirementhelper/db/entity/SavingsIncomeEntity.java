package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.IncomeTypeRules;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.TaxDeferredData;

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
    public static final String STOP_MONTHLY_ADDITION_AGE_FIELD = "stop_monthly_addition_age";
    public static final String WITHDRAW_PERCENT_FIELD = "withdraw_percent";
    public static final String ANNUAL_PERCENT_INCREASE_FIELD = "annual_percent_increase";

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = START_AGE_FIELD)
    private AgeData mStartAge;

    @ColumnInfo(name = BALANCE_FIELD)
    private String mBalance;

    @ColumnInfo(name = INTEREST_FIELD)
    private String mInterest;

    @ColumnInfo(name = MONTHLY_ADDITION_FIELD)
    private String mMonthlyAddition;

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = STOP_MONTHLY_ADDITION_AGE_FIELD)
    private AgeData mStopMonthlyAdditionAge;

    @ColumnInfo(name = WITHDRAW_PERCENT_FIELD)
    private String mWithdrawPercent;

    @ColumnInfo(name = ANNUAL_PERCENT_INCREASE_FIELD)
    private String mAnnualPercentIncrease;

    @Ignore
    private IncomeTypeRules mRules;

    public SavingsIncomeEntity(long id, int type, String name, AgeData startAge, String balance, String interest,
                               String monthlyAddition, AgeData stopMonthlyAdditionAge,
                               String withdrawPercent, String annualPercentIncrease) {
        super(id, type, name);
        mStartAge = startAge;
        mBalance = balance;
        mInterest = interest;
        mMonthlyAddition = monthlyAddition;
        mStopMonthlyAdditionAge = stopMonthlyAdditionAge;
        mWithdrawPercent = withdrawPercent;
        mAnnualPercentIncrease = annualPercentIncrease;
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public void setStartAge(AgeData startAge) {
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

    public AgeData getStopMonthlyAdditionAge() {
        return mStopMonthlyAdditionAge;
    }

    public void setStopMonthlyAdditionAge(AgeData stopMonthlyAdditionAgeAge) {
        mStopMonthlyAdditionAge = stopMonthlyAdditionAgeAge;
    }

    public String getWithdrawPercent() {
        return mWithdrawPercent;
    }

    public void setWithdrawPercent(String withdrawPercent) {
        mWithdrawPercent = withdrawPercent;
    }

    public String getAnnualPercentIncrease() {
        return mAnnualPercentIncrease;
    }

    public void setAnnualPercentIncrease(String annualPercentIncrease) {
        mAnnualPercentIncrease = annualPercentIncrease;
    }

    public void setRules(IncomeTypeRules rules) {
        mRules = rules;
    }

    public BenefitData getBenefitForAge(AgeData age) {
        if(mRules != null) {
            return mRules.getBenefitForAge(age);
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
        List<AgeData> ages = new ArrayList<>();
        //ages.add(SystemUtils.parseAgeString(minAge));
        return ages;
    }

    @Override
    public List<BenefitData> getBenefitData() {
        if(mRules != null) {
            return mRules.getBenefitData();
        } else {
            return null;
        }
    }

    public BenefitData getBenefitData(BenefitData benefitData) {
        if(mRules != null) {
            return mRules.getBenefitData(benefitData);
        } else {
            return null;
        }
    }

    public TaxDeferredData getMonthlyBenefitForAge(AgeData startAge) {
        if(mRules != null) {
            return null;
        } else {
            return null;
        }
    }
}
