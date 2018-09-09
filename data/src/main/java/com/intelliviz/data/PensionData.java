package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.Collections;
import java.util.List;

/**
 * Created by edm on 11/21/2017.
 */

public class PensionData extends AbstractIncomeSource {
    private AgeData mStartAge;
    private String mMonthlyBenefit;
    private int mBenefitInfo;
    private PensionRules mRules;

    public PensionData(long id, int type) {
        this(id, type, "", RetirementConstants.OWNER_SELF);
    }

    public PensionData(long id, int type, String name, int self) {
        super(id, type, name, self);
    }

    public PensionData(long id, int type, String name, int self,
                       AgeData age, String monthlyBenefit, int benefitInfo) {
        this(id, type, name, self);
        mStartAge = age;
        mMonthlyBenefit = monthlyBenefit;
        mBenefitInfo = benefitInfo;
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

    public int getBenefitInfo() {
        return mBenefitInfo;
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
    public List<IncomeData> getIncomeData() {
        if(mRules != null) {
            return mRules.getIncomeData();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        if(mRules != null) {
            return mRules.getIncomeDataAccessor();
        } else {
            return null;
        }
    }
}
