package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity.TABLE_NAME;

/**
 * Created by edm on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME,
        foreignKeys = @ForeignKey(entity = IncomeTypeEntity.class,
                                  parentColumns = "id",
                                   childColumns = "income_type_id"))
public class SavingsIncomeEntity {
    public static final String TABLE_NAME = "savings_income";
    public static final String INCOME_TYPE_ID_FIELD = "income_type_id";
    public static final String MONTHLY_ADDITION_FIELD = "monthly_addition";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = INCOME_TYPE_ID_FIELD)
    private int incomeTypeId;

    private String interest;

    @ColumnInfo(name = MONTHLY_ADDITION_FIELD)
    private String monthlyAddition;

    private String balance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIncomeTypeId() {
        return incomeTypeId;
    }

    public void setIncomeTypeId(int incomeTypeId) {
        this.incomeTypeId = incomeTypeId;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getMonthlyAddition() {
        return monthlyAddition;
    }

    public void setMonthlyAddition(String monthlyAddition) {
        this.monthlyAddition = monthlyAddition;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
