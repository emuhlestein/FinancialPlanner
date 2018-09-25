package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.lowlevel.util.RetirementConstants.SC_GOOD;
import static com.intelliviz.lowlevel.util.RetirementConstants.SC_SEVERE;
import static com.intelliviz.lowlevel.util.RetirementConstants.SC_WARNING;

/**
 * Created by edm on 10/19/2017.
 */

public class SavingsIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {

    public SavingsIncomeRules(RetirementOptions ro) {
        super(ro);
    }

    @Override
    protected IncomeData createIncomeData(AgeData age, double monthlyAmount, double balance) {
        int state;
        if(balance == 0) {
            state = SC_SEVERE;
        } else if(balance < monthlyAmount * 12) {
            state = SC_WARNING;
        } else {
            state = SC_GOOD;
        }

        return new IncomeData(age, monthlyAmount, balance, state, "");
    }

    @Override
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new SavingIncomeDataAccessor(getOwner(), getIncomeData(), getRetirementOptions());
    }
}
