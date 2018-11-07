package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

/**
 * Created by edm on 11/21/2017.
 */

public class PensionData extends AbstractIncomeSource {
    private AgeData mStartAge;
    private String mMonthlyBenefit;
    private PensionRules mRules;

    public PensionData(long id, int type) {
        this(id, type, "", RetirementConstants.OWNER_PRIMARY, 1);
    }

    public PensionData(long id, int type, String name, int owner, int included) {
        super(id, type, name, owner, included);
    }

    public PensionData(long id, int type, String name, int owner, int included,
                       AgeData startAge, String monthlyBenefit) {
        this(id, type, name, owner, included);
        mStartAge = startAge;
        mMonthlyBenefit = monthlyBenefit;
    }

    public PensionData(int owner, AgeData startAge, String monthlyBenefit) {
        this(-1, 0, "", owner, 1);
        mStartAge = startAge;
        mMonthlyBenefit = monthlyBenefit;
    }

    public void setStartAge(AgeData startAge) {
        mStartAge = startAge;
    }

    public void setMonthlyBenefit(String monthlyBenefit) {
        mMonthlyBenefit = monthlyBenefit;
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public String getMonthlyBenefit() {
        return mMonthlyBenefit;
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof PensionRules) {
            mRules = (PensionRules)rules;
            Bundle bundle = new Bundle();
            bundle.putInt(RetirementConstants.EXTRA_INCOME_OWNER, getOwner());
            bundle.putString(RetirementConstants.EXTRA_INCOME_FULL_BENEFIT, mMonthlyBenefit);
            bundle.putParcelable(RetirementConstants.EXTRA_INCOME_START_AGE, mStartAge);
            mRules.setValues(bundle);
        } else {
            mRules = null;
        }
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
       return mRules.getIncomeData(age);
    }
}
