package com.intelliviz.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.db.entity.GovPensionEntity.TABLE_NAME;

/**
 * Database table for government pension income source.
 *
 * Created by Ed Muhlestein on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class GovPensionEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "gov_pension_income";
    private static final String MONTHLY_BENEFIT_FIELD = "full_monthly_benefit";
    private static final String START_AGE_FIELD = "start_age";
    private static final String SPOUSE_FIELD = "spouse";

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    private String mFullMonthlyBenefit;

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = START_AGE_FIELD)
    private AgeData mStartAge;

    @ColumnInfo(name = SPOUSE_FIELD)
    private int mSpouse;

    @Ignore
    public GovPensionEntity(long id, int type) {
        super(id, type, "", 1);
        mFullMonthlyBenefit = "0";
        mStartAge = new AgeData(0);
        mSpouse = 0;
    }

    /**
     * Constructor.
     * @param id Database id.
     * @param type Type of income source.
     * @param name Name of income source.
     * @param fullMonthlyBenefit Monthly benefit when full retirement age is reached.
     * @param startAge The age at which to start receiving benefits.
     * @param spouse 1 if this is a spouse. 0 otherwise.
     */
    public GovPensionEntity(long id, int type, String name, int self, String fullMonthlyBenefit, AgeData startAge, int spouse) {
        super(id, type, name, self);
        mFullMonthlyBenefit = fullMonthlyBenefit;
        mStartAge = startAge;
        mSpouse = spouse;
    }

    /**
     * Get the full monthly benefit.
     * @return The full monthly benefit.
     */
    public String getFullMonthlyBenefit() {
        return mFullMonthlyBenefit;
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public void setStartAge(AgeData startAge) {
        mStartAge = startAge;
    }

    public int getSpouse() {
        return mSpouse;
    }

    public void setSpouse(int spouse) {
        mSpouse = spouse;
    }

}
