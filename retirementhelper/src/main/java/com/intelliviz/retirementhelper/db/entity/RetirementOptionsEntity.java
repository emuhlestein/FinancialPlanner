package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = END_AGE_FIELD)
    private String endAge;
    @ColumnInfo(name = WITHDRAW_MODE_FIELD)
    private int withdrawMode;
    @ColumnInfo(name = WITHDRAW_AMOUNT_FIELD)
    private String withdrawAmount;
    private String birthdate;

    public RetirementOptionsEntity(int id, String endAge, int withdrawMode, String withdrawAmount, String birthdate) {
        this.id = id;
        this.endAge = endAge;
        this.withdrawMode = withdrawMode;
        this.withdrawAmount = withdrawAmount;
        this.birthdate = birthdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndAge() {
        return endAge;
    }

    public void setEndAge(String endAge) {
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

    public void setWithdrawAmount(String withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
