package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity.TABLE_NAME;
import static java.lang.Double.parseDouble;

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

    @Override
    public List<MilestoneData> getMilestones(List<MilestoneAgeEntity> ages, RetirementOptionsEntity rod) {
        String endAge = rod.getEndAge();
        double withdrawAmount = parseDouble(rod.getWithdrawAmount());
        List<MilestoneData> milestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return milestones;
        }

        AgeData endOfLifeAge = SystemUtils.parseAgeString(endAge);

        double dbalance = Double.parseDouble(balance);
        double dinterest = Double.parseDouble(interest);
        double dmonthlyIncrease = Double.parseDouble(monthlyIncrease);

        List<Double> milestoneBalances = getMilestoneBalances(ages, dbalance, dinterest, dmonthlyIncrease);

        milestones = getMilestones(endOfLifeAge, null, dinterest, 0, rod.getWithdrawMode(), withdrawAmount, ages, milestoneBalances);
        return milestones;
    }

    @Override
    public List<AgeData> getAges() {
        return Collections.emptyList();
    }
}
