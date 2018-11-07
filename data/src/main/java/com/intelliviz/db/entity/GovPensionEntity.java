package com.intelliviz.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.db.entity.GovPensionEntity.TABLE_NAME;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_PRIMARY;

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

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    private String mFullMonthlyBenefit;

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = START_AGE_FIELD)
    private AgeData mStartAge;

    @Ignore
    public GovPensionEntity(long id, int type) {
        super(id, type, "", OWNER_PRIMARY, 1);
        mFullMonthlyBenefit = "0";
        mStartAge = new AgeData(0);
    }

    /**
     * Constructor.
     * @param id Database id.
     * @param type Type of income source.
     * @param name Name of income source.
     * @param fullMonthlyBenefit Monthly benefit when full retirement age is reached.
     * @param startAge The age at which to start receiving benefits.
     */
    public GovPensionEntity(long id, int type, String name, int owner, int included, String fullMonthlyBenefit, AgeData startAge) {
        super(id, type, name, owner, included);
        mFullMonthlyBenefit = fullMonthlyBenefit;
        mStartAge = startAge;
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
}
