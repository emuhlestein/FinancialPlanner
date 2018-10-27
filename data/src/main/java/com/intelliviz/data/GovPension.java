package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

/**
 * Created by edm on 6/19/2018.
 */

public class GovPension extends AbstractIncomeSource {
    private SocialSecurityRules mRules;
    private String mFullMonthlyBenefit;
    private AgeData mStartAge;
    private int mState;
    private String mMessage;

    public GovPension(long id, int type, String name, int owner) {
        super(id, type, name, owner);
    }

    public GovPension(long id, int type) {
        this(id, type, "", RetirementConstants.OWNER_PRIMARY);
    }

    public GovPension(long id, int type, String name, int owner,
        String fullBenefit, AgeData startAge) {
        this(id, type, name, owner);
        mFullMonthlyBenefit = fullBenefit;
        mStartAge = startAge;
    }

    public AgeData getFullRetirementAge() {
        return mRules.getFullRetirementAge();
    }

    public void setFullMonthlyBenefit(String fullMonthlyBenefit) {
        mFullMonthlyBenefit = fullMonthlyBenefit;
    }

    public String getFullMonthlyBenefit() {
        return mFullMonthlyBenefit;
    }

    public void setStartAge(AgeData startAge) {
        mStartAge = startAge;
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public double getMonthlyBenefit() {
        if(mRules != null) {
            return mRules.getMonthlyBenefit();
        }else {
            return 0;
        }
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public AgeData getActualStartAge() {
        if(mRules != null) {
            return mRules.getActualStartAge();
        } else {
            return null;
        }
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof SocialSecurityRules) {
            mRules = (SocialSecurityRules)rules;
            Bundle bundle = new Bundle();
            bundle.putInt(RetirementConstants.EXTRA_INCOME_OWNER, getOwner());
            bundle.putString(RetirementConstants.EXTRA_INCOME_FULL_BENEFIT, mFullMonthlyBenefit);
            bundle.putParcelable(RetirementConstants.EXTRA_INCOME_START_AGE, mStartAge);
            mRules.setValues(bundle);
        } else {
            mRules = null;
        }
    }

    @Override
    public IncomeData getIncomeData() {
        if(mRules != null) {
            return mRules.getIncomeData();
        } else {
            return null;
        }
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        return mRules.getIncomeData(age);
    }

    @Override
    public IncomeData getIncomeData(IncomeData incomeData) {
        return null;
    }
}
