package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.List;

/**
 * Created by edm on 6/19/2018.
 */

public class GovPension extends AbstractIncomeSource {
    private SocialSecurityRules mRules;
    private boolean mIsPrincipleSpouse;
    private String mFullMonthlyBenefit;
    private AgeData mStartAge;
    private boolean mSpouse;
    private int mState;
    private String mMessage;

    public GovPension(long id, int type) {
        this(id, type, "", 1);
    }

    public GovPension(long id, int type, String name, int self) {
        super(id, type, name, self);
    }

    public GovPension(long id, int type, String name, int self,
        String fullBenefit, AgeData startAge, boolean isSpouse) {
        this(id, type, name, self);
        mFullMonthlyBenefit = fullBenefit;
        mStartAge = startAge;
        mSpouse = isSpouse;
    }

    public AgeData getFullRetirementAge() {
        return mRules.getFullRetirementAge();
    }

    public boolean isPrincipleSpouse() {
        return mIsPrincipleSpouse;
    }

    public void setPrincipleSpouse(boolean principleSpouse) {
        mIsPrincipleSpouse = principleSpouse;
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

    public void setSpouse(boolean spouse) {
        mSpouse = spouse;
    }

    public boolean isSpouse() {
        return mSpouse;
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
            bundle.putString(RetirementConstants.EXTRA_INCOME_FULL_BENEFIT, mFullMonthlyBenefit);
            bundle.putParcelable(RetirementConstants.EXTRA_INCOME_START_AGE, mStartAge);
            mRules.setValues(bundle);
        } else {
            mRules = null;
        }
    }

    public List<IncomeData> getIncomeData() {
        if(mRules != null) {
            return mRules.getIncomeData();
        } else {
            return null;
        }
    }

    public IncomeDataAccessor getIncomeDataAccessor() {
        if(mRules != null) {
            return mRules.getIncomeDataAccessor();
        } else {
            return null;
        }
    }
}
