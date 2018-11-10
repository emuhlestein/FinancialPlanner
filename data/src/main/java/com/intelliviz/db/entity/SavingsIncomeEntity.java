package com.intelliviz.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import static com.intelliviz.db.entity.SavingsIncomeEntity.TABLE_NAME;


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
    public static final String SHOW_MONTHLY_AMOUNTS = "show_monthly_amounts";

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
    public SavingsIncomeEntity(long id, int type) {
        super(id, type, "", RetirementConstants.OWNER_PRIMARY, 1);
        mStartAge = new AgeData(65, 0); // TODO need to create const
        mBalance = "0";
        mInterest = "0";
        mMonthlyAddition = "0";
        mStopMonthlyAdditionAge = new AgeData(65, 0); // TODO need to create consst
        mWithdrawPercent = "0";
        mAnnualPercentIncrease = "0";
    }

    public SavingsIncomeEntity(long id, int type, String name, int owner, int included, AgeData startAge, String balance, String interest,
                               String monthlyAddition, AgeData stopMonthlyAdditionAge,
                               String withdrawPercent, String annualPercentIncrease) {
        super(id, type, name, owner, included);
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
}
