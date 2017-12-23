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
    public static final String WITHDRAW_MODE_FIELD = "withdraw_mode";
    public static final String WITHDRAW_AMOUNT_FIELD = "withdraw_amount";
    public static final String WITHDRAW_PERCENT_FIELD = "withdraw_percent";
    public static final String BIRTHDATE_FIELD = "birthdate";
    public static final String PERCENT_INCREASE_FIELD = "percent_increase";

    @PrimaryKey(autoGenerate = true)
    private int id;
    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = END_AGE_FIELD)
    private AgeData endAge;
    @ColumnInfo(name = WITHDRAW_MODE_FIELD)
    private int withdrawMode;
    @ColumnInfo(name = WITHDRAW_AMOUNT_FIELD)
    private String withdrawAmount;
    @ColumnInfo(name = WITHDRAW_PERCENT_FIELD)
    private String withdrawPercent;
    @ColumnInfo(name = BIRTHDATE_FIELD)
    private String birthdate;
    @ColumnInfo(name = PERCENT_INCREASE_FIELD)
    private String percentIncrease;

    public RetirementOptionsEntity(int id, AgeData endAge, int withdrawMode, String withdrawAmount, String birthdate, String percentIncrease) {
        this.id = id;
        this.endAge = endAge;
        this.withdrawMode = withdrawMode;
        this.withdrawAmount = withdrawAmount;
        this.birthdate = birthdate;
        this.percentIncrease = percentIncrease;
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

    public int getWithdrawMode() {
        return withdrawMode;
    }

    public void setWithdrawMode(int withdrawMode) {
        this.withdrawMode = withdrawMode;
    }

    public String getWithdrawAmount() {
        return withdrawAmount;
    }

    public String getWithdrawPercent() {
        return withdrawPercent;
    }

    public void setWithdrawPercent(String withdrawPercent) {
        this.withdrawPercent = withdrawPercent;
    }

    public void setWithdrawAmount(String withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPercentIncrease() {
        return percentIncrease;
    }

    public void setPercentIncrease(String percentIncrease) {
        this.percentIncrease = percentIncrease;
    }
}
