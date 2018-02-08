package com.intelliviz.retirementhelper.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;
import android.os.Bundle;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.IncomeTypeRules;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;

import java.util.List;

import static com.intelliviz.retirementhelper.db.entity.GovPensionEntity.TABLE_NAME;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_FULL_BENEFIT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_IS_SPOUSE_ENTITY;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SPOUSE_BIRTHDATE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_START_AGE;

/**
 * Created by edm on 10/2/2017.
 */
@Entity(tableName = TABLE_NAME)
public class GovPensionEntity extends IncomeSourceEntityBase {
    public static final String TABLE_NAME = "gov_pension_income";
    private static final String MONTHLY_BENEFIT_FIELD = "full_monthly_benefit";
    private static final String SPOUSE_FIELD = "spouse";
    private static final String SPOUSE_BIRTHDATE_FIELD = "spouse_birthdate";
    private static final String START_AGE_FIELD = "start_age";

    @ColumnInfo(name = MONTHLY_BENEFIT_FIELD)
    private String fullMonthlyBenefit;

    @ColumnInfo(name = SPOUSE_FIELD)
    private int mSpouse;

    @ColumnInfo(name = SPOUSE_BIRTHDATE_FIELD)
    private String mSpouseBirhtdate;

    @TypeConverters({AgeConverter.class})
    @ColumnInfo(name = START_AGE_FIELD)
    private AgeData mStartAge;

    @Ignore
    private SocialSecurityRules mRules;

    public GovPensionEntity(long id, int type, String name, String fullMonthlyBenefit, AgeData startAge,
                            int spouse, String spouseBirhtdate) {
        super(id, type, name);
        this.fullMonthlyBenefit = fullMonthlyBenefit;
        mSpouse = spouse;
        if(spouseBirhtdate == null || spouseBirhtdate.equals("0")) {
            spouseBirhtdate = "13-03-1957";
        }
        mSpouseBirhtdate = spouseBirhtdate;
        mStartAge = startAge;
    }

    public String getFullMonthlyBenefit() {
        return fullMonthlyBenefit;
    }

    public void setFullMonthlyBenefit(String fullMonthlyBenefit) {
        this.fullMonthlyBenefit = fullMonthlyBenefit;
    }

    public int getSpouse() {
        return mSpouse;
    }

    public void setSpouse(int spouse) {
        mSpouse = spouse;
    }

    public String getSpouseBirhtdate() {
        return mSpouseBirhtdate;
    }

    public void setSpouseBirhtdate(String spouseBirhtdate) {
        mSpouseBirhtdate = spouseBirhtdate;
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public void setStartAge(AgeData startAge) {
        mStartAge = startAge;
    }

    public AgeData getFullRetirementAge() {
        return mRules.getFullRetirementAge();
    }

    public AgeData getFullRetirementAge(String birthdate) {
        return mRules.getFullRetirementAge(birthdate);
    }

    public double getSpousalMonthlyBenefit() {
        if(mRules != null) {
            return mRules.getSpousalMonthlyBenefit();
        } else {
            return 0;
        }
    }

    public double getMonthlyBenefit() {
        if(mRules != null) {
            return mRules.getMonthlyBenefit();
        }else {
            return 0;
        }
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof SocialSecurityRules) {
            mRules = (SocialSecurityRules)rules;

            Bundle bundle = new Bundle();

            boolean isSpouseEntity;
            if(mSpouse == 1) {
                isSpouseEntity = true;
            } else {
                isSpouseEntity = false;
            }

            bundle.putDouble(EXTRA_INCOME_FULL_BENEFIT, Double.parseDouble(fullMonthlyBenefit));
            bundle.putParcelable(EXTRA_INCOME_START_AGE, mStartAge);
            bundle.putBoolean(EXTRA_INCOME_IS_SPOUSE_ENTITY, isSpouseEntity);
            if(isSpouseEntity) {
                bundle.putString(EXTRA_INCOME_SPOUSE_BIRTHDATE, mSpouseBirhtdate);
            }
            mRules.setValues(bundle);
        } else {
            mRules = null;
        }
    }

    @Override
    public List<BenefitData> getBenefitData() {
        if(mRules != null) {
            return mRules.getBenefitData();
        } else {
            return null;
        }
    }
}
