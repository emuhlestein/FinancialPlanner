package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.retirementhelper.data.AgeData;

import static com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */

@Entity(tableName = TABLE_NAME)
public class RetirementOptionsEntity {
    public static final String TABLE_NAME = "retirement_options";
    public static final String END_AGE_FIELD = "end_age";
    public static final String CURRENT_OPTION_FIELD = "current_option";
    public static final String REACH_AMOUNT_FIELD = "reach_amount";
    public static final String REACH_PERCENT_FIELD = "reach_percent";
    public static final String BIRTHDATE_FIELD = "birthdate";
    public static final String MONTHLY_INCOME_FIELD = "monthly_income";

    @PrimaryKey(autoGenerate = true)
    private int id;
    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = END_AGE_FIELD)
    private AgeData endAge;
    @ColumnInfo(name = CURRENT_OPTION_FIELD)
    private int currentOption;
    @ColumnInfo(name = REACH_AMOUNT_FIELD)
    private String reachAmount;
    @ColumnInfo(name = REACH_PERCENT_FIELD)
    private String reachPercent;
    @ColumnInfo(name = BIRTHDATE_FIELD)
    private String birthdate;
    @ColumnInfo(name = MONTHLY_INCOME_FIELD)
    private String monthlyIncome;

    public RetirementOptionsEntity(int id, AgeData endAge, int currentOption, String birthdate) {
        this.id = id;
        this.endAge = endAge;
        this.currentOption = currentOption;
        this.birthdate = birthdate;
        this.reachAmount = "0";
        this.reachPercent = "0";
        this.monthlyIncome = "0";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AgeData getEndAge() {
        return endAge;
    }

    public void setEndAge(AgeData endAge) {
        this.endAge = endAge;
    }

    public int getCurrentOption() {
        return currentOption;
    }

    public void setCurrentOption(int currentOption) {
        this.currentOption = currentOption;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getReachAmount() {
        return reachAmount;
    }

    public void setReachAmount(String reachAmount) {
        this.reachAmount = reachAmount;
    }

    public String getReachPercent() {
        return reachPercent;
    }

    public void setReachPercent(String reachPercent) {
        this.reachPercent = reachPercent;
    }

    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(String monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }
}
