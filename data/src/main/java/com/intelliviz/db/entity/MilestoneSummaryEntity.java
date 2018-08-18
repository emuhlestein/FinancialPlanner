package com.intelliviz.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.db.entity.MilestoneSummaryEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class MilestoneSummaryEntity {
    public static final String TABLE_NAME = "milestone_summary_income";
    public static final String MONTHLY_BENEFIT_FIELD = "monthly_benefit";
    public static final String START_AGE_FIELD = "start_age";
    public static final String END_AGE_FIELD = "end_age";
    public static final String MIN_AGE_FIELD = "min_age";
    public static final String START_BALANCE_FIELD = "start_balance";
    public static final String END_BALANCE_FIELD = "end_balance";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    private String monthlyBenefit;

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = START_AGE_FIELD)
    private AgeData startAge;

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = END_AGE_FIELD)
    private AgeData endAge;

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = MIN_AGE_FIELD)
    private AgeData minAge;

    @ColumnInfo(name = START_BALANCE_FIELD)
    private String startBalance;

    @ColumnInfo(name = END_BALANCE_FIELD)
    private String endBalance;

    private String penalty;

    private int months;

    public MilestoneSummaryEntity(int id, String monthlyBenefit, AgeData startAge, AgeData endAge, AgeData minAge, String startBalance, String endBalance, String penalty, int months) {
        this.id = id;
        this.monthlyBenefit = monthlyBenefit;
        this.startAge = startAge;
        this.endAge = endAge;
        this.minAge = minAge;
        this.startBalance = startBalance;
        this.endBalance = endBalance;
        this.penalty = penalty;
        this.months = months;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMonthlyBenefit() {
        return monthlyBenefit;
    }

    public void setMonthlyBenefit(String monthlyBenefit) {
        this.monthlyBenefit = monthlyBenefit;
    }

    public AgeData getStartAge() {
        return startAge;
    }

    public void setStartAge(AgeData startAge) {
        this.startAge = startAge;
    }

    public AgeData getEndAge() {
        return endAge;
    }

    public void setEndAge(AgeData endAge) {
        this.endAge = endAge;
    }

    public AgeData getMinAge() {
        return minAge;
    }

    public void setMinAge(AgeData minAge) {
        this.minAge = minAge;
    }

    public String getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(String startBalance) {
        this.startBalance = startBalance;
    }

    public String getEndBalance() {
        return endBalance;
    }

    public void setEndBalance(String endBalance) {
        this.endBalance = endBalance;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }
}
