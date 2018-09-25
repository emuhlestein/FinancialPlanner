package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.Collections;
import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.SC_GOOD;

/**
 * Created by edm on 10/19/2017.
 */

public class PensionRules implements IncomeTypeRules {
    private int mOwner;
    private AgeData mStartAge;
    private double mMonthlyAmount;
    private RetirementOptions mRO;

    public PensionRules(RetirementOptions ro) {
        mRO = ro;
    }

    /**
     * These parameters should come from PensionData.
     * @param bundle Values to set.
     */
    @Override
    public void setValues(Bundle bundle) {
        mOwner = bundle.getInt(RetirementConstants.EXTRA_INCOME_OWNER);
        String value = bundle.getString(RetirementConstants.EXTRA_INCOME_FULL_BENEFIT);
        mMonthlyAmount = Double.parseDouble(value);
        mStartAge = bundle.getParcelable(RetirementConstants.EXTRA_INCOME_START_AGE);
    }

    @Override
    public List<IncomeData> getIncomeData() {
        return Collections.emptyList();
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new PensionIncomeDataAccessor(mOwner, mStartAge, mMonthlyAmount, SC_GOOD, null, mRO);
    }
}
