package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity.TABLE_NAME;
import static java.lang.Double.parseDouble;

/**
 * Created by edm on 10/2/2017.
 */

@Entity(tableName = TABLE_NAME)
public class TaxDeferredIncomeEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "tax_deferred_income";
    public static final String MONTHLY_INCREASE_FIELD = "monthly_increase";
    public static final String MIN_AGE_FIELD = "min_age";
    public static final String IS_401K_FIELD = "is_401k";

    @ColumnInfo(name = MONTHLY_INCREASE_FIELD)
    private String monthlyIncrease;

    private String penalty;

    private String interest;

    @ColumnInfo(name = MIN_AGE_FIELD)
    private String minAge;

    @ColumnInfo(name = IS_401K_FIELD)
    private int is401k;

    private String balance;

    public TaxDeferredIncomeEntity(long id, int type, String name, String interest, String monthlyIncrease, String penalty, String minAge, int is401k, String balance) {
        super(id, type, name);
        this.interest = interest;
        this.monthlyIncrease = monthlyIncrease;
        this.penalty = penalty;
        this.minAge = minAge;
        this.is401k = is401k;
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

    @Override
    public List<MilestoneData> getMilestones(List<MilestoneAgeEntity> ages, RetirementOptionsEntity rod) {
        String endAge = rod.getEndAge();
        double withdrawAmount = parseDouble(rod.getWithdrawAmount());
        List<MilestoneData> milestones = new ArrayList<>();
        if(ages.isEmpty()) {
            return milestones;
        }
        AgeData minimumAge = SystemUtils.parseAgeString(minAge);
        AgeData endOfLifeAge = SystemUtils.parseAgeString(endAge);

        double dbalance = Double.parseDouble(balance);
        double dinterest = Double.parseDouble(interest);
        double dmonthlyIncrease = Double.parseDouble(monthlyIncrease);
        double dpenalty = Double.parseDouble(penalty);

        List<Double> milestoneBalances = getMilestoneBalances(ages, dbalance, dinterest, dmonthlyIncrease);

        milestones = getMilestones(endOfLifeAge, minimumAge, dinterest, dpenalty, rod.getWithdrawMode(), withdrawAmount, ages, milestoneBalances);
        return milestones;
    }

    @Override
    public List<AgeData> getAges() {
        List<AgeData> ages = new ArrayList<>();
        ages.add(SystemUtils.parseAgeString(minAge));
        return ages;
    }
}
