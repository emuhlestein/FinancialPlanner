package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.IncomeTypeRules;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.SavingsIncomeRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class SavingsIncomeEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "savings_income";
    public static final String MONTHLY_INCREASE_FIELD = "monthly_increase";

    private String interest;

    @ColumnInfo(name = MONTHLY_INCREASE_FIELD)
    private String monthlyIncrease;

    private String balance;

    @Ignore
    private SavingsIncomeRules mRules;

    public SavingsIncomeEntity(long id, int type, String name, String interest, String monthlyIncrease, String balance) {
        super(id, type, name);
        this.interest = interest;
        this.monthlyIncrease = monthlyIncrease;
        this.balance = balance;
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

    public void setMonthlyIncrease(String monthlyIncrease) {
        this.monthlyIncrease = monthlyIncrease;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof SavingsIncomeRules) {
            mRules = (SavingsIncomeRules)rules;
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
        return Collections.emptyList();
    }
}
