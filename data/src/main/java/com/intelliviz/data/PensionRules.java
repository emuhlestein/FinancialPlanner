package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import static com.intelliviz.data.IncomeSummaryHelper.getOwnerAge;
import static com.intelliviz.lowlevel.util.RetirementConstants.SC_GOOD;

/**
 * Created by edm on 10/19/2017.
 */

public class PensionRules implements IncomeTypeRules {
    private int mOwner;
    private AgeData mMinAge;
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
        mMinAge = bundle.getParcelable(RetirementConstants.EXTRA_INCOME_START_AGE);
    }

    public IncomeData getIncomeData(AgeData age) {
        AgeData ownerAge = getOwnerAge(age, mOwner, mRO);
        if(ownerAge.isBefore(mMinAge)) {
            return new IncomeData(age, 0, 0, SC_GOOD, null);
        } else {
            return new IncomeData(age, mMonthlyAmount, 0, SC_GOOD, null);
        }
    }
}
